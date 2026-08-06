package ds;

import model.Order;

/**
 * Custom Binary Search Tree (BST) for the Order Book.
 *
 * This BST stores Buy/Sell orders sorted by price.
 * - BUY side: Best order = HIGHEST price (rightmost node)
 * - SELL side: Best order = LOWEST price (leftmost node)
 */
public class CustomBSTOrderBook {

    private CustomBSTNode root;     // root of the BST
    private final boolean isBuy;    // true = Buy side, false = Sell side
    private int size;               // number of orders in the tree

    public CustomBSTOrderBook(boolean isBuy) {
        this.isBuy = isBuy;
        this.root = null;
        this.size = 0;
    }

    // Returns number of orders in the book
    public int size() {
        return size;
    }

    // Returns true if no orders are present
    public boolean isEmpty() {
        return root == null;
    }

    // ----------------------------------------------------------------
    //  COMPARE ORDERS
    //  Decides where each order goes in the BST (left or right).
    //
    //  Returns:
    //    negative  --> o1 goes LEFT  of o2  (o1 is "less than" o2)
    //    zero      --> same orderId (duplicate – do not insert again)
    //    positive  --> o1 goes RIGHT of o2  (o1 is "greater than" o2)
    //
    //  BUY side  : higher price   --> goes RIGHT (best buy = rightmost)
    //              same price     : earlier orderId --> goes RIGHT (FIFO)
    //  SELL side : lower price    --> goes LEFT  (best sell = leftmost)
    //              same price     : earlier orderId --> goes LEFT  (FIFO)
    // ----------------------------------------------------------------
    private int compareOrders(Order o1, Order o2) {
        // Same order ID = duplicate, treat as equal
        if (o1.getOrderId() == o2.getOrderId()) {
            return 0;
        }

        if (isBuy) {
            // Buy side: higher price goes right (rightmost = best)
            if (o1.getPrice() != o2.getPrice()) {
                return Double.compare(o1.getPrice(), o2.getPrice());
            }
            // Same price: earlier order (smaller ID) goes right (FIFO)
            return o1.getOrderId() < o2.getOrderId() ? 1 : -1;
        } else {
            // Sell side: lower price goes left (leftmost = best)
            if (o1.getPrice() != o2.getPrice()) {
                return Double.compare(o1.getPrice(), o2.getPrice());
            }
            // Same price: earlier order (smaller ID) goes left (FIFO)
            return o1.getOrderId() < o2.getOrderId() ? -1 : 1;
        }
    }

    // ----------------------------------------------------------------
    //  INSERT
    //  Recursively finds the correct position and places the new order.
    //  Duplicate orderIds are ignored.
    // ----------------------------------------------------------------
    public void insert(Order order) {
        root = insertRec(root, order);
    }

    private CustomBSTNode insertRec(CustomBSTNode node, Order order) {
        // Base case: found an empty spot --> create new node here
        if (node == null) {
            size++;
            return new CustomBSTNode(order);
        }

        int cmp = compareOrders(order, node.data);

        if (cmp < 0) {
            node.left = insertRec(node.left, order);   // go left
        } else if (cmp > 0) {
            node.right = insertRec(node.right, order); // go right
        }
        // cmp == 0 --> duplicate orderId, do nothing

        return node;
    }

    // ----------------------------------------------------------------
    //  PEEK MIN (view smallest without removing)
    //  In BST, smallest value is always at the LEFTMOST node.
    // ----------------------------------------------------------------
    public Order peekMin() {
        if (root == null) return null;
        CustomBSTNode curr = root;
        while (curr.left != null) {
            curr = curr.left;
        }
        return curr.data;
    }

    // ----------------------------------------------------------------
    //  PEEK MAX (view largest without removing)
    //  In BST, largest value is always at the RIGHTMOST node.
    // ----------------------------------------------------------------
    public Order peekMax() {
        if (root == null) return null;
        CustomBSTNode curr = root;
        while (curr.right != null) {
            curr = curr.right;
        }
        return curr.data;
    }

    // ----------------------------------------------------------------
    //  POLL MIN (remove and return smallest)
    //  Used by: Sell Side – to get the cheapest sell order
    // ----------------------------------------------------------------
    public Order pollMin() {
        Order min = peekMin();
        if (min != null) {
            remove(min);
        }
        return min;
    }

    // ----------------------------------------------------------------
    //  POLL MAX (remove and return largest)
    //  Used by: Buy Side – to get the highest buy order
    // ----------------------------------------------------------------
    public Order pollMax() {
        Order max = peekMax();
        if (max != null) {
            remove(max);
        }
        return max;
    }

    // ----------------------------------------------------------------
    //  REMOVE
    //  Standard BST deletion with 3 cases.
    // ----------------------------------------------------------------
    public void remove(Order order) {
        root = removeRec(root, order);
    }

    private CustomBSTNode removeRec(CustomBSTNode node, Order order) {
        // Base case: order not found in tree
        if (node == null) return null;

        int cmp = compareOrders(order, node.data);

        if (cmp < 0) {
            // Target is in left subtree
            node.left = removeRec(node.left, order);
        } else if (cmp > 0) {
            // Target is in right subtree
            node.right = removeRec(node.right, order);
        } else {
            // Found the node to delete
            size--;

            // Case 1 & 2: No left child --> replace node with right child
            if (node.left == null) {
                return node.right;
            }
            // Case 2: No right child --> replace node with left child
            if (node.right == null) {
                return node.left;
            }

            // Case 3: Node has TWO children
            // Find in-order successor = smallest node in right subtree
            Order successor = findMin(node.right);
            node.data = successor;

            // Delete the successor from the right subtree
            // (temporarily undo size decrement since removeRec will decrement again)
            size++;
            node.right = removeRec(node.right, successor);
        }
        return node;
    }

    // ----------------------------------------------------------------
    //  FIND MIN (helper)
    //  Returns the data of the leftmost node in the given subtree.
    //  Used internally during Case 3 deletion.
    // ----------------------------------------------------------------
    private Order findMin(CustomBSTNode node) {
        Order min = node.data;
        while (node.left != null) {
            min = node.left.data;
            node = node.left;
        }
        return min;
    }
}
