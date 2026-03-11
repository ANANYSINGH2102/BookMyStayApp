import java.util.*;

// Reservation class (simplified for history tracking)
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

// Booking History – stores confirmed reservations
class BookingHistory {
    private List<Reservation> history = new ArrayList<>();

    // Add confirmed reservation to history
    public void addReservation(Reservation reservation) {
        history.add(reservation);
        System.out.println("Added to history: " + reservation);
    }

    // Retrieve all reservations
    public List<Reservation> getAllReservations() {
        return history;
    }
}

// Booking Report Service – generates summaries
class BookingReportService {
    private BookingHistory history;

    public BookingReportService(BookingHistory history) {
        this.history = history;
    }

    // Generate a simple report
    public void generateReport() {
        List<Reservation> reservations = history.getAllReservations();
        System.out.println("\n--- Booking Report ---");
        System.out.println("Total Reservations: " + reservations.size());

        Map<String, Integer> roomTypeCount = new HashMap<>();
        for (Reservation res : reservations) {
            roomTypeCount.put(res.getRoomType(), roomTypeCount.getOrDefault(res.getRoomType(), 0) + 1);
        }

        System.out.println("Breakdown by Room Type:");
        for (Map.Entry<String, Integer> entry : roomTypeCount.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        System.out.println("\nDetailed Reservations:");
        for (Reservation res : reservations) {
            System.out.println(res);
        }
    }
}

public class BookRequest {
    public static void main(String[] args) {
        BookingHistory history = new BookingHistory();
        BookingReportService reportService = new BookingReportService(history);

        // Simulate confirmed reservations
        Reservation res1 = new Reservation("Alice", "Deluxe");
        Reservation res2 = new Reservation("Bob", "Suite");
        Reservation res3 = new Reservation("Charlie", "Deluxe");

        history.addReservation(res1);
        history.addReservation(res2);
        history.addReservation(res3);

        // Admin requests report
        reportService.generateReport();
    }
}
