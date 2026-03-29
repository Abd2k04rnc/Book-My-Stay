import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

public class UseCase6BookMyStay {

    // 1. Queue to retrieve booking requests in FIFO order
    private Queue<String> requestQueue = new LinkedList<>();

    // 2. Inventory Service State: Tracks remaining availability
    private Map<String, Integer> inventoryMap = new HashMap<>();

    // 3. Mapping Room Types to Assigned Rooms: Enforces uniqueness
    private Map<String, Set<String>> allocatedRoomsMap = new HashMap<>();

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
     * Processes the next booking in the queue.
     * This method acts as a single atomic logical operation to prevent double-booking.
     */
    public void processNextBooking() {
        // Step 1: Booking request is dequeued from the request queue.
        String roomType = requestQueue.poll();

        if (roomType == null) {
            System.out.println("No pending requests in the queue.");
            return;
        }

        System.out.println("\n[Processing Request] Type: " + roomType);

        // Step 2: The system checks availability for the requested room type.
        int currentInventory = inventoryMap.getOrDefault(roomType, 0);

        if (currentInventory > 0) {
            // Step 3: A unique room ID is generated and assigned.
            String newRoomId = roomType.substring(0, 3).toUpperCase() + "-" + UUID.randomUUID().toString().substring(0, 5);

            // Step 4: The room ID is recorded to prevent reuse using the Set structure.
            Set<String> allocatedRooms = allocatedRoomsMap.get(roomType);
            allocatedRooms.add(newRoomId);

            // Step 5: Inventory count is decremented immediately.
            inventoryMap.put(roomType, currentInventory - 1);

            // Step 6: Reservation is confirmed.
            System.out.println("SUCCESS: Reservation confirmed.");
            System.out.println("         Assigned Room ID: " + newRoomId);
            System.out.println("         Remaining " + roomType + " inventory: " + (currentInventory - 1));
        } else {
            // Prevented double-booking / overbooking
            System.err.println("FAILED: Reservation denied. " + roomType + " is completely sold out!");
        }
    }

    /**
     * Helper method to audit the final state of the HashMaps.
     */
    public void printSystemState() {
        System.out.println("\n--- Final System Audit ---");
        System.out.println("Inventory State: " + inventoryMap);
        System.out.println("Allocated Rooms Mapping: " + allocatedRoomsMap);
        System.out.println("--------------------------\n");
    }

    public static void main(String[] args) {
        System.out.println("Book My Stay App - Use Case 6: Room Allocation & Uniqueness");

        UseCase6BookMyStay service = new UseCase6BookMyStay();

        // Initialize Inventory
        service.setupInventory("Suite", 1);
        service.setupInventory("Standard", 2);

        // Queue requests (FIFO testing)
        // 2 Suite requests (only 1 available) and 2 Standard requests
        service.addBookingRequest("Suite");
        service.addBookingRequest("Suite");
        service.addBookingRequest("Standard");
        service.addBookingRequest("Standard");

        // Process Queue
        service.processNextBooking(); // Expected: SUCCESS (Suite 1)
        service.processNextBooking(); // Expected: FAILED (Suite sold out)
        service.processNextBooking(); // Expected: SUCCESS (Standard 1)
        service.processNextBooking(); // Expected: SUCCESS (Standard 2)

        // Verify the Sets and Maps
        service.printSystemState();
    }
}