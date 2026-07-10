package engine;

import ds.CustomBSTOrderBook;
import ds.CustomLinkedList;
import model.Order;

public class OrderBook {
    private final String ticker;
    private final CustomBSTOrderBook buySide;
    private final CustomBSTOrderBook sellSide;

    private static final CustomLinkedList<OrderBook> orderBooks = new CustomLinkedList<>();

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

    // Static registry for thread-safe lookups
    public static synchronized OrderBook get(String ticker) {
        for (OrderBook ob : orderBooks) {
            if (ob.getTicker().equalsIgnoreCase(ticker)) {
                return ob;
            }
        }
        OrderBook newOb = new OrderBook(ticker.toUpperCase());
        orderBooks.addLast(newOb);
        return newOb;
    }

    public static synchronized CustomLinkedList<OrderBook> getAll() {
        return orderBooks;
    }
}
