# Java Thread Safe Queue

A Java implementation of a thread-safe linked queue with routines for insertion, deletion, and search, along with a simplified map-reduce system using thread pools. The solution ensures concurrency and synchronization, fulfilling requirements for thread safety. This project is developed as part of the [INF5170: Models of Concurrency course](https://www.uio.no/studier/emner/matnat/ifi/IN5170/).

## Project Overview

This project implements:

- **Thread-Safe Queue (`LinkedQueue`)**: A custom implementation of a linked queue that is thread-safe, allowing multiple threads to concurrently insert and delete elements.
- **Map-Reduce System**: A basic map-reduce framework using Java's `ExecutorService` for parallel processing. The mappers distribute input numbers into two queues (even and odd), and reducers compute the sum of squares for each queue.

The primary goal of this project is to demonstrate concurrency control and efficient use of multithreading in Java without using built-in concurrent collections like `ConcurrentLinkedQueue`.

## Performance log

### V1 

With a single lock around write operations. 

```text
Sum even: 166002164
Sum odd: 166666500
1164836
```

### V2

Improved distribution and thread distribution: 
```text
Sum even: 166866696
Sum odd: 166417514
549290

# PERFECT RUN!
Sum even: 167167000
Sum odd: 166666500
0

Sum even: 167167000
Sum odd: 165869050
797450

Sum even: 167024116
Sum odd: 166544699
264685
```

### V3

Lock around reducer variables. Each reducer has their own lock. 
This makes the result deterministic and each run it will always do as required. 


# Resources

Lock free Queues: <br>
https://jbseg.medium.com/lock-free-queues-e48de693654b 

Package `java.utils.atomic`: <br>
https://download.java.net/java/early_access/panama/docs/api/java.base/java/util/concurrent/atomic/package-summary.html 