import java.util.*;

// Reservation class
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }

    @Override
    public String toString() {
        return "Reservation [Guest: " + guestName + ", Room Type: " + roomType + "]";
    }
}

// Inventory Service with synchronized methods
class InventoryService {
    private Map<String, Integer> roomInventory = new HashMap<>();

    public void addRoomType(String roomType, int count) {
        roomInventory.put(roomType, count);
    }

    // Thread-safe allocation
    public synchronized boolean allocateRoom(String roomType, String guestName) {
        int available = roomInventory.getOrDefault(roomType, 0);
        if (available > 0) {
            roomInventory.put(roomType, available - 1);
            System.out.println("Room allocated: " + roomType + " to " + guestName +
                    " | Remaining: " + roomInventory.get(roomType));
            return true;
        } else {
            System.out.println("No rooms available for " + roomType + " (Guest: " + guestName + ")");
            return false;
        }
    }

    public int getAvailable(String roomType) {
        return roomInventory.getOrDefault(roomType, 0);
    }
}

// Concurrent Booking Processor
class BookingProcessor implements Runnable {
    private Reservation reservation;
    private InventoryService inventory;

    public BookingProcessor(Reservation reservation, InventoryService inventory) {
        this.reservation = reservation;
        this.inventory = inventory;
    }

    @Override
    public void run() {
        inventory.allocateRoom(reservation.getRoomType(), reservation.getGuestName());
    }
}

public class BookRequest{
    public static void main(String[] args) {
        InventoryService inventory = new InventoryService();
        inventory.addRoomType("Deluxe", 2); // Only 2 Deluxe rooms available

        // Multiple guests trying to book at the same time
        List<Reservation> reservations = Arrays.asList(
                new Reservation("Alice", "Deluxe"),
                new Reservation("Bob", "Deluxe"),
                new Reservation("Charlie", "Deluxe"),
                new Reservation("Diana", "Deluxe")
        );

        // Create threads for concurrent booking
        List<Thread> threads = new ArrayList<>();
        for (Reservation res : reservations) {
            threads.add(new Thread(new BookingProcessor(res, inventory)));
        }

        // Start all threads simultaneously
        for (Thread t : threads) {
            t.start();
        }

        // Wait for all threads to finish
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\nFinal Inventory: Deluxe = " + inventory.getAvailable("Deluxe"));
    }
}
