package no.uio.ifi.task;

import java.util.concurrent.locks.*;

public class LinkedQueue<T> {
    private Node<T> head;
    private Node<T> tail;

    // ReentrantLock for fine-grained locking instead of synchronized methods
    private final ReentrantLock lock = new ReentrantLock();

    public synchronized int find(T t) {
        // Assume that head is not null 
        assert (head != null);

        Node<T> currentNode = head;
        while (currentNode != null) {
            if (currentNode.content.equals(t)) {
                // Return "pointer" to the node
                return System.identityHashCode(currentNode);
            }
            currentNode = currentNode.next;
        }

        // Not found
        return 0;
    }

    public void insert(T t) {
        lock.lock(); // Lock the method to ensure thread-safe insertions
        try {
            Node<T> newNode = new Node<>(t);
            if (tail == null) {
                head = newNode;
            } else {
                tail.next = newNode;
            }
            tail = newNode;
        } finally {
            lock.unlock();
        }
    }

    public T delfront() {
        lock.lock(); // Lock only the section where deletion is performed
        try {
            if (head != null) {
                // Store content
                T content = head.content;

                // Check for only one item in the list
                if (head == tail) {
                    tail = null; // One element in list
                }
                // Reassign head to the next element
                head = head.next;
                return content;
            }

            // Returns null when the queue is empty
            return null;
        } finally {
            lock.unlock(); // Always unlock in a finally block
        }
    }
}
