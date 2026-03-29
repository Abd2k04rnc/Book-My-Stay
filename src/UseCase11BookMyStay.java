import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

public class UseCase11BookMyStay {

    // --- Shared Mutable State ---
    // In a multi-threaded environment, these data structures are vulnerable to Race Conditions
    // if accessed simultaneously without synchronization.
    private final Queue<String> requestQueue = new LinkedList<>();
    private final Map<String, Integer> inventoryMap = new HashMap<>();
    private final Map<String, Set<String>> allocatedRoomsMap = new HashMap<>();
    private final List<Reservation> bookingHistory = new ArrayList<>();

    public void setupInventory(String roomType, int count) {
        inventoryMap.put(roomType, count);
        allocatedRoomsMap.put(roomType, new HashSet<>());
    }

    /**
     * Synchronized Access for Queueing
     * Ensures two threads don't try to write to the LinkedList at the exact same millisecond.
     */
    public synchronized void addBookingRequest(String roomType) {
        requestQueue.add(roomType);
    }

    /**
     * The Critical Section (Concurrent Booking Processor)
     * The 'synchronized' keyword ensures that only ONE thread can execute this method at a time.
     * This guarantees Thread Safety and prevents double allocation.
     */
    public synchronized void processNextBooking() {
        String threadName = Thread.currentThread().getName();
        String roomType = requestQueue.poll();

        if (roomType == null) {
            return; // Queue is empty
        }

        System.out.println("[" + threadName + "] Attempting to book: " + roomType);

        int currentInventory = inventoryMap.getOrDefault(roomType, 0);

        // If this block wasn't synchronized, multiple threads could see currentInventory > 0
        // at the same time and bypass this check, leading to negative inventory.
        if (currentInventory > 0) {

            // Simulate a tiny delay to naturally encourage race conditions if sync was missing
            try { Thread.sleep(10); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

            String newRoomId = roomType.substring(0, 3).toUpperCase() + "-" + UUID.randomUUID().toString().substring(0, 5);

            allocatedRoomsMap.get(roomType).add(newRoomId);
            inventoryMap.put(roomType, currentInventory - 1);
            bookingHistory.add(new Reservation(newRoomId, roomType));

            System.out.println("   -> SUCCESS: " + threadName + " secured " + roomType + " (ID: " + newRoomId + "). Remaining: " + (currentInventory - 1));
        } else {
            System.err.println("   -> FAILED: " + threadName + " denied. " + roomType + " is sold out!");
        }
    }

    public void generateSystemReport() {
        System.out.println("\n=================================================");
        System.out.println("      FINAL SYSTEM AUDIT (POST-CONCURRENCY)      ");
        System.out.println("=================================================");
        System.out.println("Final Inventory State: " + inventoryMap);
        System.out.println("Total Confirmed Bookings: " + bookingHistory.size());
        System.out.println("=================================================\n");
    }

    public static void main(String[] args) {
        System.out.println("Book My Stay App - Use Case 11: Concurrency & Thread Safety\n");

        UseCase11BookMyStay service = new UseCase11BookMyStay();

        // 1. Initialize Inventory (Only 3 Suites available)
        service.setupInventory("Suite", 3);

        // 2. Simulate 10 guests wanting a Suite at the same time
        int totalRequests = 10;
        for (int i = 0; i < totalRequests; i++) {
            service.addBookingRequest("Suite");
        }

        System.out.println("Starting Concurrent Booking Processor with 10 threads...\n");

        // 3. Create a Thread Pool to simulate simultaneous multi-user access
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // 4. Fire off 10 concurrent processing threads
        for (int i = 0; i < totalRequests; i++) {
            executor.submit(service::processNextBooking);
        }

        // 5. Gracefully shut down the executor and wait for all threads to finish
        executor.shutdown();
        try {
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.err.println("Execution interrupted!");
        }

        // 6. Verify System Integrity (Should exactly equal 3 bookings, 0 remaining)
        service.generateSystemReport();
    }
}
