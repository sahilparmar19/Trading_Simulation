package ds;

import model.Order;
import java.sql.Timestamp;

public class DataStructureTest {

    public static void main(String[] args) {

        /* ==========================================================
         * TEST 1 : Custom Linked List
         * Objective:
         * Verify insertion, deletion, size calculation and index access.
         * ========================================================== */
        System.out.println("=== Testing CustomLinkedList ===");

        CustomLinkedList list = new CustomLinkedList();

        // Insert elements into the linked list
        list.addLast(1);
        list.addLast(2);
        list.addFirst(0);
        list.addLast(3);

        // Verify size after insertion
        System.out.println("Size (expected 4): " + list.size());

        // Display all elements using index-based loop
        // (Note: stored as Object, cast to Integer when retrieving)
        for (int i = 0; i < list.size(); i++) {
            System.out.print((Integer) list.get(i) + " ");
        }
        System.out.println();

        // Remove an existing element
        list.remove(2);

        // Verify size after deletion
        System.out.println("Size after remove(2) (expected 3): " + list.size());

        // Display updated list
        for (int i = 0; i < list.size(); i++) {
            System.out.print((Integer) list.get(i) + " ");
        }
        System.out.println();


        /* ==========================================================
         * TEST 2 : Buy Order Book
         * Objective:
         * Verify insertion, ordering, polling and FIFO behaviour
         * for Buy Orders.
         * ========================================================== */
        System.out.println("=== Testing CustomBSTOrderBook (Buy Side) ===");

        CustomBSTOrderBook buyBook = new CustomBSTOrderBook(true);
        Timestamp now = new Timestamp(System.currentTimeMillis());


        /* ------------------------------
         * Empty Order Book Test
         * ------------------------------ */
        System.out.println("\n=== Empty Buy Book Test ===");

        System.out.println("Is Empty (expected true): " + buyBook.isEmpty());
        System.out.println("Peek Max (expected null): " + buyBook.peekMax());
        System.out.println("Peek Min (expected null): " + buyBook.peekMin());


        // Create sample buy orders
        Order o1 = new Order(1, 101, "RELIANCE", true, "LIMIT", 100.0, 10, 0.0, now, "PENDING");
        Order o2 = new Order(2, 102, "RELIANCE", true, "LIMIT", 105.0, 5, 0.0, now, "PENDING");
        Order o3 = new Order(3, 103, "RELIANCE", true, "LIMIT", 100.0, 15, 0.0, now, "PENDING");


        // Insert all orders into the Buy Order Book
        buyBook.insert(o1);
        buyBook.insert(o2);
        buyBook.insert(o3);


        /* ------------------------------
         * Remove Non-Existing Order Test
         * ------------------------------ */
        Order fake = new Order(
                99,
                999,
                "RELIANCE",
                true,
                "LIMIT",
                200.0,
                10,
                0.0,
                now,
                "PENDING"
        );

        System.out.println("Buy Book size before removing fake order (expected 3): "
                + buyBook.size());

        buyBook.remove(fake);

        System.out.println("Size after removing fake order (expected 3): "
                + buyBook.size());


        // Verify highest priority Buy Order
        System.out.println("Buy Book size (expected 3): " + buyBook.size());

        Order bestBuy = buyBook.peekMax();

        System.out.println(
                "Best Buy (expected ID 2, price 105.0): ID "
                        + (bestBuy != null ? bestBuy.getOrderId() : "null")
                        + ", Price "
                        + (bestBuy != null ? bestBuy.getPrice() : "null")
        );


        // Poll highest priority Buy Order
        Order polled1 = buyBook.pollMax();

        System.out.println(
                "Polled 1 (expected ID 2): ID "
                        + (polled1 != null ? polled1.getOrderId() : "null")
        );


        // FIFO verification for same price orders
        Order polled2 = buyBook.pollMax();

        System.out.println(
                "Polled 2 (expected ID 1): ID "
                        + (polled2 != null ? polled2.getOrderId() : "null")
        );


        Order polled3 = buyBook.pollMax();

        System.out.println(
                "Polled 3 (expected ID 3): ID "
                        + (polled3 != null ? polled3.getOrderId() : "null")
        );

        System.out.println(
                "Buy Book size after polling all (expected 0): "
                        + buyBook.size()
        );


        /* ==========================================================
         * TEST 3 : Single Node Deletion
         * Objective:
         * Verify deletion when BST contains only one node.
         * ========================================================== */
        System.out.println("\n=== Single Node Test ===");

        CustomBSTOrderBook singleBook = new CustomBSTOrderBook(true);

        Order single = new Order(
                20,
                101,
                "TCS",
                true,
                "LIMIT",
                3000,
                5,
                0,
                now,
                "PENDING"
        );

        singleBook.insert(single);

        System.out.println(
                "Size before remove (expected 1): "
                        + singleBook.size()
        );

        singleBook.remove(single);

        System.out.println(
                "Size after remove (expected 0): "
                        + singleBook.size()
        );

        System.out.println(
                "Peek Max (expected null): "
                        + singleBook.peekMax()
        );


        /* ==========================================================
         * TEST 4 : Two Child Node Deletion
         * Objective:
         * Verify deletion of a node having two children.
         * ========================================================== */
        System.out.println("\n=== Two Child Delete Test ===");

        CustomBSTOrderBook testBook = new CustomBSTOrderBook(true);

        Order a = new Order(1, 101, "ABC", true, "LIMIT", 100, 5, 0, now, "PENDING");
        Order b = new Order(2, 101, "ABC", true, "LIMIT", 90,  5, 0, now, "PENDING");
        Order c = new Order(3, 101, "ABC", true, "LIMIT", 110, 5, 0, now, "PENDING");

        testBook.insert(a);
        testBook.insert(b);
        testBook.insert(c);

        System.out.println(
                "Size before delete (expected 3): "
                        + testBook.size()
        );

        testBook.remove(a);

        System.out.println(
                "Size after delete (expected 2): "
                        + testBook.size()
        );

        Order best = testBook.peekMax();

        System.out.println(
                "Best Buy (expected ID 3): "
                        + (best != null ? best.getOrderId() : "null")
        );


        /* ==========================================================
         * TEST 5 : Duplicate Order ID
         * Objective:
         * Verify that duplicate Order IDs are not inserted.
         * ========================================================== */
        System.out.println("\n=== Duplicate Order ID Test ===");

        CustomBSTOrderBook duplicateBook = new CustomBSTOrderBook(true);

        Order d1 = new Order(
                50, 101, "ABC",
                true, "LIMIT",
                100, 5, 0,
                now, "PENDING"
        );

        Order d2 = new Order(
                50, 102, "ABC",
                true, "LIMIT",
                120, 5, 0,
                now, "PENDING"
        );

        duplicateBook.insert(d1);
        duplicateBook.insert(d2);

        System.out.println(
                "Size (expected 1): "
                        + duplicateBook.size()
        );


        /* ==========================================================
         * TEST 6 : Sell Order Book
         * Objective:
         * Verify insertion, ordering, polling and FIFO behaviour
         * for Sell Orders.
         * ========================================================== */
        System.out.println("=== Testing CustomBSTOrderBook (Sell Side) ===");

        CustomBSTOrderBook sellBook = new CustomBSTOrderBook(false);

        Order s1 = new Order(4, 101, "RELIANCE", false, "LIMIT", 100.0, 10, 0.0, now, "PENDING");
        Order s2 = new Order(5, 102, "RELIANCE", false, "LIMIT", 95.0,  5,  0.0, now, "PENDING");
        Order s3 = new Order(6, 103, "RELIANCE", false, "LIMIT", 100.0, 15, 0.0, now, "PENDING");


        // Insert Sell Orders
        sellBook.insert(s1);
        sellBook.insert(s2);
        sellBook.insert(s3);


        // Verify lowest price Sell Order
        Order bestSell = sellBook.peekMin();

        System.out.println(
                "Best Sell (expected ID 5, price 95.0): ID "
                        + (bestSell != null ? bestSell.getOrderId() : "null")
                        + ", Price "
                        + (bestSell != null ? bestSell.getPrice() : "null")
        );


        // Poll Sell Orders in priority order
        Order polledS1 = sellBook.pollMin();

        System.out.println(
                "Polled Sell 1 (expected ID 5): ID "
                        + (polledS1 != null ? polledS1.getOrderId() : "null")
        );


        Order polledS2 = sellBook.pollMin();

        System.out.println(
                "Polled Sell 2 (expected ID 4): ID "
                        + (polledS2 != null ? polledS2.getOrderId() : "null")
        );


        Order polledS3 = sellBook.pollMin();

        System.out.println(
                "Polled Sell 3 (expected ID 6): ID "
                        + (polledS3 != null ? polledS3.getOrderId() : "null")
        );

        System.out.println(
                "Sell Book size after polling all (expected 0): "
                        + sellBook.size()
        );
    }
}
