import java.util.LinkedList;
import java.util.Queue;

// Represents a guest's intent to book a room
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

// Booking Request Queue that preserves arrival order
class BookingRequestQueue {
    private Queue<Reservation> requestQueue;

    public BookingRequestQueue() {
        requestQueue = new LinkedList<>();
    }

    // Accept booking request from guest
    public void addRequest(Reservation reservation) {
        requestQueue.add(reservation);
        System.out.println("Request added: " + reservation);
    }

    // Peek at the next request (without removing)
    public Reservation peekNextRequest() {
        return requestQueue.peek();
    }

    // Process next request (simulate allocation later)
    public Reservation processNextRequest() {
        return requestQueue.poll();
    }

    // Check if queue is empty
    public boolean isEmpty() {
        return requestQueue.isEmpty();
    }
}

public class BookRequest {
    public static void main(String[] args) {
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        // Guests submit booking requests
        bookingQueue.addRequest(new Reservation("Alice", "Deluxe"));
        bookingQueue.addRequest(new Reservation("Bob", "Suite"));
        bookingQueue.addRequest(new Reservation("Charlie", "Standard"));

        System.out.println("\nProcessing requests in FIFO order:");
        while (!bookingQueue.isEmpty()) {
            Reservation next = bookingQueue.processNextRequest();
            System.out.println("Processing: " + next);
        }
    }
}

