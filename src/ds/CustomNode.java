package ds;

/**
 * Node class for Singly Linked List.
 *
 * DS Concept (Sem 2 - Chapter 4):
 *   A node is the basic building block of a Linked List.
 *   Each node stores:
 *     - data  : the actual value (stored as Object so any type can be used)
 *     - next  : reference (pointer) to the next node
 *
 *  [data | next] --> [data | next] --> null
 */
public class CustomNode {
    public Object data;      // stores any type of value (Order, Stock, etc.)
    public CustomNode next;  // points to the next node in the list

    public CustomNode(Object data) {
        this.data = data;
        this.next = null;    // by default, next points to nothing
    }
}
