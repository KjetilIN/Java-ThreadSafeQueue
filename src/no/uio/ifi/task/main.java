package no.uio.ifi.task;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
/* You are allowed to 1. add modifiers to fields and method signatures of subclasses, and 2. add code at the marked places, including removing the following return */
public class main {
    public static void main(String[] args) throws InterruptedException {

        LinkedQueue<Integer> inputQueue = new LinkedQueue<>();
        LinkedQueue<Integer> evenQueue = new LinkedQueue<>();
        LinkedQueue<Integer> oddQueue = new LinkedQueue<>();

        HashMap<Boolean, LinkedQueue<Integer>> layer = new HashMap<>();
        layer.put(true, evenQueue);
        layer.put(false, oddQueue);

        int n = 1000;

        ExecutorService inputExc = Executors.newCachedThreadPool();
        // Starting threads to insert numbers into the input queue
        for (int i = 1; i <= n; i++){
            // Submit the task of inserting the number to the thread pool
            final int numberToInsert = i; 
            inputExc.submit(() ->{
                inputQueue.insert(numberToInsert);
            });
        }
        inputExc.shutdown();

        Mapper<Integer, Boolean> mapper1 = new Mapper<Integer, Boolean>(layer) {
            @Override
            void transform(Integer input) {
                // take number and put it into the right queue 
                LinkedQueue<Integer> targetQueue = layer.get(input % 2 == 0); 
                targetQueue.insert(input * input); 
                this.count += 1;
            }
        };
        Mapper<Integer, Boolean> mapper2 = new Mapper<Integer, Boolean>(layer) {
            @Override
            void transform(Integer input) {
                // take number and put it into the right queue  
                LinkedQueue<Integer> targetQueue = layer.get(input % 2 == 0); 
                targetQueue.insert(input * input); 
                this.count += 1;
            }
        };

        ExecutorService distribute = Executors.newCachedThreadPool();
        /* TODO: start n threads, each taking a single number from inputQueue to either mapper1 or mapper2
        *        each mapper must have the same amount of work
        *        the mapper must add its number to the correct queue*/

        // Shared atomic counter for distributing tasks
        AtomicInteger distributeCounter = new AtomicInteger(0);

        // Starting threads distribute the work to headers 
        for (int i = 1; i <= n; i++){
            distribute.submit(() ->{
                // Try to get the number from delfront method
                // Remove the number from the queue
                // If number is null, then we know the queue 
                Integer number = inputQueue.delfront();
                if (number != null){
                    // Atomic fetch and increment 
                    // Then based on if the number is odd or even, distribute the work to a mapper
                    if (distributeCounter.getAndIncrement() % 2 == 0){
                        mapper1.transform(number);
                    }else{
                        mapper2.transform(number);
                    }
                }                
            });
        }
        distribute.shutdown();

        Reducer<Integer> reducer1 = new Reducer<Integer>() {
            @Override
            protected void reduce(Integer input) {
                this.count += 1; 
                this.current += input;

            }
        };
        Reducer<Integer> reducer2 = new Reducer<Integer>() {
            @Override
            protected void reduce(Integer input) {
                this.count += 1; 
                this.current += input;
            }
        };


        ExecutorService reduce = Executors.newCachedThreadPool();

        /* TODO: start n threads, each taking one number from either queue and giving it to a reducer.
        *        Reducer 1 will only add even numbers, reducer 2 will only add off numbers */

        for (int j = 1; j<=n; j++){
            final int reduceNumber = j; 
            reduce.submit(()->{
                boolean isEven = reduceNumber % 2 == 0;
                LinkedQueue<Integer> targetQueue = layer.get(isEven); 
                Integer numberFromQueue = targetQueue.delfront();

                if (reduceNumber % 2 == 0){
                    reducer1.reduce(numberFromQueue);
                }else{
                    reducer2.reduce(numberFromQueue);
                }
                            
            });
        }
        reduce.shutdown();

        Thread.sleep(2000);

        System.out.println("InputQueue count: " + inputQueue.count);
        System.out.println("OddQueue count: " + oddQueue.count);
        System.out.println("EvenQueue count: " + evenQueue.count);

        System.out.println("Mapper1 count: " + mapper1.count);
        System.out.println("Mapper2 count: " + mapper2.count);

        System.out.println("Sum even: "+reducer1.current);
        System.out.println("Count even: " + reducer1.count);
        System.out.println("Sum odd: "+reducer2.current);
        System.out.println("Count odd: " + reducer2.count);

        int total = 0;
        for(int i = 1; i<= n; i++){
            total += i*i;
        }
        System.out.println(total - (reducer1.current + reducer2.current));
    }
}