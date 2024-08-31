# Java Thread Safe Queue

A Java implementation of a thread-safe linked queue with routines for insertion, deletion, and search, along with a simplified map-reduce system using thread pools. The solution ensures concurrency and synchronization, fulfilling requirements for thread safety. Task for the [INF5170: Models of Concurrency course.](https://www.uio.no/studier/emner/matnat/ifi/IN5170/)


## Performance log


### V1 

With a single lock around write operations. 

```text
Execution Time: 2s

Sum even: 166002164
Sum odd: 166666500
1164836
```