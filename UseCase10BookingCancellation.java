import java.util.*;

/**
 * =================================================================
 * CLASS - RoomInventory (Mocked for Use Case 10)
 * =================================================================
 */
class RoomInventory {
    private Map<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        // Initializing with some dummy data
        inventory.put("Single", 5);
        inventory.put("Double", 3);
        inventory.put("Suite", 2);
    }

    public void incrementInventory(String roomType) {
        inventory.put(roomType, inventory.get(roomType) + 1);
    }

    public int getAvailableCount(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }
}

/**
 * =================================================================
 * CLASS - CancellationService
 * =================================================================
 * Responsible for handling booking cancellations and inventory rollback.
 */
class CancellationService {
    // Stack tracks released reservation IDs (Rollback history)
    private Stack<String> releasedReservationIds;
    // Map stores ReservationID -> RoomType for lookup during cancellation
    private Map<String, String> reservationRoomTypeMap;

    public CancellationService() {
        this.releasedReservationIds = new Stack<>();
        this.reservationRoomTypeMap = new HashMap<>();
    }

    /**
     * Registers a confirmed booking to allow for future cancellation.
     */
    public void registerBooking(String reservationId, String roomType) {
        reservationRoomTypeMap.put(reservationId, roomType);
    }

    /**
     * Cancels a confirmed booking and restores inventory safely.
     */
    public void cancelBooking(String reservationId, RoomInventory inventory) {
        // Validation: Ensure the reservation exists
        if (!reservationRoomTypeMap.containsKey(reservationId)) {
            System.out.println("Error: Reservation ID " + reservationId + " not found.");
            return;
        }

        // Get the room type associated with this reservation
        String roomType = reservationRoomTypeMap.get(reservationId);

        // Perform Rollback Logic
        inventory.incrementInventory(roomType);
        releasedReservationIds.push(reservationId); // Track in stack
        reservationRoomTypeMap.remove(reservationId); // Remove from active bookings

        System.out.println("Booking cancelled successfully. Inventory restored for room type: " + roomType);
    }

    /**
     * Displays recently cancelled reservations using LIFO order.
     */
    public void showRollbackHistory() {
        System.out.println("\nRollback History (Most Recent First):");
        if (releasedReservationIds.isEmpty()) {
            System.out.println("No cancellations recorded.");
            return;
        }

        // Stacks naturally provide LIFO order
        for (int i = releasedReservationIds.size() - 1; i >= 0; i--) {
            System.out.println("Released Reservation ID: " + releasedReservationIds.get(i));
        }
    }
}

/**
 * =================================================================
 * MAIN CLASS - UseCase10BookingCancellation
 * =================================================================
 */
public class UseCase10BookingCancellation {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        RoomInventory inventory = new RoomInventory();
        CancellationService cancellationService = new CancellationService();

        System.out.println("Booking Cancellation");

        // Simulating some existing bookings in the system
        cancellationService.registerBooking("Single-1", "Single");
        cancellationService.registerBooking("Double-5", "Double");

        // User Input for Cancellation
        System.out.print("Enter Reservation ID to cancel (e.g., Single-1): ");
        String resId = scanner.nextLine();

        // Process Cancellation
        cancellationService.cancelBooking(resId, inventory);

        // Display Rollback History
        cancellationService.showRollbackHistory();

        // Display Updated Inventory State
        // Assuming we want to see the status of 'Single' as per your screenshot example
        System.out.println("\nUpdated Single Room Availability: " + inventory.getAvailableCount("Single"));

        scanner.close();
    }
}