import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

// --- Custom Exception ---
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// --- Data Model ---
class Reservation {
    private final String reservationId;
    private final String roomType;
    private String status; // Tracks state: ACTIVE or CANCELLED

    public Reservation(String reservationId, String roomType) {
        this.reservationId = reservationId;
        this.roomType = roomType;
        this.status = "ACTIVE";
    }

    public String getReservationId() { return reservationId; }
    public String getRoomType() { return roomType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Reservation ID: " + reservationId + " | Type: " + roomType + " | Status: " + status;
    }
}

public class UseCase10BookMyStay {

    private Queue<String> requestQueue = new LinkedList<>();
    private Map<String, Integer> inventoryMap = new HashMap<>();
    private Map<String, Set<String>> allocatedRoomsMap = new HashMap<>();
    private List<Reservation> bookingHistory = new ArrayList<>();

    // NEW: Stack Data Structure for LIFO Rollback Tracking
    // Records recently released room IDs.
    private Stack<String> cancelledRoomIds = new Stack<>();

    public void setupInventory(String roomType, int count) {
        inventoryMap.put(roomType, count);
        allocatedRoomsMap.put(roomType, new HashSet<>());
    }

    public void addBookingRequest(String roomType) {
        requestQueue.add(roomType);
    }

    public void processNextBooking() {
        String roomType = requestQueue.poll();
        if (roomType == null) return;

        try {
            if (!inventoryMap.containsKey(roomType)) {
                throw new InvalidBookingException("Invalid room type: " + roomType);
            }
            if (inventoryMap.get(roomType) <= 0) {
                throw new InvalidBookingException("Sold out: " + roomType);
            }

            int currentInventory = inventoryMap.get(roomType);
            String newRoomId = roomType.substring(0, 3).toUpperCase() + "-" + UUID.randomUUID().toString().substring(0, 5);

            allocatedRoomsMap.get(roomType).add(newRoomId);
            inventoryMap.put(roomType, currentInventory - 1);
            bookingHistory.add(new Reservation(newRoomId, roomType));

            System.out.println("SUCCESS: Booked " + roomType + " (ID: " + newRoomId + ")");
        } catch (InvalidBookingException e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }

    /**
     * Cancellation Service Engine
     * Safely reverses system state, validates requests, and restores inventory.
     */
    public void cancelBooking(String reservationId) {
        System.out.println("\n[Processing Cancellation] ID: " + reservationId);

        try {
            // 1. Validation: Ensure reservation exists and is cancellable
            Reservation targetReservation = null;
            for (Reservation res : bookingHistory) {
                if (res.getReservationId().equals(reservationId)) {
                    targetReservation = res;
                    break;
                }
            }

            if (targetReservation == null) {
                throw new InvalidBookingException("Cancellation Failed: Reservation ID not found.");
            }
            if (targetReservation.getStatus().equals("CANCELLED")) {
                throw new InvalidBookingException("Cancellation Failed: Reservation is already cancelled.");
            }

            String roomType = targetReservation.getRoomType();

            // 2. Controlled Mutation: Remove from allocation set
            allocatedRoomsMap.get(roomType).remove(reservationId);

            // 3. Rollback Structure: Push to Stack (LIFO tracking)
            cancelledRoomIds.push(reservationId);

            // 4. Inventory Restoration: Increment count immediately
            int currentInventory = inventoryMap.get(roomType);
            inventoryMap.put(roomType, currentInventory + 1);

            // 5. Update Booking History
            targetReservation.setStatus("CANCELLED");

            System.out.println("SUCCESS: Reservation " + reservationId + " cancelled safely.");
            System.out.println("         Inventory restored. Remaining " + roomType + "s: " + (currentInventory + 1));

        } catch (InvalidBookingException e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }

    public void generateSystemReport() {
        System.out.println("\n--- Final System Audit ---");
        System.out.println("Current Inventory: " + inventoryMap);
        System.out.println("Allocated Rooms: " + allocatedRoomsMap);
        System.out.println("Recently Cancelled IDs (Stack): " + cancelledRoomIds);
        System.out.println("\n--- Booking History ---");
        for (int i = 0; i < bookingHistory.size(); i++) {
            System.out.println((i + 1) + ". " + bookingHistory.get(i).toString());
        }
        System.out.println("--------------------------\n");
    }

    public static void main(String[] args) {
        System.out.println("Book My Stay App - Use Case 10: State Reversal & Cancellations\n");

        UseCase10BookMyStay service = new UseCase10BookMyStay();

        // 1. Initialize Inventory
        service.setupInventory("Suite", 1);
        service.setupInventory("Standard", 2);

        // 2. Queue and Process Bookings
        service.addBookingRequest("Suite");
        service.addBookingRequest("Standard");
        service.processNextBooking(); // Books Suite
        service.processNextBooking(); // Books Standard

        // Grab the generated IDs from the history to test cancellation
        String bookedSuiteId = service.bookingHistory.get(0).getReservationId();
        String bookedStandardId = service.bookingHistory.get(1).getReservationId();

        // 3. Test Valid Cancellation
        service.cancelBooking(bookedSuiteId);

        // 4. Test Invalid Cancellation (Already Cancelled)
        service.cancelBooking(bookedSuiteId);

        // 5. Test Invalid Cancellation (Fake ID)
        service.cancelBooking("FAKE-ID-12345");

        // 6. Test Inventory Restoration
        // Suite should be available again after cancellation!
        service.addBookingRequest("Suite");
        service.processNextBooking();

        // 7. Verify System Integrity
        service.generateSystemReport();
    }
}