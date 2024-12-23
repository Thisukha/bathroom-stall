import java.util.concurrent.*;  // Import concurrency utilities for handling threads, executors, and time-based tasks

public class BathroomSimulation {
    public static void main(String[] args) {
        // Initialize the bathroom with 6 stalls
        BathroomManager bathroom = new BathroomManager(6);  // Create a BathroomManager object with 6 stalls

        // Create an ExecutorService with a fixed thread pool of size 100
        ExecutorService executor = Executors.newFixedThreadPool(100);  // Allows managing multiple threads efficiently, here a pool of 100 threads

        // Create and start 60 employees
        for (int i = 1; i <= 60; i++) {
            executor.submit(new Person(i, bathroom, "Employee"));  // Submit a task for each employee to use the bathroom
        }

        // Create and start 40 students
        for (int i = 1; i <= 40; i++) {
            executor.submit(new Person(i, bathroom, "Student"));  // Submit a task for each student to use the bathroom
        }

        // Shutdown the executor and wait for all tasks to complete
        executor.shutdown();  // Shut down the executor so no more tasks can be submitted

        try {
            // Wait for all tasks to finish or until 1 minute has passed
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) { 
                System.out.println("Some tasks did not complete before timeout.");  // Print a message if some tasks were not finished within the timeout
            }
        } catch (InterruptedException e) {
            // Handle interruption if the main thread is interrupted while waiting for tasks to finish
            System.err.println("Main thread was interrupted: " + e.getMessage());  // Print the error message if interrupted
        }

        // Print the total number of users served after the simulation
        System.out.println("\nSimulation complete! Total users served: " + bathroom.getUsersServed());  // Display the final count of users who were served
    }
}
