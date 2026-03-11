import java.util.*;

// Custom exception for invalid booking scenarios
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// Reservation class
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

// Inventory Service with validation
class InventoryService {
    private Map<String, Integer> roomInventory = new HashMap<>();

    public void addRoomType(String roomType, int count) {
        roomInventory.put(roomType, count);
    }

    public boolean isValidRoomType(String roomType) {
        return roomInventory.containsKey(roomType);
    }

    public boolean isAvailable(String roomType) {
        return roomInventory.getOrDefault(roomType, 0) > 0;
    }

    public void decrement(String roomType) throws InvalidBookingException {
        int current = roomInventory.getOrDefault(roomType, 0);
        if (current <= 0) {
            throw new InvalidBookingException("No rooms available for " + roomType);
        }
        roomInventory.put(roomType, current - 1);
    }
}

// Booking Service with validation and error handling
class BookingService {
    private InventoryService inventory;

    public BookingService(InventoryService inventory) {
        this.inventory = inventory;
    }

    public void confirmReservation(Reservation reservation) {
        try {
            // Validate room type
            if (!inventory.isValidRoomType(reservation.getRoomType())) {
                throw new InvalidBookingException("Invalid room type: " + reservation.getRoomType());
            }

            // Validate availability
            if (!inventory.isAvailable(reservation.getRoomType())) {
                throw new InvalidBookingException("Room type " + reservation.getRoomType() + " is fully booked.");
            }

            // Perform allocation
            inventory.decrement(reservation.getRoomType());
            System.out.println("Reservation confirmed: " + reservation);

        } catch (InvalidBookingException e) {
            // Graceful failure handling
            System.out.println("Reservation failed for " + reservation.getGuestName() + ": " + e.getMessage());
        }
    }
}

public class BookRequest {
    public static void main(String[] args) {
        InventoryService inventory = new InventoryService();
        inventory.addRoomType("Deluxe", 1);
        inventory.addRoomType("Suite", 0); // No suites available

        BookingService bookingService = new BookingService(inventory);

        // Valid reservation
        Reservation res1 = new Reservation("Alice", "Deluxe");
        bookingService.confirmReservation(res1);

        // Invalid room type
        Reservation res2 = new Reservation("Bob", "Penthouse");
        bookingService.confirmReservation(res2);

        // Fully booked room type
        Reservation res3 = new Reservation("Charlie", "Suite");
        bookingService.confirmReservation(res3);

        // Attempt to book Deluxe again (should fail because only 1 was available)
        Reservation res4 = new Reservation("Diana", "Deluxe");
        bookingService.confirmReservation(res4);
    }
}
