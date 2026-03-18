import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * =========================================================================
 * CLASS - Service (AddOnService)
 * =========================================================================
 * Use Case 7: Add-On Service Selection
 * * Description:
 * This class represents an optional service
 * that can be added to a confirmed reservation.
 * * Examples:
 * - Breakfast
 * - Spa
 * - Airport Pickup
 * * @version 7.0
 */
class Service {
    private String serviceName;
    private double cost;

    /**
     * Creates a new add-on service.
     * @param serviceName name of the service
     * @param cost cost of the service
     */
    public Service(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    /** @return service name */
    public String getServiceName() {
        return serviceName;
    }

    /** @return service cost */
    public double getCost() {
        return cost;
    }
}

/**
 * =========================================================================
 * CLASS - AddOnServiceManager
 * =========================================================================
 * Description:
 * This class manages optional services
 * associated with confirmed reservations.
 * * It supports attaching multiple services
 * to a single reservation.
 */
class AddOnServiceManager {
    // Maps reservation ID to selected services
    // Key   -> Reservation ID
    // Value -> List of selected services
    private Map<String, List<Service>> servicesByReservation;

    public AddOnServiceManager() {
        servicesByReservation = new HashMap<>();
    }

    /**
     * Attaches a service to a reservation.
     * @param reservationId confirmed reservation ID
     * @param service add-on service
     */
    public void addService(String reservationId, Service service) {
        // If the reservation doesn't have a list yet, create one
        servicesByReservation.putIfAbsent(reservationId, new ArrayList<>());
        // Add the service to the list associated with this ID
        servicesByReservation.get(reservationId).add(service);
    }

    /**
     * Calculates total add-on cost for a reservation.
     * @param reservationId reservation ID
     * @return total service cost
     */
    public double calculateTotalServiceCost(String reservationId) {
        List<Service> services = servicesByReservation.get(reservationId);
        if (services == null || services.isEmpty()) {
            return 0.0;
        }

        double total = 0.0;
        for (Service s : services) {
            total += s.getCost();
        }
        return total;
    }
}

/**
 * =========================================================================
 * MAIN CLASS - UseCase7AddOnServiceSelection
 * =========================================================================
 * Description:
 * This class demonstrates how optional
 * services can be attached to a confirmed
 * booking.
 * * Services are added after room allocation
 * and do not affect inventory.
 */
public class UseCase7AddOnServiceSelection {

    public static void main(String[] args) {
        // Initialize Manager
        AddOnServiceManager manager = new AddOnServiceManager();

        // Define available services
        Service breakfast = new Service("Breakfast", 500.0);
        Service spa = new Service("Spa", 1000.0);

        // Simulation: Existing Reservation ID "Single-1"
        String reservationId = "Single-1";

        // Guest selects services
        manager.addService(reservationId, breakfast);
        manager.addService(reservationId, spa);

        // Output results as shown in the console snapshot
        System.out.println("Add-On Service Selection");
        System.out.println("Reservation ID: " + reservationId);

        double totalCost = manager.calculateTotalServiceCost(reservationId);
        System.out.println("Total Add-On Cost: " + totalCost);
    }
}