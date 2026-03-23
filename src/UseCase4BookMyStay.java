import java.util.HashMap;
import java.util.Map;

/**
 * Use Case 4: Safe Room Search & Read-Only Access
 * Goal: Filter and display available rooms without modifying state.
 * Version: 4.0
 */

// --- Reusing the Domain Model ---
abstract class SearchableRoom {
    private String type;
    private double price;

    public SearchableRoom(String type, double price) {
        this.type = type;
        this.price = price;
    }

    public String getType() { return type; }
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return "Type: " + type + " | Price: $" + price;
    }
}

class Suite extends SearchableRoom { public Suite() { super("Suite", 350.0); } }

// --- Search Service (The Logic Layer) ---
class SearchService {
    public void searchAvailableRooms(Map<String, Integer> inventory, Map<String, SearchableRoom> roomDetails) {
        System.out.println("\n--- Searching for Available Rooms ---");
        boolean found = false;

        for (String type : inventory.keySet()) {
            int count = inventory.get(type);

            // Validation Logic: Only show rooms with count > 0
            if (count > 0) {
                SearchableRoom details = roomDetails.get(type);
                System.out.println("[AVAILABLE] " + details + " | In Stock: " + count);
                found = true;
            }
        }

        if (!found) {
            System.out.println("Sorry, no rooms are currently available.");
        }
    }
}

// --- Main Application Class ---
public class UseCase4BookMyStay {
    public static void main(String[] args) {
        System.out.println("Book My Stay App [v4.0 - Search Mode]");
        System.out.println("----------------------------------------------");

        // 1. Setup Inventory (State)
        Map<String, Integer> inventory = new HashMap<>();
        inventory.put("Single", 10);
        inventory.put("Double", 0); // This one should be filtered out!
        inventory.put("Suite", 2);

        // 2. Setup Room Details (Domain Data)
        Map<String, SearchableRoom> roomCatalog = new HashMap<>();
        roomCatalog.put("Single", new Single());
        roomCatalog.put("Double", new DoubleR());
        roomCatalog.put("Suite", new Suite());

        // 3. Initiate Search (Read-Only)
        SearchService searchService = new SearchService();
        searchService.searchAvailableRooms(inventory, roomCatalog);

        System.out.println("\n----------------------------------------------");
        System.out.println("Search completed. Note: Inventory counts remain unchanged.");
    }
}