import java.util.Scanner;

/**
 * =================================================================
 * CLASS - InvalidBookingException
 * =================================================================
 * This custom exception represents invalid booking scenarios.
 */
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

/**
 * =================================================================
 * CLASS - RoomInventory (Mock for validation context)
 * =================================================================
 */
class RoomInventory {
    // In a full system, this would manage actual room counts
}

/**
 * =================================================================
 * CLASS - BookingRequestQueue (Mock for validation context)
 * =================================================================
 */
class BookingRequestQueue {
    // In a full system, this would handle the queue of requests
}

/**
 * =================================================================
 * CLASS - ReservationValidator
 * =================================================================
 * Responsible for validating booking requests before they are processed.
 */
class ReservationValidator {
    /**
     * Validates booking input provided by the user.
     * @throws InvalidBookingException if validation fails
     */
    public void validate(String guestName, String roomType, RoomInventory inventory)
            throws InvalidBookingException {

        // Case-sensitive validation as per the project requirements
        if (!(roomType.equals("Single") || roomType.equals("Double") || roomType.equals("Suite"))) {
            throw new InvalidBookingException("Invalid room type selected.");
        }

        if (guestName == null || guestName.trim().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty.");
        }
    }
}

/**
 * =================================================================
 * MAIN CLASS - UseCase9ErrorHandlingValidation
 * =================================================================
 */
public class UseCase9ErrorHandlingValidation {

    public static void main(String[] args) {
        // Display application header
        System.out.println("Booking Validation");

        Scanner scanner = new Scanner(System.in);

        // Initialize required components
        RoomInventory inventory = new RoomInventory();
        ReservationValidator validator = new ReservationValidator();
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        try {
            // Get User Input
            System.out.print("Enter guest name: ");
            String guestName = scanner.nextLine();

            System.out.print("Enter room type (Single/Double/Suite): ");
            String roomType = scanner.nextLine();

            // Perform Validation (Fail-Fast)
            validator.validate(guestName, roomType, inventory);

            // If validation passes, the "Happy Path" continues
            System.out.println("Booking successful for " + guestName + " (" + roomType + ")");

        } catch (InvalidBookingException e) {
            // Handle domain-specific validation errors gracefully
            System.out.println("Booking failed: " + e.getMessage());
        } catch (Exception e) {
            // Handle unexpected errors
            System.out.println("An unexpected error occurred: " + e.getMessage());
        } finally {
            // Ensure resources are closed regardless of success or failure
            scanner.close();
        }
    }
}