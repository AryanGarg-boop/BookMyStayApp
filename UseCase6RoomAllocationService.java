import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.LinkedList;
import java.util.Queue;

/**
 * ==============================================================
 * MAIN CLASS - UseCase6RoomAllocationService
 * ==============================================================
 * * Use Case 6: Reservation Confirmation & Room Allocation
 * * To Compile: javac UseCase6RoomAllocationService.java
 * To Run:     java UseCase6RoomAllocationService
 */
public class UseCase6RoomAllocationService {

    public static void main(String[] args) {
        System.out.println("Room Allocation Processing\n");

        // Set up the FIFO queue for incoming requests
        Queue<Reservation> requestQueue = new LinkedList<>();
        requestQueue.add(new Reservation("Abhi", "Single"));
        requestQueue.add(new Reservation("Subha", "Single"));
        requestQueue.add(new Reservation("Vanmathi", "Suite"));

        // Initialize Services
        RoomInventory inventory = new RoomInventory();
        RoomAllocationService allocationService = new RoomAllocationService();

        // Process queue in FIFO order
        while (!requestQueue.isEmpty()) {
            Reservation currentRequest = requestQueue.poll();
            allocationService.allocateRoom(currentRequest, inventory);
        }
    }
}

/**
 * Service to handle safe assignment of unique rooms
 * and decrementing the inventory.
 */
class RoomAllocationService {
    private Set<String> allocatedRoomIds;
    private Map<String, Set<String>> assignedRoomsByType;

    public RoomAllocationService() {
        this.allocatedRoomIds = new HashSet<>();
        this.assignedRoomsByType = new HashMap<>();
    }

    public void allocateRoom(Reservation reservation, RoomInventory inventory) {
        String roomType = reservation.getRoomType();

        // 1. Check availability
        if (inventory.isAvailable(roomType)) {
            // 2. Generate a unique room ID
            String roomId = generateRoomId(roomType);

            // 3. Record the room ID to prevent reuse (HashSet enforces uniqueness)
            allocatedRoomIds.add(roomId);

            // Map the room type to its allocated ID
            assignedRoomsByType.putIfAbsent(roomType, new HashSet<>());
            assignedRoomsByType.get(roomType).add(roomId);

            // 4. Update inventory immediately
            inventory.decrementInventory(roomType);

            // 5. Confirm reservation
            System.out.println("Booking confirmed for Guest: " + reservation.getGuestName() +
                    ", Room ID: " + roomId);
        } else {
            System.out.println("Booking failed for Guest: " + reservation.getGuestName() +
                    " - No " + roomType + " rooms available.");
        }
    }

    private String generateRoomId(String roomType) {
        int counter = 1;
        String newRoomId;

        // Loop ensures we never double-book an ID
        do {
            newRoomId = roomType + "-" + counter;
            counter++;
        } while (allocatedRoomIds.contains(newRoomId));

        return newRoomId;
    }
}

/**
 * Data model for a booking request.
 */
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }
}

/**
 * Centralized tracker for available rooms.
 */
class RoomInventory {
    private Map<String, Integer> availableRooms;

    public RoomInventory() {
        availableRooms = new HashMap<>();
        // Mock inventory loaded for the test
        availableRooms.put("Single", 5);
        availableRooms.put("Suite", 2);
    }

    public boolean isAvailable(String roomType) {
        return availableRooms.getOrDefault(roomType, 0) > 0;
    }

    public void decrementInventory(String roomType) {
        if (isAvailable(roomType)) {
            availableRooms.put(roomType, availableRooms.get(roomType) - 1);
        }
    }
}