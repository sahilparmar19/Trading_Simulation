package ds;

import model.Order;

public class CustomBSTOrderBook {
    private CustomBSTNode<Order> root;
    private final boolean isBuy;
    private int size;

    public CustomBSTOrderBook(boolean isBuy) {
        this.isBuy = isBuy;
        this.root = null;
        this.size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return root == null;
    }

    private int compareOrders(Order o1, Order o2) {
        if (o1.getOrderId() == o2.getOrderId()) {
            return 0;
        }
        if (isBuy) {
            // Buy Side: highest price first, then oldest order (smaller orderId) first.
            // Under this logic, we want the rightmost element in the tree to be the best order.
            if (o1.getPrice() != o2.getPrice()) {
                return Double.compare(o1.getPrice(), o2.getPrice());
            }
            return o1.getOrderId() < o2.getOrderId() ? 1 : -1;
        } else {
            // Sell Side: lowest price first, then oldest order (smaller orderId) first.
            // Under this logic, we want the leftmost element in the tree to be the best order.
            if (o1.getPrice() != o2.getPrice()) {
                return Double.compare(o1.getPrice(), o2.getPrice());
            }
            return o1.getOrderId() < o2.getOrderId() ? -1 : 1;
        }
    }

    public void insert(Order order) {
        root = insertRec(root, order);
    }

    private CustomBSTNode<Order> insertRec(CustomBSTNode<Order> node, Order order) {
        if (node == null) {
            size++;
            return new CustomBSTNode<>(order);
        }
        int cmp = compareOrders(order, node.data);
        if (cmp < 0) {
            node.left = insertRec(node.left, order);
        } else if (cmp > 0) {
            node.right = insertRec(node.right, order);
        }
        return node;
    }

    public Order peekMin() {
        if (root == null) return null;
        CustomBSTNode<Order> curr = root;
        while (curr.left != null) {
            curr = curr.left;
        }
        return curr.data;
    }

    public Order peekMax() {
        if (root == null) return null;
        CustomBSTNode<Order> curr = root;
        while (curr.right != null) {
            curr = curr.right;
        }
        return curr.data;
    }

    public Order pollMin() {
        Order min = peekMin();
        if (min != null) {
            remove(min);
        }
        return min;
    }

    public Order pollMax() {
        Order max = peekMax();
        if (max != null) {
            remove(max);
        }
        return max;
    }

    public void remove(Order order) {
        root = removeRec(root, order);
    }

    private CustomBSTNode<Order> removeRec(CustomBSTNode<Order> node, Order order) {
        if (node == null) return null;

        int cmp = compareOrders(order, node.data);
        if (cmp < 0) {
            node.left = removeRec(node.left, order);
        } else if (cmp > 0) {
            node.right = removeRec(node.right, order);
        } else {
            // Node found
            size--;
            if (node.left == null) {
                return node.right;
            } else if (node.right == null) {
                return node.left;
            }

            // Node with two children
            Order successor = minValue(node.right);
            node.data = successor;
            
            // Delete successor from right subtree
            size++; // temporary increment since removeRec will decrement it
            node.right = removeRec(node.right, successor);
        }
        return node;
    }

    private Order minValue(CustomBSTNode<Order> node) {
        Order minv = node.data;
        while (node.left != null) {
            minv = node.left.data;
            node = node.left;
        }
        return minv;
    }
}
