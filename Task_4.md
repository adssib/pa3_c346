## Progrmaming Assingment 3:

Made By: 
- Adib Akkari 40216815
- Omar Elmasaoudi 40255123

## Task 4: Starvation Prevention Explanation

My implementation prevents starvation by using a **FIFO (First-In-First-Out) waiting queue** to manage philosophers requesting chopsticks. When a philosopher wants to eat, they are added to the queue in the `pickUp()` method, and chopsticks are only granted to the philosopher at the front of the queue when both chopsticks become available. This ensures that every philosopher will eventually reach the front of the queue and be served, guaranteeing an **upper bound on waiting time**.

### Key Mechanisms:

1. **FIFO Queue Structure**: The Monitor class maintains a circular queue (`waitingQueue[]`) with `queueHead` and `queueTail` pointers to track the order of requests.

2. **Conditional Access**: The `pickUp()` method checks two conditions before allowing a philosopher to proceed:
   - Both required chopsticks must be available (`areBothChopsticksAvailable()`)
   - The philosopher must be first in the waiting queue (`isPhilosopherFirst()`)

3. **Notification Mechanism**: When a philosopher puts down chopsticks using `putDown()`, all waiting philosophers are notified via `notifyAll()`, allowing the next philosopher in queue to check if they can proceed.

4. **Queue Management**: The `enqueue()` method prevents duplicate entries, and `dequeue()` removes philosophers from the front once they acquire chopsticks.

### Why This Prevents Starvation:

This FIFO ordering prevents any philosopher from being indefinitely bypassed by others, ensuring **fairness** and **bounded waiting**. Since philosophers are served strictly in the order they request resources, no philosopher can "cut in line" or monopolize access to chopsticks. Every philosopher is guaranteed to eventually reach the front of the queue and acquire the necessary resources to eat, thus eliminating the possibility of starvation.

The combination of atomic operations (via `synchronized` methods), proper condition checking (via `while` loops in `wait()`), and fair queuing ensures both **deadlock-free** and **starvation-free** execution.
