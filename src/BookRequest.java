import java.util.*;

// Represents an optional add-on service
class AddOnService {
    private String serviceName;
    private double cost;

    public AddOnService(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return serviceName + " (₹" + cost + ")";
    }
}

// Manages mapping between reservations and selected services
class AddOnServiceManager {
    private Map<String, List<AddOnService>> reservationServices = new HashMap<>();

    // Attach services to a reservation
    public void addServices(String reservationId, List<AddOnService> services) {
        reservationServices.putIfAbsent(reservationId, new ArrayList<>());
        reservationServices.get(reservationId).addAll(services);
        System.out.println("Services added for Reservation ID " + reservationId + ": " + services);
    }

    // Calculate total additional cost
    public double calculateAdditionalCost(String reservationId) {
        List<AddOnService> services = reservationServices.getOrDefault(reservationId, Collections.emptyList());
        return services.stream().mapToDouble(AddOnService::getCost).sum();
    }

    // Retrieve services for a reservation
    public List<AddOnService> getServices(String reservationId) {
        return reservationServices.getOrDefault(reservationId, Collections.emptyList());
    }
}

// Reservation class extended with reservation ID
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.reservationId = UUID.randomUUID().toString();
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    @Override
    public String toString() {
        return "Reservation [ID: " + reservationId + ", Guest: " + guestName + ", Room Type: " + roomType + "]";
    }
}

public class BookRequest {
    public static void main(String[] args) {
        // Example reservation
        Reservation res1 = new Reservation("Alice", "Deluxe");

        // Add-on services
        AddOnService breakfast = new AddOnService("Breakfast", 500);
        AddOnService spa = new AddOnService("Spa Access", 1500);
        AddOnService airportPickup = new AddOnService("Airport Pickup", 1000);

        // Service Manager
        AddOnServiceManager serviceManager = new AddOnServiceManager();

        // Guest selects services
        serviceManager.addServices(res1.getReservationId(), Arrays.asList(breakfast, spa));

        // Display reservation details
        System.out.println("\nReservation Details:");
        System.out.println(res1);
        System.out.println("Selected Services: " + serviceManager.getServices(res1.getReservationId()));
        System.out.println("Additional Cost: ₹" + serviceManager.calculateAdditionalCost(res1.getReservationId()));

        // Another reservation with different services
        Reservation res2 = new Reservation("Bob", "Suite");
        serviceManager.addServices(res2.getReservationId(), Arrays.asList(airportPickup));

        System.out.println("\nReservation Details:");
        System.out.println(res2);
        System.out.println("Selected Services: " + serviceManager.getServices(res2.getReservationId()));
        System.out.println("Additional Cost: ₹" + serviceManager.calculateAdditionalCost(res2.getReservationId()));
    }
}
