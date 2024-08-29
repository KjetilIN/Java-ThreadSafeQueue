# Oblig 1: Answers to question 1 and 2.

Course: IN5170
StudentNr: 686716

# Question 1

> Identifying V and W sets of shared variables in the three routines

Each set is defined by the following: 
```math
\omega: \text{set of variables where each variable is a global read variable} \newline
\nu: \text{set of variables where each variable is a global write variable }
```

The `find(d)` routine contains read and no write to global references. It reads the head as a local variable and then traverses down the three. `i` is a local variable. Thus the W set (write variables are empty): 
- head is in the V set (reading head)

V := {head}
W := Ø


In the `insert(new)` routine. 
- tail is in the V set (reading the tail)
- head is in the W set (writing the head)
- tail is in the W set (writing to the new tail)
- tail.next is in W set (writing the next node of the tail) 

V := {tail}
W := {head, tail, tail.next}

In the `delfront()` routine.
- head is in the V set
- tail is in the V set
- head is in the W set 
- tail is in the W set
- head.next is in the V set

V := {head, tail, head.tail}
W := {head, tail}

# Question 2 

For question two, we assume several processes access the linked list: 

## A. Which combinations of routines can be executed concurrently without interference?


### Find & Find

The `find(d)` routine can run concurrently with itself. It only reads head, and does no writing. There is thus interference freedom and the processes can run concurrently: 

```math
V_{find} \cap V_{find} = \emptyset \newline
```

### Insert & Insert

For `insert(new)` and `insert(new)` routines, we check each set: 

```math
V_{insert} \cap W_{insert} = \{\text{tail}\} \newline

V_{insert} \cap W_{insert} \neq \emptyset
```

Checking the sets, we see that we conclude that they do intervene with each other. Thus we need to check the at-most-once property of both routines. Each statement in the `insert(new)` routine is assignment to a non-critical reference. Thus the amo-property is held.

Since both routines satisfies the amo-property, the two routines can run concurrently.

### Delfront & Delfront

For `delfront()` and `delfront()` routines, we check each set: 

```math
V_{delfront} \cap W_{delfront} = \{\text{head, tail}\} \newline

V_{delfront} \cap W_{delfront} \neq \emptyset
```

Checking the sets, we see that we conclude that they do intervene with each other. Thus we need to check the at-most-once property of both routines. In the routine the statement `head := head.next;` assigns a critical reference and the variable itself is used by the other routine. Thus, the routine does not satisfy the amo-property, and two `delfront()` routines can not run concurrently.

### Find & Insert
For the `find(d)` and `insert(d)` routine. We need to first check each set:

```math
V_{find} \cap W_{insert} = \{\text{head}\} \newline
V_{insert} \cap W_{find} = \emptyset \newline

V_{find} \cap W_{insert} \neq V_{insert} \cap W_{find}
```

Checking the sets, we see that we conclude that they do intervene with each other. Thus we need to check the at-most-once property of both routines.

In the `find(d)` routine the `i := head;` statement does not satisfy the amo-property, because `head` is a critical reference that could be changed by the `insert(d)` routine. Therefore, the two processes cannot run concurrently. 

### Find & Delfront

For `find(d)` and `delfront()` routines, we check each set: 

```math
V_{find} \cap W_{delfront} = \{\text{head}\} \newline

V_{delfront} \cap W_{find} = \emptyset \newline

V_{find} \cap W_{delfront} \neq V_{delfront} \cap W_{find}
```

Checking the sets, we see that we conclude that they do intervene with each other. In the `find(d)` routine the statement `i:= head` we assign a critical reference. `head` is reassigned by the `delfront()` process. This means that the `find(d)` routine does not satisfy the amo-property. Therefore we can conclude that these two can not run concurrently.

### Insert & Delfront 

For `insert(d)` and `delfront()` routines, we check each set: 

```math
V_{insert} \cap W_{delfront} = \{\text{tail}\} \newline

V_{delfront} \cap W_{insert} = \{\text{head, tail}\} \newline

V_{find} \cap W_{delfront} \neq V_{delfront} \cap W_{find}
```




### Summary 

- Find and Find can run concurrently 
- Insert and Insert can run concurrently


