package engine;

import db.DatabaseManager;
import db.TradeTransaction;
import io.IOManager;
import model.Order;
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
                for (OrderBook book : OrderBook.getAll()) {
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
                    // Execute at the price of the order that was placed first
                    double executedPrice = (buyOrder.getOrderId() < sellOrder.getOrderId()) 
                                            ? buyOrder.getPrice() 
                                            : sellOrder.getPrice();

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
                        DatabaseManager.updateOrder(buyOrder.getOrderId(), "CANCELLED", buyOrder.getQuantity());
                    }
                } else {
                    // Spread is positive, no matches possible
                    break;
                }
            }
        }
    }
}
