public class UseCase5BookMyStay
import java.util.HashMap;
import java.util.Map;

/**
 * Use Case 5: Atomic Booking Transactions
 * Goal: Ensure data consistency during state changes.
 * Version: 5.0
 */

// --- Room Model ---
class HotelRoom {
    private String type;
    private double price;

    public HotelRoom(String type, double price) {
        this.type = type;
        this.price = price;
    }
    public String getType() { return type; }
}

// --- The Booking Engine (Transaction Layer) ---
class BookingService {
    private Map<String, Integer> inventory;

    public BookingService(Map<String, Integer> inventory) {
        this.inventory = inventory;
    }

    /**
     * The "Atomic" Booking Method
     * Returns true if booking succeeded, false if sold out.
     */
    public boolean bookRoom(String type) {
        System.out.println("\n[Transaction] Attempting to book: " + type);

        // 1. Check & Validate
        if (!inventory.containsKey(type) || inventory.get(type) <= 0) {
            System.err.println("FAILED: " + type + " is currently sold out!");
            return false;
        }

        // 2. Perform Update (Atomic-style subtraction)
        int currentStock = inventory.get(type);
        inventory.put(type, currentStock - 1);

        System.out.println("SUCCESS: Room reserved. Remaining " + type + "s: " + (currentStock - 1));
        return true;
    }
}

public class UseCase5BookMyStay {
    public static void main(String[] args) {
        System.out.println("Book My Stay App [v5.0 - Booking Engine]");
        System.out.println("----------------------------------------------");

        // Initial State: Only 1 Suite available
        Map<String, Integer> hotelInventory = new HashMap<>();git add .
        hotelInventory.put("Single", 5);
        hotelInventory.put("Suite", 1);

        BookingService engine = new BookingService(hotelInventory);

        // Scenario A: Successful Booking
        engine.bookRoom("Suite");

        // Scenario B: Failed Booking (Attempting to book the same Suite again)
        engine.bookRoom("Suite");

        // Scenario C: Successful Single Room booking
        engine.bookRoom("Single");

        System.out.println("\nFinal Inventory Audit: " + hotelInventory);
        System.out.println("----------------------------------------------");
    }
}
