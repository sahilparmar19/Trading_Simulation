package engine;

import auth.AuthManager;
import db.DatabaseManager;
import model.Order;
import model.OrderStatus;
import model.OrderType;
import model.Stock;
import ds.CustomLinkedList;
import service.AlphaVantageService; // [AlphaVantage] import the new service

import java.sql.Timestamp;
import java.util.Random;

public class BotTrader extends Thread {
    private final String botUsername;
    private int botUserId = -1;
    private boolean running = true;
    private final Random random = new Random();

    // [AlphaVantage] Shared service instance — created once per BotTrader.
    // AlphaVantageService is thread-safe; HttpClient is reused across calls.
    private final AlphaVantageService alphaVantageService;

    public BotTrader(String botUsername) {
        this.botUsername = botUsername;
        this.setDaemon(true);
        // [AlphaVantage] Initialise the service (reads API key from config.properties)
        this.alphaVantageService = new AlphaVantageService();
    }

    public void shutdown() {
        running = false;
    }

    private void ensureBotUserRegistered() {
        // Try logging in, if null then sign up
        model.User user = AuthManager.login(botUsername, "BotPassword123");
        if (user == null) {
            AuthManager.signUp(botUsername, "BotPassword123", botUsername.toUpperCase());
            user = AuthManager.login(botUsername, "BotPassword123");
        }
        if (user != null) {
            this.botUserId = user.getUserId();
            // Give bots some initial balance if they run low
            if (user.getBalance() < 20000.00) {
                DatabaseManager.updateBalance(this.botUserId, 100000.00);
            }
        }
    }

    @Override
    public void run() {
        ensureBotUserRegistered();
        if (botUserId == -1) {
            System.err.println("Could not register or authenticate bot: " + botUsername);
            return;
        }

        // Available tickers
        String[] defaultTickers = {"RELIANCE", "TCS", "HDFCBANK", "TATAMOTORS", "SUNPHARMA"};

        while (running) {
            try {
                // Sleep for random 2-5 seconds
                int sleepSeconds = 2 + random.nextInt(4); // 2, 3, 4, 5
                Thread.sleep(sleepSeconds * 1000L);

                // Choose a random ticker
                String ticker = defaultTickers[random.nextInt(defaultTickers.length)];
                Stock stock = DatabaseManager.getStock(ticker);
                if (stock == null || !stock.isListed()) {
                    continue; // Skip if stock is delisted or not found
                }

                double currentPrice = stock.getCurrentPrice();

                // [AlphaVantage] Attempt to fetch a live price for this ticker.
                // getCurrentPrice() returns -1.0 on any failure (network error,
                // rate limit, parse error) — we check for that and fall back to
                // the original random ±2% deviation so the bot keeps trading.
                double livePrice = alphaVantageService.getCurrentPrice(ticker);
                double price;
                if (livePrice > 0) {
                    // Live price obtained — use it directly as the order price
                    price = livePrice;
                } else {
                    // Fallback: original random ±2% deviation around the DB price
                    double percent = (random.nextDouble() * 4.0) - 2.0; // -2.0% to +2.0%
                    price = currentPrice * (1.0 + (percent / 100.0));
                }
                // Round to 2 decimal places (same as before)
                price = Math.round(price * 100.0) / 100.0;

                boolean isBuy = random.nextBoolean();
                int qty = 1 + random.nextInt(15); // 1 to 15 shares

                // Create Order POJO (LIMIT order)
                Order order = new Order(
                    0, 
                    botUserId, 
                    ticker, 
                    isBuy, 
                    OrderType.LIMIT,
                    price, 
                    qty, 
                    0.0, 
                    new Timestamp(System.currentTimeMillis()), 
                    OrderStatus.PENDING
                );

                // Insert into DB to get order ID
                int orderId = DatabaseManager.insertOrder(order);
                if (orderId != -1) {
                    // Put in local OrderBook BST
                    OrderBook book = OrderBook.get(ticker);
                    synchronized (book) {
                        if (isBuy) {
                            book.getBuySide().insert(order);
                        } else {
                            book.getSellSide().insert(order);
                        }
                    }
                }
            } catch (InterruptedException e) {
                break;
            } catch (Exception e) {
                System.err.println("Error in BotTrader " + botUsername + ": " + e.getMessage());
            }
        }
    }
}
