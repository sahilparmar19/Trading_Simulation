package engine;

import ds.CustomBSTOrderBook;
import ds.CustomLinkedList;

public class OrderBook {
    private final String ticker;
    private final CustomBSTOrderBook buySide;
    private final CustomBSTOrderBook sellSide;

    // Static registry — stores all OrderBook objects (one per ticker)
    private static final CustomLinkedList orderBooks = new CustomLinkedList();

    public OrderBook(String ticker) {
        this.ticker = ticker;
        this.buySide = new CustomBSTOrderBook(true);
        this.sellSide = new CustomBSTOrderBook(false);
    }

    public String getTicker() {
        return ticker;
    }

    public CustomBSTOrderBook getBuySide() {
        return buySide;
    }

    public CustomBSTOrderBook getSellSide() {
        return sellSide;
    }

    // Static registry: find OrderBook by ticker, or create a new one
    public static synchronized OrderBook get(String ticker) {
        // Search through existing order books
        for (int i = 0; i < orderBooks.size(); i++) {
            OrderBook ob = (OrderBook) orderBooks.get(i);
            if (ob.getTicker().equalsIgnoreCase(ticker)) {
                return ob;
            }
        }
        // Not found — create a new one and add to registry
        OrderBook newOb = new OrderBook(ticker.toUpperCase());
        orderBooks.addLast(newOb);
        return newOb;
    }

    public static synchronized CustomLinkedList getAll() {
        return orderBooks;
    }
}
