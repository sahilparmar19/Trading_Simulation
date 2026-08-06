package ds;

/**
 * Node class for Singly Linked List.
 */
public class CustomNode {
    public Object data;      // stores any type of value (Order, Stock, etc.)
    public CustomNode next;  // points to the next node in the list

    public CustomNode(Object data) {
        this.data = data;
        this.next = null;    // by default, next points to nothing
    }
}
