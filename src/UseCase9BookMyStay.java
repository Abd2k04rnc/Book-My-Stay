import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

// --- 1. Custom Exception Class ---
// Domain-specific exception to represent invalid booking scenarios explicitly
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// --- Data Model ---
class Reservation {
    private final String reservationId;
    private final String roomType;

    public Reservation(String reservationId, String roomType) {
        this.reservationId = reservationId;
        this.roomType = roomType;
    }

    @Override
    public String toString() {
        return "Reservation ID: " + reservationId + " | Room Type: " + roomType;
    }
}

public class UseCase9BookMyStay {

    private Queue<String> requestQueue = new LinkedList<>();
    private Map<String, Integer> inventoryMap = new HashMap<>();
    private Map<String, Set<String>> allocatedRoomsMap = new HashMap<>();
    private List<Reservation> bookingHistory = new ArrayList<>();

    public void setupInventory(String roomType, int count) {
        inventoryMap.put(roomType, count);
        allocatedRoomsMap.put(roomType, new HashSet<>());
    }

    public void addBookingRequest(String roomType) {
        requestQueue.add(roomType);
    }

    /**
     * Validation Engine (Fail-Fast Design)
     * Checks all constraints BEFORE any state is modified.
     */
    private void validateRequest(String roomType) throws InvalidBookingException {
        // Check 1: Null or empty input
        if (roomType == null || roomType.trim().isEmpty()) {
            throw new InvalidBookingException("Input Validation Failed: Room type cannot be empty.");
        }

        // Check 2: Invalid/Unknown room type
        if (!inventoryMap.containsKey(roomType)) {
            throw new InvalidBookingException("System Constraint Failed: '" + roomType + "' is not a recognized room type.");
        }

        // Check 3: Inventory limits (Guarding System State)
        if (inventoryMap.get(roomType) <= 0) {
            throw new InvalidBookingException("Availability Error: '" + roomType + "' is completely sold out.");
        }
    }

    /**
     * Processes the next booking with Graceful Failure Handling.
     */
    public void processNextBooking() {
        String roomType = requestQueue.poll();

        if (roomType == null) {
            System.out.println("No pending requests in the queue.");
            return;
        }

        System.out.println("\n[Processing Request] Type: " + (roomType.isEmpty() ? "EMPTY_INPUT" : roomType));

        try {
            // Step 1: Validate input and constraints (Throws exception if invalid)
            validateRequest(roomType);

            // Step 2: If we reach here, validation passed. Proceed with atomic allocation.
            int currentInventory = inventoryMap.get(roomType);
            String newRoomId = roomType.substring(0, 3).toUpperCase() + "-" + UUID.randomUUID().toString().substring(0, 5);

            allocatedRoomsMap.get(roomType).add(newRoomId);
            inventoryMap.put(roomType, currentInventory - 1);
            bookingHistory.add(new Reservation(newRoomId, roomType));

            System.out.println("SUCCESS: Reservation confirmed (ID: " + newRoomId + ")");
            System.out.println("         Remaining " + roomType + " inventory: " + (currentInventory - 1));

        } catch (InvalidBookingException e) {
            // Step 3: Graceful Failure Handling - System doesn't crash, just reports the error.
            System.err.println("ERROR: " + e.getMessage());
        }
    }

    public void generateBookingHistoryReport() {
        System.out.println("\n--- Final Booking History Audit ---");
        for (int i = 0; i < bookingHistory.size(); i++) {
            System.out.println((i + 1) + ". " + bookingHistory.get(i).toString());
        }
        System.out.println("Total Confirmed Bookings: " + bookingHistory.size());
        System.out.println("-----------------------------------\n");
    }

    public static void main(String[] args) {
        System.out.println("Book My Stay App - Use Case 9: Validation & Error Handling");

        UseCase9BookMyStay service = new UseCase9BookMyStay();

        // Initialize Inventory
        service.setupInventory("Suite", 1);
        service.setupInventory("Standard", 5);

        // Queue requests representing "Correctness over Happy Path"
        service.addBookingRequest("Suite");      // Valid: Should succeed
        service.addBookingRequest("Suite");      // Invalid State: Should fail (Sold out)
        service.addBookingRequest("Penthouse");  // Invalid Input: Should fail (Doesn't exist)
        service.addBookingRequest("");           // Invalid Input: Should fail (Empty string)
        service.addBookingRequest("Standard");   // Valid: Should succeed (System remains stable after errors)

        // Process Queue sequentially
        service.processNextBooking();
        service.processNextBooking();
        service.processNextBooking();
        service.processNextBooking();
        service.processNextBooking();

        // Generate Admin Report to prove only valid bookings made it through
        service.generateBookingHistoryReport();
    }
}