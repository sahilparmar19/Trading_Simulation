package engine;

import db.DatabaseManager;
import db.TradeTransaction;
import ds.CustomLinkedList;
import io.IOManager;
import model.Order;
import model.OrderStatus;
import model.Trade;

import java.sql.Timestamp;

public class MatchingEngine extends Thread {
    private boolean running = true;

    public MatchingEngine() {
        this.setDaemon(true);
    }

    public void shutdown() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            try {
                // Loop through all registered order books
                CustomLinkedList books = OrderBook.getAll();
                for (int i = 0; i < books.size(); i++) {
                    OrderBook book = (OrderBook) books.get(i);
                    matchOrdersForBook(book);
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.err.println("Matching engine interrupted: " + e.getMessage());
                break;
            } catch (Exception e) {
                System.err.println("Error in matching engine loop: " + e.getMessage());
            }
        }
    }

    private void matchOrdersForBook(OrderBook book) {
        synchronized (book) {
            while (!book.getBuySide().isEmpty() && !book.getSellSide().isEmpty()) {
                Order buyOrder = book.getBuySide().peekMax();
                Order sellOrder = book.getSellSide().peekMin();

                if (buyOrder == null || sellOrder == null) break;

                if (buyOrder.getPrice() >= sellOrder.getPrice()) {
                    int qtyToMatch = Math.min(buyOrder.getQuantity(), sellOrder.getQuantity());
                    // Determine executed price safely (avoiding MARKET order sentinel prices 9999999.99 and 0.0)
                    boolean isBuyMarket = buyOrder.getOrderType() == model.OrderType.MARKET || buyOrder.getPrice() >= 9999999.0;
                    boolean isSellMarket = sellOrder.getOrderType() == model.OrderType.MARKET || sellOrder.getPrice() <= 0.0;

                    double executedPrice;
                    if (isBuyMarket && !isSellMarket) {
                        // MARKET BUY + LIMIT SELL: Execute at LIMIT SELL price
                        executedPrice = sellOrder.getPrice();
                    } else if (!isBuyMarket && isSellMarket) {
                        // LIMIT BUY + MARKET SELL: Execute at LIMIT BUY price
                        executedPrice = buyOrder.getPrice();
                    } else if (isBuyMarket && isSellMarket) {
                        // MARKET BUY + MARKET SELL: Execute at stock's current market price
                        model.Stock stock = DatabaseManager.getStock(book.getTicker());
                        executedPrice = (stock != null) ? stock.getCurrentPrice() : 100.0;
                    } else {
                        // LIMIT BUY + LIMIT SELL: Execute at the price of the order that was placed first
                        executedPrice = (buyOrder.getOrderId() < sellOrder.getOrderId()) 
                                                ? buyOrder.getPrice() 
                                                : sellOrder.getPrice();
                    }

                    boolean success = TradeTransaction.execute(buyOrder, sellOrder, executedPrice, qtyToMatch);
                    if (success) {
                        // Log trade
                        Trade trade = new Trade(0, buyOrder.getOrderId(), sellOrder.getOrderId(), 
                                                book.getTicker(), executedPrice, qtyToMatch, 
                                                new Timestamp(System.currentTimeMillis()));
                        IOManager.logTrade(trade);

                        // Update current price of the stock
                        DatabaseManager.updateStockPriceAndHistory(book.getTicker(), executedPrice);

                        // Update local quantities and remove if fully matched
                        if (buyOrder.getQuantity() == qtyToMatch) {
                            book.getBuySide().pollMax();
                        } else {
                            buyOrder.setQuantity(buyOrder.getQuantity() - qtyToMatch);
                        }

                        if (sellOrder.getQuantity() == qtyToMatch) {
                            book.getSellSide().pollMin();
                        } else {
                            sellOrder.setQuantity(sellOrder.getQuantity() - qtyToMatch);
                        }
                    } else {
                        // If transaction failed (usually due to buyer insufficient balance),
                        // we must cancel the buy order so we don't get stuck in an infinite loop.
                        // System.err.println("Match failed for Buy Order " + buyOrder.getOrderId() + " and Sell Order " + sellOrder.getOrderId() + ". Cancelling buy order due to transaction failure.");
                        book.getBuySide().pollMax();
                        DatabaseManager.updateOrder(buyOrder.getOrderId(), OrderStatus.CANCELLED, buyOrder.getQuantity());
                    }
                } else {
                    // Spread is positive, no matches possible
                    break;
                }
            }
        }
    }
}
