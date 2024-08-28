package no.uio.ifi.task;

public class Node<T> {
    public Node<T> next;
    public final T content;

    public Node(T content) {
        this.content = content;
    }
}