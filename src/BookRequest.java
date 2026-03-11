import java.util.*;

// Reservation class
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;
    private String roomId;
    private boolean isCancelled;

    public Reservation(String guestName, String roomType, String roomId) {
        this.reservationId = UUID.randomUUID().toString();
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomId = roomId;
        this.isCancelled = false;
    }

    public String getReservationId() { return reservationId; }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public String getRoomId() { return roomId; }
    public boolean isCancelled() { return isCancelled; }
    public void cancel() { this.isCancelled = true; }

    @Override
    public String toString() {
        return "Reservation [ID: " + reservationId + ", Guest: " + guestName +
                ", Room Type: " + roomType + ", Room ID: " + roomId +
                ", Cancelled: " + isCancelled + "]";
    }
}

// Inventory Service
class InventoryService {
    private Map<String, Integer> roomInventory = new HashMap<>();

    public void addRoomType(String roomType, int count) {
        roomInventory.put(roomType, count);
    }

    public void decrement(String roomType) {
        roomInventory.put(roomType, roomInventory.get(roomType) - 1);
    }

    public void increment(String roomType) {
        roomInventory.put(roomType, roomInventory.get(roomType) + 1);
    }

    public int getAvailable(String roomType) {
        return roomInventory.getOrDefault(roomType, 0);
    }
}

// Booking History
class BookingHistory {
    private List<Reservation> history = new ArrayList<>();

    public void addReservation(Reservation reservation) {
        history.add(reservation);
    }

    public List<Reservation> getAllReservations() {
        return history;
    }

    public Reservation findById(String reservationId) {
        for (Reservation res : history) {
            if (res.getReservationId().equals(reservationId)) {
                return res;
            }
        }
        return null;
    }
}

// Cancellation Service
class CancellationService {
    private InventoryService inventory;
    private BookingHistory history;
    private Stack<String> rollbackStack = new Stack<>();

    public CancellationService(InventoryService inventory, BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
    }

    public void cancelReservation(String reservationId) {
        Reservation res = history.findById(reservationId);

        if (res == null) {
            System.out.println("Cancellation failed: Reservation not found.");
            return;
        }
        if (res.isCancelled()) {
            System.out.println("Cancellation failed: Reservation already cancelled.");
            return;
        }

        // Record rollback
        rollbackStack.push(res.getRoomId());

        // Restore inventory
        inventory.increment(res.getRoomType());

        // Update reservation state
        res.cancel();

        System.out.println("Cancellation successful: " + res.getGuestName() +
                " -> Room Type: " + res.getRoomType() +
                " restored. Inventory now: " + inventory.getAvailable(res.getRoomType()));
    }
}

public class BookRequest{
    public static void main(String[] args) {
        InventoryService inventory = new InventoryService();
        inventory.addRoomType("Deluxe", 1);
        inventory.addRoomType("Suite", 1);

        BookingHistory history = new BookingHistory();

        // Simulate confirmed reservations
        Reservation res1 = new Reservation("Alice", "Deluxe", "D101");
        Reservation res2 = new Reservation("Bob", "Suite", "S201");

        history.addReservation(res1);
        history.addReservation(res2);

        // Inventory updated after booking
        inventory.decrement("Deluxe");
        inventory.decrement("Suite");

        CancellationService cancellationService = new CancellationService(inventory, history);

        // Attempt cancellations
        cancellationService.cancelReservation(res1.getReservationId()); // Success
        cancellationService.cancelReservation(res1.getReservationId()); // Already cancelled
        cancellationService.cancelReservation("fake-id");               // Invalid ID
    }
}
