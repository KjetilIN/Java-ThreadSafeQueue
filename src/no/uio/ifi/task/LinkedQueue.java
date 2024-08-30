package no.uio.ifi.task;


/* You are allowed to 1. add modifiers to fields and method signatures and 2. add code at the marked places, including removing the following return */
public class LinkedQueue<T> {
    private Node<T> head;
    private Node<T> tail;


    public synchronized int find(T t){
        // Assume that head is not null 
        assert(head != null);

        // Set the first node as the starting point for the search, head must not be null 
        Node<T> currentNode = head;

        // Loop over every node
        while (!currentNode.content.equals(t)) {
            currentNode = currentNode.next;
            if (currentNode == null){
                return 0;
            }
        }

        /* Should return the a pointer to the node */
        return System.identityHashCode(currentNode);
    }

    public void insert(T t){
       if (tail == null){
            head = new Node<T>(t);
       }else{
            tail.next = new Node<T>(t);
       }
       tail = new Node<T>(t);
       return;
    }

    public synchronized T delfront(){
        if (head != null){
            // Storing content 
            T content = head.content;

            // Check for only one item in the list
            if (head == tail){
                // One element in list
                tail = null; 
            }
            // Reassign head to the next element
            head = head.next; 
            return content;
        }

        // Returns null when the 
        return null;
    }
}
