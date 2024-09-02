package no.uio.ifi.task;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/* You are allowed to 1. add modifiers to fields and method signatures of subclasses, and 2. add code at the marked places, including removing the following return */
public class main {
    public static void main(String[] args) throws InterruptedException {

        /** Linked Queues */
        LinkedQueue<Integer> inputQueue = new LinkedQueue<>();
        LinkedQueue<Integer> evenQueue = new LinkedQueue<>();
        LinkedQueue<Integer> oddQueue = new LinkedQueue<>();

        HashMap<Boolean, LinkedQueue<Integer>> layer = new HashMap<>();
        layer.put(true, evenQueue);
        layer.put(false, oddQueue);

        int n = 1000;

        /** Insert to queue service
         * The API for the thread pool allows us to submit a new task. 
         * In this case, we start a new task for inserting numbers to the queue. 
         */
        ExecutorService inputExc = Executors.newCachedThreadPool();
        // Starting threads to insert numbers into the input queue
        for (int i = 1; i <= n; i++){
            // Submit the task of inserting the number to the thread pool
            // Each task is to submit a number to the start queue 
            final int numberToInsert = i; 
            inputExc.submit(() ->{
                inputQueue.insert(numberToInsert);
            });
        }
        // Here we call the shutdown method. It tells the pool, that no more task will be entered to the thread pool
        inputExc.shutdown();

        // The two mappers that are each responsible to take from the input queue 
        // Both mapper will both insert to both queues 
        Mapper<Integer, Boolean> mapper1 = new Mapper<Integer, Boolean>(layer) {
            @Override
            void transform(Integer input) {
                // Take number and put it into the right queue 
                // We get the queue we want to add data to and then insert the input squared
                LinkedQueue<Integer> targetQueue = layer.get(input % 2 == 0); 
                targetQueue.insert(input * input); 
                this.count += 1;
            }
        };
        Mapper<Integer, Boolean> mapper2 = new Mapper<Integer, Boolean>(layer) {
            @Override
            void transform(Integer input) {
                // Same implementation as mapper1
                LinkedQueue<Integer> targetQueue = layer.get(input % 2 == 0); 
                targetQueue.insert(input * input); 
                this.count += 1;
            }
        };

        // Creating the pool of threads to distribute 
        // Start N tasks that each take from the input queue 
        ExecutorService distribute = Executors.newCachedThreadPool();

        /** Shared atomic counter for distributing tasks
         * This is an object part of the java.util.concurrent.atomic library.
         * It acts as a integer, but all operations are atomic. 
         * This will act as a way to distribute the workload between the two mappers
         *  - distribute based on if the integer is even or odd? 
        */ 
        AtomicInteger distributeCounter = new AtomicInteger(0);
        // Starting threads distribute the work to headers 
        for (int i = 1; i <= n; i++){
            distribute.submit(() ->{
                // Try to get the number from delfront method
                // Remove the number from the queue with the delfront() queue 
                // If number is null, then we know the queue is empty.
                Integer number = inputQueue.delfront();
                if (number != null){
                    // Atomic fetch and increment 
                    // Then based on if the number is odd or even, distribute the work to a mapper
                    // We get and increment atomically to ensure that we get either an odd or even number
                    if (distributeCounter.getAndIncrement() % 2 == 0){
                        mapper1.transform(number);
                    }else{
                        mapper2.transform(number);
                    }
                }                
            });
        }

        // Shutdown, because no more numbers to distribute  
        distribute.shutdown();


        /** Reducers:
         * We assign one reducer for each queue - even and odd numbers.
         * The process itself is very simple, but we still need a lock for each. 
         * This is because there are some cases where the operations of incrementing the count and current leads to non deterministic result.
         * The original code result: 
         * Lock around 
         * InputQueue count: 1000
         * OddQueue count: 500
         * EvenQueue count: 500
         * Mapper1 count: 500
         * Mapper2 count: 500
         * Sum even: 165458664
         * Count even: 497 => Suggests that count happened in a way that lead to a bad result. And 
         * Sum odd: 165791482
         * Count odd: 498
         * 2583354
         * 
         * With the lock for each reducer, we assure that we get a more deterministic result.
         */
        Lock reduceLock1 = new ReentrantLock();
        Reducer<Integer> reducer1 = new Reducer<Integer>() {
            @Override
            protected void reduce(Integer input) {
                reduceLock1.lock();
                try{
                    this.count += 1; 
                    this.current += input;
                }finally{
                    reduceLock1.unlock();
                }
            }
        };

        Lock reduceLock2 = new ReentrantLock();
        Reducer<Integer> reducer2 = new Reducer<Integer>() {
            @Override
            protected void reduce(Integer input) {
                reduceLock2.lock();
                try{
                    this.count += 1; 
                    this.current += input;
                }finally{
                    reduceLock2.unlock();
                }
            }
        };


        // Reducer thread-pool 
        ExecutorService reduce = Executors.newCachedThreadPool();
        for (int j = 1; j<=n; j++){
            // Reduce task to be submitted
            final int reduceNumber = j; 
            reduce.submit(()->{
                // Boolean for what queue to take from 
                // Will evenly distribute the reduce method for each 
                // This is also another way to solve the task of distributing 
                boolean isEven = reduceNumber % 2 == 0;

                // Get a queue based on if the even or odd task number. 
                LinkedQueue<Integer> targetQueue = layer.get(isEven); 
                Integer numberFromQueue = targetQueue.delfront();

                // Distribute and then reduce 
                if (isEven){
                    reducer1.reduce(numberFromQueue);
                }else{
                    reducer2.reduce(numberFromQueue);
                }
                            
            });
        }

        // Shutdown reducer thread pool. No more tasks. 
        reduce.shutdown();

        // Final tests
        Thread.sleep(2000);
        System.out.println("Sum even: "+reducer1.current);
        System.out.println("Sum odd: "+reducer2.current);

        int total = 0;
        for(int i = 1; i<= n; i++){
            total += i*i;
        }
        System.out.println(total - (reducer1.current + reducer2.current));
    }
}