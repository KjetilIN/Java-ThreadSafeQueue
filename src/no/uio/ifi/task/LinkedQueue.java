package no.uio.ifi.task;

import java.util.concurrent.locks.*;

public class LinkedQueue<T> {
    private Node<T> head;
    private Node<T> tail;
    protected int count = 0;

    // ReentrantLock for fine-grained locking instead of synchronized methods
    private final Lock lock = new ReentrantLock();

    public synchronized int find(T t) {
        // Assume that head is not null 
        assert (head != null);

        // Look for the correct node, starting from head. 
        Node<T> currentNode = head;
        while (currentNode != null) {
            if (currentNode.content.equals(t)) {
                // Found the correct node
                return System.identityHashCode(currentNode);
            }
            currentNode = currentNode.next;
        }

        // Not found
        return 0;
    }

    public void insert(T t) {
        // Create the new node without needing a lock
        Node<T> newNode = new Node<>(t);

        // Lock when inserting 
        lock.lock(); 
        try {
            if (tail == null) {
                head = newNode;
            } else {
                tail.next = newNode;
            }
            tail = newNode;
            this.count += 1; 
        } finally {
            lock.unlock();
        }
    }

    public T delfront() {
        T content;

        // Lock when deleting 
        lock.lock(); 
        try {
            // Check if the linked queue is empty 
            if (head == null) {
                return null;
            }

            // Store content
            content = head.content;

            // Check for only one item in the list
            if (head == tail) {
                tail = null; // One element in list
            }
            // Reassign head to the next element
            head = head.next;
        } finally {
            lock.unlock(); // Always unlock in a finally block
        }

        return content;
    }
}
