import java.io.*;
import java.util.*;

/**
 * =================================================================
 * CLASS - HotelSystemState (The "Snapshot" Object)
 * =================================================================
 * This class holds all data that needs to be persisted.
 * Must implement Serializable to be written to a file.
 */
class HotelSystemState implements Serializable {
    private static final long serialVersionUID = 1L;

    // System data to be saved
    public Map<String, Integer> inventory = new HashMap<>();
    public List<String> bookingHistory = new ArrayList<>();

    public HotelSystemState() {
        // Default initial state if no file is found
        inventory.put("Single", 5);
        inventory.put("Double", 3);
        inventory.put("Suite", 2);
    }
}

/**
 * =================================================================
 * CLASS - PersistenceService
 * =================================================================
 * Handles the saving and loading logic.
 */
class PersistenceService {
    private static final String FILE_NAME = "hotel_state.ser";

    /**
     * Serializes the state to a file.
     */
    public void saveState(HotelSystemState state) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(state);
            System.out.println("System state persisted successfully to " + FILE_NAME);
        } catch (IOException e) {
            System.err.println("Error saving state: " + e.getMessage());
        }
    }

    /**
     * Deserializes the state from a file.
     */
    public HotelSystemState loadState() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            System.out.println("No persistence file found. Starting with a fresh state.");
            return new HotelSystemState();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (HotelSystemState) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading state (file might be corrupted). Resetting state.");
            return new HotelSystemState();
        }
    }
}

/**
 * =================================================================
 * MAIN CLASS - UseCase12DataPersistenceRecovery
 * =================================================================
 */
public class UseCase12DataPersistenceRecovery {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        PersistenceService persistenceService = new PersistenceService();

        // RECOVERY: Load data from previous session
        System.out.println("Book My Stay - System Recovery");
        HotelSystemState state = persistenceService.loadState();

        // Display current loaded state
        System.out.println("\n--- Current System State ---");
        System.out.println("Inventory: " + state.inventory);
        System.out.println("Total Bookings in History: " + state.bookingHistory.size());
        if (!state.bookingHistory.isEmpty()) {
            System.out.println("Last Booking: " + state.bookingHistory.get(state.bookingHistory.size() - 1));
        }
        System.out.println("----------------------------\n");

        // Simple loop for user interaction
        while (true) {
            System.out.println("1. Make a Booking");
            System.out.println("2. View History");
            System.out.println("3. Exit & Save");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                System.out.print("Enter guest name: ");
                String name = scanner.nextLine();
                System.out.print("Enter room type (Single/Double/Suite): ");
                String type = scanner.nextLine();

                if (state.inventory.containsKey(type) && state.inventory.get(type) > 0) {
                    state.inventory.put(type, state.inventory.get(type) - 1);
                    String record = "Guest: " + name + " | Room: " + type;
                    state.bookingHistory.add(record);
                    System.out.println("Booking successful!");
                } else {
                    System.out.println("Booking failed: Room unavailable or invalid type.");
                }

            } else if (choice.equals("2")) {
                System.out.println("\n--- Booking History ---");
                if (state.bookingHistory.isEmpty()) System.out.println("No records found.");
                for (String record : state.bookingHistory) {
                    System.out.println(record);
                }
                System.out.println();

            } else if (choice.equals("3")) {
                // PERSISTENCE: Save data before shutting down
                persistenceService.saveState(state);
                System.out.println("Shutting down safely. Goodbye!");
                break;
            } else {
                System.out.println("Invalid option.");
            }
        }

        scanner.close();
    }
}