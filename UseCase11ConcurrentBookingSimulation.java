import java.util.*;

/**
 * =================================================================
 * DOMAIN CLASSES (Simplified for UC11 Independence)
 * =================================================================
 */
class Reservation {
    String guestName;
    String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }
}

class RoomInventory {
    private Map<String, Integer> inventory = new HashMap<>();
    private Map<String, Integer> allocatedCount = new HashMap<>();

    public RoomInventory() {
        inventory.put("Single", 5);
        inventory.put("Double", 3);
        inventory.put("Suite", 2);
        allocatedCount.put("Single", 0);
        allocatedCount.put("Double", 0);
        allocatedCount.put("Suite", 0);
    }

    public int getAvailable(String type) { return inventory.getOrDefault(type, 0); }

    public void allocate(String type) {
        inventory.put(type, inventory.get(type) - 1);
        allocatedCount.put(type, allocatedCount.get(type) + 1);
    }

    public int getAllocatedId(String type) { return allocatedCount.get(type); }

    public void displayRemaining() {
        System.out.println("\nRemaining Inventory:");
        inventory.forEach((k, v) -> System.out.println(k + ": " + v));
    }
}

class BookingRequestQueue {
    private Queue<Reservation> queue = new LinkedList<>();
    public void addRequest(Reservation res) { queue.add(res); }
    public Reservation getNext() { return queue.poll(); }
    public boolean isEmpty() { return queue.isEmpty(); }
}

class RoomAllocationService {
    public void allocateRoom(Reservation res, RoomInventory inventory) {
        if (inventory.getAvailable(res.roomType) > 0) {
            inventory.allocate(res.roomType);
            System.out.println("Booking confirmed for Guest: " + res.guestName +
                    ", Room ID: " + res.roomType + "-" + inventory.getAllocatedId(res.roomType));
        } else {
            System.out.println("Booking failed for " + res.guestName + ": No " + res.roomType + " rooms available.");
        }
    }
}

/**
 * =================================================================
 * CLASS - ConcurrentBookingProcessor
 * =================================================================
 */
class ConcurrentBookingProcessor implements Runnable {
    private BookingRequestQueue bookingQueue;
    private RoomInventory inventory;
    private RoomAllocationService allocationService;

    public ConcurrentBookingProcessor(BookingRequestQueue q, RoomInventory inv, RoomAllocationService svc) {
        this.bookingQueue = q;
        this.inventory = inv;
        this.allocationService = svc;
    }

    @Override
    public void run() {
        while (true) {
            Reservation reservation = null;

            // CRITICAL SECTION 1: Synchronized access to the shared Queue
            synchronized (bookingQueue) {
                if (bookingQueue.isEmpty()) {
                    break; // No more requests to process
                }
                reservation = bookingQueue.getNext();
            }

            if (reservation != null) {
                // CRITICAL SECTION 2: Synchronized access to shared Inventory
                // This prevents "Double Booking" where two threads see 1 room left and both take it.
                synchronized (inventory) {
                    allocationService.allocateRoom(reservation, inventory);
                }
            }

            // Small sleep to simulate processing time and encourage thread interleaving
            try { Thread.sleep(100); } catch (InterruptedException e) { break; }
        }
    }
}

/**
 * =================================================================
 * MAIN CLASS - UseCase11ConcurrentBookingSimulation
 * =================================================================
 */
public class UseCase11ConcurrentBookingSimulation {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        RoomInventory inventory = new RoomInventory();
        BookingRequestQueue bookingQueue = new BookingRequestQueue();
        RoomAllocationService allocationService = new RoomAllocationService();

        System.out.println("Concurrent Booking Simulation");
        System.out.print("How many bookings would you like to enter? ");
        int count = Integer.parseInt(scanner.nextLine());

        for (int i = 0; i < count; i++) {
            System.out.println("\nBooking #" + (i + 1));
            System.out.print("Enter guest name: ");
            String name = scanner.nextLine();
            System.out.print("Enter room type (Single/Double/Suite): ");
            String type = scanner.nextLine();
            bookingQueue.addRequest(new Reservation(name, type));
        }

        // Create two threads to process the shared queue concurrently
        Thread t1 = new Thread(new ConcurrentBookingProcessor(bookingQueue, inventory, allocationService));
        Thread t2 = new Thread(new ConcurrentBookingProcessor(bookingQueue, inventory, allocationService));

        System.out.println("\nStarting concurrent processing...\n");
        t1.start();
        t2.start();

        try {
            // Wait for both threads to finish
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            System.out.println("Thread execution interrupted.");
        }

        inventory.displayRemaining();
        scanner.close();
    }
}