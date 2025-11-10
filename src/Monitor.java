/**
 * Class Monitor
 * To synchronize dining philosophers.
 *
 * @author Serguei A. Mokhov, mokhov@cs.concordia.ca
 */
public class Monitor
{
    /*
     * ------------
     * Data members
     * ------------
     */

    // Number of philosophers
    private int numPhilosophers;

    // State of each chopstick (true = available, false = in use)
    private boolean[] chopstickAvailable;

    // Track if someone is currently talking
    private boolean isSomeoneTalking;

    // Track the order philosophers requested chopsticks (for starvation prevention)
    private int[] waitingQueue;
    private int queueHead;
    private int queueTail;
    private int queueSize;

    /**
     * Constructor
     */
    public Monitor(int piNumberOfPhilosophers)
    {
        // TODO: set appropriate number of chopsticks based on the # of philosophers
        // Set appropriate number of chopsticks based on the # of philosophers
        this.numPhilosophers = piNumberOfPhilosophers;

        // Initialize all chopsticks as available
        this.chopstickAvailable = new boolean[piNumberOfPhilosophers];
        for(int i = 0; i < piNumberOfPhilosophers; i++)
        {
            chopstickAvailable[i] = true;
        }

        // Initialize talking state - no one is talking initially
        this.isSomeoneTalking = false;

        // Initialize waiting queue for FIFO ordering (prevents starvation)
        this.waitingQueue = new int[piNumberOfPhilosophers];
        this.queueHead = 0;
        this.queueTail = 0;
        this.queueSize = 0;
    }

    /*
     * -------------------------------
     * User-defined monitor procedures
     * -------------------------------
     */

    /**
     * Grants request (returns) to eat when both chopsticks/forks are available.
     * Else forces the philosopher to wait()
     */
    public synchronized void pickUp(final int piTID)
    {
        // ...

        // Calculate chopstick indices for this philosopher
        int philosopherIndex = piTID - 1; // Convert TID to 0-based index
        int leftChopstick = philosopherIndex;
        int rightChopstick = (philosopherIndex + 1) % numPhilosophers;

        // Add philosopher to the waiting queue
        enqueue(piTID);

        // Wait until:
        // 1. Both chopsticks are available
        // 2. This philosopher is at the front of the queue (FIFO - prevents starvation)
        while(!areBothChopsticksAvailable(leftChopstick, rightChopstick) ||
                !isPhilosopherFirst(piTID))
        {
            try
            {
                wait(); // Release lock and wait for notification
            }
            catch(InterruptedException e)
            {
                System.err.println("Monitor.pickUp():");
                DiningPhilosophers.reportException(e);
            }
        }

        // Atomically pick up both chopsticks
        chopstickAvailable[leftChopstick] = false;
        chopstickAvailable[rightChopstick] = false;

        // Remove philosopher from waiting queue
        dequeue();

        // Debug output
        System.out.println("Philosopher " + piTID + " picked up chopsticks " +
                (leftChopstick + 1) + " and " + (rightChopstick + 1));
    }

    /**
     * When a given philosopher's done eating, they put the chopstiks/forks down
     * and let others know they are available.
     */
    public synchronized void putDown(final int piTID)
    {
        // ...
        // Calculate chopstick indices for this philosopher
        int philosopherIndex = piTID - 1; // Convert TID to 0-based index
        int leftChopstick = philosopherIndex;
        int rightChopstick = (philosopherIndex + 1) % numPhilosophers;

        // Put down both chopsticks (make them available)
        chopstickAvailable[leftChopstick] = true;
        chopstickAvailable[rightChopstick] = true;

        // Debug output
        System.out.println("Philosopher " + piTID + " put down chopsticks " +
                (leftChopstick + 1) + " and " + (rightChopstick + 1));

        // Notify all waiting philosophers that chopsticks are now available
        notifyAll();
    }

    /**
     * Only one philopher at a time is allowed to philosophy
     * (while she is not eating).
     */
    public synchronized void requestTalk()
    {
        // ...
        // Wait while someone else is talking
        while(isSomeoneTalking)
        {
            try
            {
                wait(); // Release lock and wait until talking slot is free
            }
            catch(InterruptedException e)
            {
                System.err.println("Monitor.requestTalk():");
                DiningPhilosophers.reportException(e);
            }
        }

        // Grant permission to talk
        isSomeoneTalking = true;
    }

    /**
     * When one philosopher is done talking stuff, others
     * can feel free to start talking.
     */
    public synchronized void endTalk()
    {
        // ...
        // Release the talking slot
        isSomeoneTalking = false;

        // Notify all waiting philosophers that they can now request to talk
        notifyAll();
    }

    /*
     * -------------------------------
     * Helper methods
     * -------------------------------
     */

    /**
     * Check if both chopsticks are available
     */
    private boolean areBothChopsticksAvailable(int left, int right)
    {
        return chopstickAvailable[left] && chopstickAvailable[right];
    }

    /**
     * Check if the given philosopher is first in the waiting queue
     * This ensures FIFO ordering and prevents starvation
     */
    private boolean isPhilosopherFirst(int piTID)
    {
        if(queueSize == 0)
        {
            return false;
        }
        return waitingQueue[queueHead] == piTID;
    }

    /**
     * Add philosopher to the waiting queue
     */
    private void enqueue(int piTID)
    {
        // Only add if not already in queue
        if(!isInQueue(piTID))
        {
            waitingQueue[queueTail] = piTID;
            queueTail = (queueTail + 1) % numPhilosophers;
            queueSize++;
        }
    }

    /**
     * Remove philosopher from the front of the waiting queue
     */
    private void dequeue()
    {
        if(queueSize > 0)
        {
            queueHead = (queueHead + 1) % numPhilosophers;
            queueSize--;
        }
    }

    /**
     * Check if philosopher is already in the queue
     */
    private boolean isInQueue(int piTID)
    {
        int index = queueHead;
        for(int i = 0; i < queueSize; i++)
        {
            if(waitingQueue[index] == piTID)
            {
                return true;
            }
            index = (index + 1) % numPhilosophers;
        }
        return false;
    }
}

// EOF