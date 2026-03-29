import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

// --- Data Model for Historical Tracking ---
// Treats reservation data as a single entity to prepare for future database rows
class Reservation {
    private final String reservationId;
    private final String roomType;

    public Reservation(String reservationId, String roomType) {
        this.reservationId = reservationId;
        this.roomType = roomType;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getRoomType() {
        return roomType;
    }

    @Override
    public String toString() {
        return "Reservation ID: " + reservationId + " | Room Type: " + roomType;
    }
}

public class UseCase8BookMyStay {

    // 1. Queue for FIFO booking requests
    private Queue<String> requestQueue = new LinkedList<>();

    // 2. Inventory State
    private Map<String, Integer> inventoryMap = new HashMap<>();

    // 3. Uniqueness Enforcement Set
    private Map<String, Set<String>> allocatedRoomsMap = new HashMap<>();

    // 4. NEW: Booking History tracking using a List for chronological order
    private List<Reservation> bookingHistory = new ArrayList<>();

    /**
     * Initializes the system state for a specific room type.
     */
    public void setupInventory(String roomType, int count) {
        inventoryMap.put(roomType, count);
        allocatedRoomsMap.put(roomType, new HashSet<>());
    }

    /**
     * Adds a booking request to the queue.
     */
    public void addBookingRequest(String roomType) {
        requestQueue.add(roomType);
    }

    /**
     * Processes the next booking, confirms it, and saves it to history.
     */
    public void processNextBooking() {
        String roomType = requestQueue.poll();

        if (roomType == null) {
            System.out.println("No pending requests in the queue.");
            return;
        }

        int currentInventory = inventoryMap.getOrDefault(roomType, 0);

        if (currentInventory > 0) {
            // Step A: Atomic Allocation Logic
            String newRoomId = roomType.substring(0, 3).toUpperCase() + "-" + UUID.randomUUID().toString().substring(0, 5);

            allocatedRoomsMap.get(roomType).add(newRoomId);
            inventoryMap.put(roomType, currentInventory - 1);

            // Step B: Add confirmed reservation to historical tracking list
            Reservation confirmedReservation = new Reservation(newRoomId, roomType);
            bookingHistory.add(confirmedReservation);

            System.out.println("SUCCESS: Reservation confirmed for " + roomType + " (ID: " + newRoomId + ")");
        } else {
            System.err.println("FAILED: Reservation denied. " + roomType + " is completely sold out!");
        }
    }

    /**
     * Admin/Reporting Service: Retrieves and displays the chronological booking history.
     * Iterates over the List, guaranteeing insertion order is preserved.
     * Note: This strictly reads data without modifying the existing system state.
     */
    public void generateBookingHistoryReport() {
        System.out.println("\n=================================================");
        System.out.println("      ADMIN REPORT: BOOKING HISTORY AUDIT        ");
        System.out.println("=================================================");

        if (bookingHistory.isEmpty()) {
            System.out.println("No bookings have been confirmed yet.");
            System.out.println("=================================================\n");
            return;
        }

        // Ordered Storage Verification: Printing in insertion order
        for (int i = 0; i < bookingHistory.size(); i++) {
            System.out.println((i + 1) + ". " + bookingHistory.get(i).toString());
        }

        System.out.println("-------------------------------------------------");
        System.out.println("Total Confirmed Bookings: " + bookingHistory.size());
        System.out.println("=================================================\n");
    }

    public static void main(String[] args) {
        System.out.println("Book My Stay App - Use Case 8: Historical Tracking & Reporting\n");

        UseCase8BookMyStay service = new UseCase8BookMyStay();

        // Initialize Inventory
        service.setupInventory("Suite", 2);
        service.setupInventory("Standard", 1);

        // Queue requests
        service.addBookingRequest("Suite");    // 1st request
        service.addBookingRequest("Standard"); // 2nd request
        service.addBookingRequest("Suite");    // 3rd request
        service.addBookingRequest("Suite");    // 4th request (Should fail)

        // Process Queue sequentially
        service.processNextBooking(); // Success (Suite)
        service.processNextBooking(); // Success (Standard)
        service.processNextBooking(); // Success (Suite)
        service.processNextBooking(); // Fail (Suite sold out)

        // Generate Admin Report to view chronological history
        service.generateBookingHistoryReport();
    }
}