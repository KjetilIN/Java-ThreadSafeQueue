package no.uio.ifi.task;

import java.util.concurrent.locks.*;

public class LinkedQueue<T> {
    private Node<T> head;
    private Node<T> tail;

    // ReentrantLock for fine-grained locking instead of synchronized methods
    private final Lock lock = new ReentrantLock();

    public synchronized int find(T t) {

        /**
         * NOTE: We did not find a proper use for the function.
         * We are able to do the task without using it. 
         * 
         * We always take the first node from the queue. Thread N does not need to look for number N in the queue.
         * 
         * It was also unclear what to return from this function, since the return type has to be int and not T. 
         * Therefor, we return 1 if the node is in the queue.  
         */

        // Assume that head is not null 
        assert (head != null);

        // Look for the correct node, starting from head. 
        Node<T> currentNode = head;
        while (currentNode != null && !currentNode.content.equals(t)) {
            currentNode = currentNode.next;
        }

        // If current node was found 
        // We return 1
        if (currentNode != null){
            return 1;
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
        } finally {
            lock.unlock();
        }
    }

    public synchronized T delfront() {
        /**
         * This function has the synchronized keyword. This means only one method call off delfront 
         * can be ran at the time for the thread. This ensures that deleting will only happen once for the entire thread.
         * Java will allow only one thread at a particular time to complete a given task entirely.
         * 
         * Therefor, we do not need to add a lock here. 
         */

        T content;
        
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
        

        return content;
    }
}
