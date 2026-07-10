package ds;

public class CustomNode<T> {
    public T data;
    public CustomNode<T> next;

    public CustomNode(T data) {
        this.data = data;
        this.next = null;
    }
}
