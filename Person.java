import java.util.Random;  // Import the Random class to generate random numbers, used for delays

public class Person implements Runnable {
    private final int id;  // Unique identifier for each person (Employee or Student)
    private final BathroomManager bathroom;  // Reference to the BathroomManager that handles bathroom usage
    private final String type;  // The type of person (Employee or Student)
    private final Random random;  // Random object to generate random numbers for delays

    // Constructor to initialize the Person object with an ID, bathroom reference, and type (Employee/Student)
    public Person(int id, BathroomManager bathroom, String type) {
        this.id = id;  // Set the person's unique ID
        this.bathroom = bathroom;  // Assign the bathroom reference
        this.type = type;  // Assign the type of person (Employee or Student)
        this.random = new Random();  // Create a Random object for generating random numbers
    }

    // The run method that defines the task for the thread
    @Override
    public void run() {
        try {
            // Random delay (0-5 seconds) before the person needs to use the bathroom
            Thread.sleep(random.nextInt(5000));  // The person waits for a random time between 0 and 5000 milliseconds (5 seconds)

            // Request to use the bathroom by calling the useBathroom method on the BathroomManager object
            bathroom.useBathroom(id, type);  // This method is called to simulate the person using the bathroom
        } catch (InterruptedException e) {  // Catch an InterruptedException if the thread is interrupted while sleeping or waiting
            Thread.currentThread().interrupt();  // Restore the interrupted status for the current thread
            System.err.println("Person " + id + " was interrupted: " + e.getMessage());  // Print an error message if the person was interrupted
        }
    }
}
