# Oblig 1: Answers to question 1 and 2.

> [!NOTE]
> Course: IN5170 <br>
> Student Name: Kjetil Indrehus <br>
> StudentNr: 686716

# Question 1

> Identifying V and W sets of shared variables in the three routines

Each set is defined by the following: 
```math
\omega: \text{set of global read variables} 
```
```math
\nu: \text{set of global write variables}
```

The `find(d)` routine contains read and no write to global references. It reads the head as a local variable and then traverses down the three. `i` is a local variable. Thus the W set (write variables are empty): 
- head is in the V set (reading head)


```math
\nu = \{head\} 
```
```math
\omega = Ø
```

In the `insert(new)` routine.
- tail is in the V set (reading the tail)
- head is in the W set (writing the head)
- tail is in the W set (writing to the new tail)
- tail.next is in W set (writing the next node of the tail) 

```math
\nu = \{tail \} 
```
```math
\omega = \{head, tail, tail.next\}
```
In the `delfront()` routine.
- head is in the V set
- tail is in the V set
- head is in the W set 
- tail is in the W set
- head.next is in the V set

```math
\nu = \{head, tail, head.tail\} 
```
```math
\omega = \{head, tail\}
```
# Question 2 

For question two, we assume several processes access the linked list.

## A. Which combinations of routines can be executed concurrently without interference?

The following are reasoning for why the 

### Find & Find

The `find(d)` routine can run concurrently with itself. It only reads head, and does no writing. There is thus interference freedom and the processes can run concurrently: 

```math
\nu_{find} \cap \nu_{find} = \emptyset \\
```

```math
\omega_{find}= \emptyset \\
```

### Insert & Insert

For `insert(new)` and `insert(new)` routines, we check each set: 

```math
\nu_{insert} \cap \omega_{insert} = \{\text{tail}\} \\ 
```
```math
\nu_{insert} \cap \omega_{insert} \neq \emptyset 
```

Checking the sets, we see that we conclude that they do intervene with each other. Thus we need to check the at-most-once property of both routines. Each statement in the `insert(new)` routine is assignment to a non-critical reference. Thus the amo-property is held.

Since both routines satisfies the amo-property, the two routines can run concurrently.

### Find & Insert
For the `find(d)` and `insert(d)` routine. We need to first check each set:

```math
\nu_{find} \cap \omega_{insert} = \{\text{head}\}
```
```math
\nu_{insert} \cap \omega_{find} = \emptyset \\
```
```math
\nu_{find} \cap \omega_{insert} \neq \nu_{insert} \cap \omega_{find}
```

Checking the sets, we see that we conclude that they do intervene with each other. Thus we need to check the at-most-once property of both routines.

In the `find(d)` routine the `i := head;` statement is assigned to a critical reference, but `i` is a local variable that is not modified by another process, and thus the statement holds the amo-property. 

In the `insert(d)` routine the `head:= new`, which assigns a non-critical statement. Therefor, the amo-property holds. 

## B. Which combinations of routines must be executed one at a time?

The following combinations of routines must be executed one at a time:  

### Delfront & Delfront

For `delfront()` and `delfront()` routines, we check each set: 

```math
\nu_{delfront} \cap \omega_{delfront} = \{\text{head, tail}\}
```
```math
\nu_{delfront} \cap \omega_{delfront} \neq \emptyset
```

Checking the sets, we see that we conclude that they do intervene with each other. Thus we need to check the at-most-once property of both routines. In the routine the statement `head := head.next;` assigns a critical reference and the variable itself is used by the other routine. Thus, the routine does not satisfy the amo-property, and two `delfront()` routines can not run concurrently.

We can image that two processes are reading the content and storing it in a variable (to be returned). Then both reassign at the same time, which also would be non-deterministic. And then we have deleted two nodes, without getting the content of the second one. 

### Find & Delfront

For `find(d)` and `delfront()` routines, we check each set: 

```math
\nu_{find} \cap \omega_{delfront} = \{\text{head}\} \\
```
```math
\nu_{delfront} \cap \omega_{find} = \emptyset \\
```
```math
\nu_{find} \cap \omega_{delfront} \neq \nu_{delfront} \cap \omega_{find}
```

Checking the sets, we see that we conclude that they do intervene with each other. In the `find(d)` routine the statement `i:= head` we assign a critical reference. `head` is reassigned by the `delfront()` process. This means that the `find(d)` routine does not satisfy the amo-property. Thus we can conclude that these two can not run concurrently.

### Insert & Delfront 

For `insert(d)` and `delfront()` routines, we check each set: 

```math
\nu_{insert} \cap \omega_{delfront} = \{\text{tail}\} \\
```
```math
\nu_{delfront} \cap \omega_{insert} = \{\text{head, tail}\} \\
```
```math
\nu_{find} \cap \omega_{delfront} \neq \nu_{delfront} \cap \omega_{find} \neq \emptyset
```

Checking the sets, we see that we conclude that they do intervene with each other. Then we need to review the amo-property for both routines. In `delfront()` routine the statement `head:=head.next` we assign a critical statement to the `head`. The `insert()` process reassigns `head` and therefor will the `head.next` be different based on the order of concurrency. Thus the `delfront()` routine does not satisfy the amo-property, and the processes cannot run concurrently.

