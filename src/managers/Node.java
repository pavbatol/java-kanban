package managers;

public class Node<T> {
    public T data;
    public Node<T> next;
    public Node<T> prev;

    Node(Node<T> prev, T data, Node<T> next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }
}
