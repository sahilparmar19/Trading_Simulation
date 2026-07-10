package ds;

public class CustomBSTNode<T> {
    public T data;
    public CustomBSTNode<T> left;
    public CustomBSTNode<T> right;

    public CustomBSTNode(T data) {
        this.data = data;
        this.left = null;
        this.right = null;
    }
}
