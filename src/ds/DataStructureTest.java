package ds;

import model.Order;
import java.sql.Timestamp;

public class DataStructureTest {
    public static void main(String[] args) {
        System.out.println("=== Testing CustomLinkedList ===");
        CustomLinkedList<Integer> list = new CustomLinkedList<>();
        list.addLast(1);
        list.addLast(2);
        list.addFirst(0);
        list.addLast(3);

        System.out.println("Size (expected 4): " + list.size());
        for (int val : list) {
            System.out.print(val + " ");
        }
        System.out.println();

        list.remove(2);
        System.out.println("Size after remove(2) (expected 3): " + list.size());
        for (int val : list) {
            System.out.print(val + " ");
        }
        System.out.println();

        System.out.println("=== Testing CustomBSTOrderBook (Buy Side) ===");
        CustomBSTOrderBook buyBook = new CustomBSTOrderBook(true); // buy side
        Timestamp now = new Timestamp(System.currentTimeMillis());

        Order o1 = new Order(1, 101, "RELIANCE", true, "LIMIT", 100.0, 10, 0.0, now, "PENDING");
        Order o2 = new Order(2, 102, "RELIANCE", true, "LIMIT", 105.0, 5, 0.0, now, "PENDING");
        Order o3 = new Order(3, 103, "RELIANCE", true, "LIMIT", 100.0, 15, 0.0, now, "PENDING"); // same price as o1, but later ID

        buyBook.insert(o1);
        buyBook.insert(o2);
        buyBook.insert(o3);

        System.out.println("Buy Book size (expected 3): " + buyBook.size());
        // Best buy order should be o2 (price 105.0)
        Order bestBuy = buyBook.peekMax();
        System.out.println("Best Buy (expected ID 2, price 105.0): ID " + (bestBuy != null ? bestBuy.getOrderId() : "null") + ", Price " + (bestBuy != null ? bestBuy.getPrice() : "null"));

        Order polled1 = buyBook.pollMax();
        System.out.println("Polled 1 (expected ID 2): ID " + (polled1 != null ? polled1.getOrderId() : "null"));

        // Next best should be o1 (price 100.0, ID 1 - FIFO tie-breaker)
        Order polled2 = buyBook.pollMax();
        System.out.println("Polled 2 (expected ID 1): ID " + (polled2 != null ? polled2.getOrderId() : "null"));

        Order polled3 = buyBook.pollMax();
        System.out.println("Polled 3 (expected ID 3): ID " + (polled3 != null ? polled3.getOrderId() : "null"));
        System.out.println("Buy Book size after polling all (expected 0): " + buyBook.size());

        System.out.println("=== Testing CustomBSTOrderBook (Sell Side) ===");
        CustomBSTOrderBook sellBook = new CustomBSTOrderBook(false); // sell side
        Order s1 = new Order(4, 101, "RELIANCE", false, "LIMIT", 100.0, 10, 0.0, now, "PENDING");
        Order s2 = new Order(5, 102, "RELIANCE", false, "LIMIT", 95.0, 5, 0.0, now, "PENDING");
        Order s3 = new Order(6, 103, "RELIANCE", false, "LIMIT", 100.0, 15, 0.0, now, "PENDING"); // same price, later ID

        sellBook.insert(s1);
        sellBook.insert(s2);
        sellBook.insert(s3);

        // Best sell order should be s2 (price 95.0)
        Order bestSell = sellBook.peekMin();
        System.out.println("Best Sell (expected ID 5, price 95.0): ID " + (bestSell != null ? bestSell.getOrderId() : "null") + ", Price " + (bestSell != null ? bestSell.getPrice() : "null"));

        Order polledS1 = sellBook.pollMin();
        System.out.println("Polled Sell 1 (expected ID 5): ID " + (polledS1 != null ? polledS1.getOrderId() : "null"));

        // Next best should be s1 (price 100.0, ID 4 - FIFO tie-breaker)
        Order polledS2 = sellBook.pollMin();
        System.out.println("Polled Sell 2 (expected ID 4): ID " + (polledS2 != null ? polledS2.getOrderId() : "null"));

        Order polledS3 = sellBook.pollMin();
        System.out.println("Polled Sell 3 (expected ID 6): ID " + (polledS3 != null ? polledS3.getOrderId() : "null"));
        System.out.println("Sell Book size after polling all (expected 0): " + sellBook.size());
    }
}
