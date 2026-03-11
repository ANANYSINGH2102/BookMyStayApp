import java.io.*;
import java.util.*;

// Reservation class (Serializable for persistence)
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;
    private String reservationId;
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.reservationId = UUID.randomUUID().toString();
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getReservationId() { return reservationId; }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }

    @Override
    public String toString() {
        return "Reservation [ID: " + reservationId + ", Guest: " + guestName + ", Room Type: " + roomType + "]";
    }
}

// Inventory Service (Serializable)
class InventoryService implements Serializable {
    private static final long serialVersionUID = 1L;
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

    public void increment(String roomType) {
        roomInventory.put(roomType, roomInventory.get(roomType) + 1);
    }

    public Map<String, Integer> getInventorySnapshot() {
        return roomInventory;
    }

    @Override
    public String toString() {
        return "Inventory: " + roomInventory;
    }
}

// Booking History (Serializable)
class BookingHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Reservation> history = new ArrayList<>();

    public void addReservation(Reservation reservation) {
        history.add(reservation);
    }

    public List<Reservation> getAllReservations() {
        return history;
    }

    @Override
    public String toString() {
        return "Booking History: " + history;
    }
}

// Persistence Service
class PersistenceService {
    private static final String FILE_NAME = "system_state.ser";

    public static void saveState(InventoryService inventory, BookingHistory history) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(inventory);
            oos.writeObject(history);
            System.out.println("System state saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving state: " + e.getMessage());
        }
    }

    public static Object[] loadState() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            InventoryService inventory = (InventoryService) ois.readObject();
            BookingHistory history = (BookingHistory) ois.readObject();
            System.out.println("System state loaded successfully.");
            return new Object[]{inventory, history};
        } catch (FileNotFoundException e) {
            System.out.println("No saved state found. Starting fresh.");
            return new Object[]{new InventoryService(), new BookingHistory()};
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading state: " + e.getMessage());
            return new Object[]{new InventoryService(), new BookingHistory()};
        }
    }
}

public class BookRequest {
    public static void main(String[] args) {
        // Load previous state if available
        Object[] state = PersistenceService.loadState();
        InventoryService inventory = (InventoryService) state[0];
        BookingHistory history = (BookingHistory) state[1];

        // Initialize inventory if fresh start
        if (inventory.getInventorySnapshot().isEmpty()) {
            inventory.addRoomType("Deluxe", 2);
            inventory.addRoomType("Suite", 1);
        }

        // Simulate new reservations
        Reservation res1 = new Reservation("Alice", "Deluxe");
        if (inventory.isAvailable(res1.getRoomType())) {
            inventory.decrement(res1.getRoomType());
            history.addReservation(res1);
        }

        Reservation res2 = new Reservation("Bob", "Suite");
        if (inventory.isAvailable(res2.getRoomType())) {
            inventory.decrement(res2.getRoomType());
            history.addReservation(res2);
        }

        // Display current state
        System.out.println(inventory);
        System.out.println(history);

        // Save state before shutdown
        PersistenceService.saveState(inventory, history);
    }
}
