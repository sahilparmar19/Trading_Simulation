package ds;

import model.Order;

/**
 * Node class for Binary Search Tree (BST) — stores Order objects.
 */
public class CustomBSTNode {
    public Order data;
    public CustomBSTNode left;
    public CustomBSTNode right;

    public CustomBSTNode(Order data) {
        this.data = data;
        this.left = null;
        this.right = null;
    }
}