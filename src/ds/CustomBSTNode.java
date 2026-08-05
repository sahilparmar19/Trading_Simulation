package ds;

import model.Order;

/**
 * Node class for Binary Search Tree (BST) — stores Order objects.
 *
 * DS Concept (Sem 2 - Unit 9: Binary Search Tree):
 *   Each BST node has:
 *     - data  : the Order stored at this node
 *     - left  : reference to left child  (smaller values go left)
 *     - right : reference to right child (larger values go right)
 *
 *   Tree structure:
 *              [root]
 *             /      \
 *         [left]   [right]
 *
 *   BST Property:
 *     left.data < root.data < right.data
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