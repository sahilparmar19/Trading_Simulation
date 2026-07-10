package display;

import auth.Session;
import db.DatabaseManager;
import ds.CustomLinkedList;
import engine.OrderBook;
import model.Order;
import model.Stock;

import java.sql.Timestamp;
import java.util.Scanner;

public class WatchlistView {

    public static void render(Scanner scanner) {
        if (!Session.isLoggedIn()) {
            System.out.println("Please log in first.");
            return;
        }

        int userId = Session.getCurrentUser().getUserId();

        while (true) {
            System.out.println("\n=================================================================");
            System.out.println("                         MY WATCHLIST");
            System.out.println("=================================================================");
            CustomLinkedList<Stock> watchlist = DatabaseManager.getWatchlist(userId);

            if (watchlist.size() == 0) {
                System.out.println(" Your watchlist is currently empty.");
                System.out.println("-----------------------------------------------------------------");
            } else {
                System.out.printf("%-10s | %-32s | %-12s%n", "Ticker", "Company Name", "Current Price");
                System.out.println("-----------------------------------------------------------------");
                for (Stock stock : watchlist) {
                    System.out.printf("%-10s | %-32s | ₹%-12.2f%n",
                            stock.getTicker(), stock.getCompanyName(), stock.getCurrentPrice());
                }
                System.out.println("=================================================================");
            }

            System.out.println(" [1] Add Stock to Watchlist");
            System.out.println(" [2] Remove Stock from Watchlist");
            System.out.println(" [3] Quick Action on Stock (Buy / Sell / History)");
            System.out.println(" [4] Go Back");
            System.out.print(" Choose option: ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("4")) {
                break;
            }

            switch (choice) {
                case "1":
                    System.out.print(" Enter stock ticker to add: ");
                    String addTicker = scanner.nextLine().trim().toUpperCase();
                    Stock addStock = DatabaseManager.getStock(addTicker);
                    if (addStock != null) {
                        DatabaseManager.addToWatchlist(userId, addTicker);
                        System.out.println(addTicker + " added to your watchlist.");
                    } else {
                        System.out.println("Stock not found with ticker: " + addTicker);
                    }
                    break;
                case "2":
                    System.out.print(" Enter stock ticker to remove: ");
                    String removeTicker = scanner.nextLine().trim().toUpperCase();
                    DatabaseManager.removeFromWatchlist(userId, removeTicker);
                    System.out.println(removeTicker + " removed from your watchlist.");
                    break;
                case "3":
                    System.out.print(" Enter stock ticker: ");
                    String ticker = scanner.nextLine().trim().toUpperCase();
                    Stock stock = DatabaseManager.getStock(ticker);
                    if (stock == null) {
                        System.out.println("Stock not found with ticker: " + ticker);
                        break;
                    }
                    performQuickAction(stock, scanner);
                    break;
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        }
    }

    private static void performQuickAction(Stock stock, Scanner scanner) {
        System.out.print(" Choose action - [B]uy, [S]ell, [H]istory: ");
        String action = scanner.nextLine().trim().toUpperCase();

        if (action.equals("H")) {
            StockDetailView.render(stock.getTicker(), scanner);
            return;
        }

        if (!action.equals("B") && !action.equals("S")) {
            System.out.println("Invalid action.");
            return;
        }

        boolean isBuy = action.equals("B");

        System.out.print(" Enter Quantity: ");
        int qty;
        try {
            qty = Integer.parseInt(scanner.nextLine().trim());
            if (qty <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity.");
            return;
        }

        if (!isBuy) {
            CustomLinkedList<DatabaseManager.PortfolioHolding> portfolio = DatabaseManager.getPortfolio(Session.getCurrentUser().getUserId());
            int ownedQty = 0;
            for (DatabaseManager.PortfolioHolding holding : portfolio) {
                if (holding.ticker.equalsIgnoreCase(stock.getTicker())) {
                    ownedQty = holding.quantity;
                    break;
                }
            }
            if (ownedQty < qty) {
                System.out.println("You do not hold enough shares of " + stock.getTicker() + " to sell. Owned: " + ownedQty + ", Requested: " + qty);
                return;
            }
        }


        System.out.println(" Order Type:");
        System.out.println("  [1] LIMIT Order");
        System.out.println("  [2] MARKET Order");
        System.out.print(" Select: ");
        String typeChoice = scanner.nextLine().trim();

        String type = "LIMIT";
        double price = 0.0;

        if (typeChoice.equals("1")) {
            type = "LIMIT";
            System.out.print(" Enter Limit Price (INR): ");
            try {
                price = Double.parseDouble(scanner.nextLine().trim());
                if (price <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                System.out.println("Invalid price.");
                return;
            }
        } else if (typeChoice.equals("2")) {
            type = "MARKET";
            // For market orders in limit book:
            // Buy: set price to very high so it matches lowest ask
            // Sell: set price to 0.0 so it matches highest bid
            price = isBuy ? 9999999.99 : 0.0;
        } else {
            System.out.println("Invalid option.");
            return;
        }

        // Place order
        Order order = new Order(
            0,
            Session.getCurrentUser().getUserId(),
            stock.getTicker(),
            isBuy,
            type,
            price,
            qty,
            0.0,
            new Timestamp(System.currentTimeMillis()),
            "PENDING"
        );

        int orderId = DatabaseManager.insertOrder(order);
        if (orderId != -1) {
            OrderBook book = OrderBook.get(stock.getTicker());
            synchronized (book) {
                if (isBuy) {
                    book.getBuySide().insert(order);
                } else {
                    book.getSellSide().insert(order);
                }
            }
            System.out.println("Order placed successfully! Order ID: " + orderId);
        } else {
            System.out.println("Failed to place order.");
        }
    }
}
