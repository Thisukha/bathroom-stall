import java.util.concurrent.*;  // Import concurrency utilities (Locks, Semaphores, BlockingQueue)
import java.util.concurrent.locks.ReentrantLock;  // Import ReentrantLock for managing stall access
import java.util.Random;  // Import Random for generating random usage times
import java.text.SimpleDateFormat;  // Import SimpleDateFormat for formatted timestamps
import java.util.Date;  // Import Date for getting the current time

public class BathroomManager {
    private final int numStalls ;  // Total number of bathroom stalls
    private final boolean[] stalls;  // Array to track if a stall is occupied (true) or free (false)
    private final ReentrantLock stallLock;  // Lock to ensure only one thread can modify the stalls array at a time
    private final Semaphore availableStalls;  // Semaphore to limit concurrent users based on available stalls
    private final BlockingQueue<Integer> waitingQueue;  // Queue to manage waiting users
    private int usersServed;  // Counter to track the number of users served
    private final ReentrantLock usersServedLock;  // Lock to ensure thread-safe updates to usersServed
    private final Random random;  // Random number generator for simulating bathroom usage time

    // For formatted timestamps
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");  // Format for timestamps

    public BathroomManager(int numStalls) {
        this.numStalls = numStalls;  // Initialize the number of stalls
        this.stalls = new boolean[numStalls];  // Initialize an array to track the availability of stalls
        this.stallLock = new ReentrantLock();  // Initialize the lock for stall operations
        this.availableStalls = new Semaphore(numStalls, true);  // Semaphore to manage the available stalls (fair ordering)
        this.waitingQueue = new LinkedBlockingQueue<>();  // Initialize the waiting queue for users
        this.usersServed = 0;  // Initialize the counter for users served
        this.usersServedLock = new ReentrantLock();  // Lock to ensure thread-safe increment of usersServed
        this.random = new Random();  // Initialize the random number generator

        // Initialize all stalls as available (false means available)
        for (int i = 0; i < numStalls; i++) {
            stalls[i] = false;  // False means the stall is free
        }
    }

    // Get current timestamp in the format HH:mm:ss
    private String getCurrentTime() {
        return timeFormat.format(new Date());  // Get the current time in the specified format
    }

    // Find an available stall
    private int findAvailableStall() {
        for (int i = 0; i < numStalls; i++) {
            if (!stalls[i]) {  // If the stall is free (false), return the stall number
                return i;
            }
        }
        return -1;  // Return -1 if no stalls are available
    }

    // Method for a person (either employee or student) to use the bathroom
    public void useBathroom(int personId, String personType) throws InterruptedException {
        // Print the current time and that the person is waiting for a stall
        String time = getCurrentTime();
        System.out.println(time + " - " + personType + " " + personId + " is waiting for a stall...");

        // Add the person to the waiting queue
        waitingQueue.put(personId);  // This blocks if the queue is full, ensuring fair handling of waiting users

        // Acquire a permit to use a stall (decreases availableStalls by 1)
        availableStalls.acquire();  // This will block until there is an available stall

        int stallNumber;
        stallLock.lock();  // Lock the stall array to avoid race conditions
        try {
            stallNumber = findAvailableStall();  // Find an available stall
            stalls[stallNumber] = true;  // Mark the stall as occupied (true means the stall is taken)
            waitingQueue.take();  // Remove the person from the waiting queue once they occupy a stall
            // Print that the person is using the stall
            System.out.println(time + " - " + personType + " " + personId + " is using stall " + (stallNumber + 1));
        } finally {
            stallLock.unlock();  // Release the lock after accessing the stalls array
        }

        // Simulate bathroom usage time (random sleep between 1000 and 3000 milliseconds)
        Thread.sleep(random.nextInt(2000) + 1000);  // Simulate using the stall for a random time

        stallLock.lock();  // Lock the stall array again to release the stall
        try {
            stalls[stallNumber] = false;  // Mark the stall as available (false means free)
            // Print that the person has finished using the stall
            System.out.println(time + " - " + personType + " " + personId + " has finished using stall " + (stallNumber + 1));
        } finally {
            stallLock.unlock();  // Release the lock after modifying the stall array
        }

        // Release the semaphore permit, increasing the number of available stalls
        availableStalls.release();  // This allows another person to acquire a stall

        // Increment the usersServed count to track the number of people served
        usersServedLock.lock();  // Lock to ensure thread-safe increment
        try {
            usersServed++;  // Increment the count of users served
        } finally {
            usersServedLock.unlock();  // Release the lock after updating the count
        }
    }

    // Get the total number of users served
    public int getUsersServed() {
        return usersServed;  // Return the total number of users served
    }
}
