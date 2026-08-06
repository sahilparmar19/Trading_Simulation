package ds;

/**
 * Custom Singly Linked List implementation.
 *
 * Used in this project to store:
 *   - OrderBook registry (all tickers)
 *   - Portfolio holdings, sectors, stocks, pending orders
 */
public class CustomLinkedList {

    private CustomNode head;   // points to first node
    private CustomNode tail;   // points to last node (for O(1) addLast)
    private int size;          // tracks number of elements

    // Constructor – empty list
    public CustomLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    // --------- -------------------------------------------------------
    //  INSERT AT BEGINNING  (addFirst)
    //  New node becomes the new head.
    //  Before: head --> [B] --> [C] --> null
    //  After:  head --> [A] --> [B] --> [C] --> null
    // ----------------------------------------------------------------
    public void addFirst(Object data) {
        CustomNode newNode = new CustomNode(data);
        if (head == null) {
            // List was empty – both head and tail point to new node
            head = newNode;
            tail = newNode;
        } else {
            newNode.next = head;
            head = newNode;
        }
        size++;
    }

    // ----------------------------------------------------------------
    //  INSERT AT END  (addLast)
    //  New node is added after tail.
    //  Before: head --> [A] --> [B]      (tail = B)
    //  After:  head --> [A] --> [B] --> [C]   (tail = C)
    // ----------------------------------------------------------------
    public void addLast(Object data) {
        CustomNode newNode = new CustomNode(data);
        if (tail == null) {
            // List was empty
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
    }

    // ----------------------------------------------------------------
    //  DELETE BY VALUE  (remove)
    //  Traverses list to find the node with matching data, then
    //  bypasses it by adjusting the previous node's next pointer.
    //
    //  Before: head --> [A] --> [B] --> [C] --> null
    //  Remove B:
    //  After:  head --> [A] --> [C] --> null
    // ----------------------------------------------------------------
    public boolean remove(Object data) {
        if (head == null) return false;

        // Special case: removing the head node
        if (head.data.equals(data)) {
            head = head.next;
            if (head == null) {
                tail = null;  // list is now empty
            }
            size--;
            return true;
        }

        // Traverse to find the node just before the one to delete
        CustomNode current = head;
        while (current.next != null) {
            if (current.next.data.equals(data)) {
                if (current.next == tail) {
                    tail = current;  // update tail if last node is deleted
                }
                current.next = current.next.next;  // bypass the deleted node
                size--;
                return true;
            }
            current = current.next;
        }
        return false;  // data not found
    }

    // ----------------------------------------------------------------
    //  ACCESS BY INDEX  (get)
    //  Traverses from head until reaching the desired index.
    //  Returns Object — caller must cast to the correct type.
    //  O(n) – unlike arrays which are O(1).
    // ----------------------------------------------------------------
    public Object get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        CustomNode current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }

    // ----------------------------------------------------------------
    //  SEARCH BY VALUE  (contains)
    //  Traverses entire list to check if value exists.
    // ----------------------------------------------------------------
    public boolean contains(Object data) {
        CustomNode current = head;
        while (current != null) {
            if (current.data.equals(data)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    // Returns number of elements in the list
    public int size() {
        return size;
    }

    // Returns true if list has no elements
    public boolean isEmpty() {
        return size == 0;
    }
}
