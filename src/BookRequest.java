import java.util.*;

// Reservation class (same as Use Case 5)
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

    @Override
    public String toString() {
        return "Reservation [Guest: " + guestName + ", Room Type: " + roomType + "]";
    }
}

// Booking Request Queue (FIFO)
class BookingRequestQueue {
    private Queue<Reservation> requestQueue = new LinkedList<>();

    public void addRequest(Reservation reservation) {
        requestQueue.add(reservation);
        System.out.println("Request added: " + reservation);
    }

    public Reservation getNextRequest() {
        return requestQueue.poll(); // FIFO dequeue
    }

    public boolean isEmpty() {
        return requestQueue.isEmpty();
    }
}

// Inventory Service – maintains room availability
class InventoryService {
    private Map<String, Integer> roomInventory = new HashMap<>();

    public void addRoomType(String roomType, int count) {
        roomInventory.put(roomType, count);
    }

    public boolean isAvailable(String roomType) {
        return roomInventory.getOrDefault(roomType, 0) > 0;
    }

    public void decrement(String roomType) {
        roomInventory.put(roomType, roomInventory.get(roomType) - 1);
    }
}

// Booking Service – processes requests and allocates rooms
class BookingService {
    private InventoryService inventory;
    private Map<String, Set<String>> allocatedRooms = new HashMap<>();

    public BookingService(InventoryService inventory) {
        this.inventory = inventory;
    }

    public void confirmReservation(Reservation reservation) {
        String roomType = reservation.getRoomType();

        if (!inventory.isAvailable(roomType)) {
            System.out.println("No rooms available for " + roomType + ". Reservation failed for " + reservation.getGuestName());
            return;
        }

        // Generate unique room ID
        String roomId = UUID.randomUUID().toString();

        // Ensure uniqueness
        allocatedRooms.putIfAbsent(roomType, new HashSet<>());
        if (allocatedRooms.get(roomType).contains(roomId)) {
            System.out.println("Duplicate room ID detected. Allocation aborted.");
            return;
        }

        // Atomic allocation: assign + update inventory
        allocatedRooms.get(roomType).add(roomId);
        inventory.decrement(roomType);

        System.out.println("Reservation confirmed: " + reservation.getGuestName() +
                " -> Room Type: " + roomType + ", Room ID: " + roomId);
    }
}

public class BookRequest {
    public static void main(String[] args) {
        // Setup inventory
        InventoryService inventory = new InventoryService();
        inventory.addRoomType("Deluxe", 2);
        inventory.addRoomType("Suite", 1);
        inventory.addRoomType("Standard", 3);

        BookingRequestQueue queue = new BookingRequestQueue();
        BookingService bookingService = new BookingService(inventory);

        // Guests submit requests
        queue.addRequest(new Reservation("Alice", "Deluxe"));
        queue.addRequest(new Reservation("Bob", "Suite"));
        queue.addRequest(new Reservation("Charlie", "Deluxe"));
        queue.addRequest(new Reservation("Diana", "Suite")); // should fail (only 1 Suite)

        System.out.println("\nProcessing reservations:");
        while (!queue.isEmpty()) {
            Reservation next = queue.getNextRequest();
            bookingService.confirmReservation(next);
        }
    }
}

