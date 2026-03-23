import java.util.HashMap;
import java.util.Map;

/**
 * Use Case 3: Centralized Inventory Management
 * System: Book My Stay App
 * Concept: HashMap for "Single Source of Truth"
 */

// --- Unique Domain Models for Use Case 3 ---
abstract class InventoryRoom {
    private String type;
    private double price;

    public InventoryRoom(String type, double price) {
        this.type = type;
        this.price = price;
    }

    public String getType() { return type; }
    public void display() {
        System.out.print("Room Type: " + type + " | Price: $" + price);
    }
}

class InventorySingle extends InventoryRoom { public InventorySingle() { super("Single", 100.0); } }
class InventoryDouble extends InventoryRoom { public InventoryDouble() { super("Double", 180.0); } }
class InventorySuite  extends InventoryRoom { public InventorySuite()  { super("Suite", 350.0); } }

// --- Centralized Inventory Manager ---
class RoomInventory {
    private Map<String, Integer> stock;

    public RoomInventory() {
        this.stock = new HashMap<>();
    }

    public void initializeRoom(String type, int count) {
        stock.put(type, count);
    }

    public int getAvailable(String type) {
        return stock.getOrDefault(type, 0);
    }

    public void updateStock(String type, int change) {
        if (stock.containsKey(type)) {
            stock.put(type, stock.get(type) + change);
        }
    }

    public void displayFullInventory() {
        System.out.println("\n--- Live Inventory Status ---");
        for (Map.Entry<String, Integer> entry : stock.entrySet()) {
            System.out.println(entry.getKey() + " Rooms -> Available: " + entry.getValue());
        }
    }
}

// --- Main Application Class ---
public class UseCase3BookMyStay {
    public static void main(String[] args) {
        System.out.println("Book My Stay App [v3.0 - Inventory Management]");
        System.out.println("----------------------------------------------");

        // 1. Initialize the Centralized Inventory
        RoomInventory manager = new RoomInventory();
        manager.initializeRoom("Single", 10);
        manager.initializeRoom("Double", 5);
        manager.initializeRoom("Suite", 2);

        // 2. Create Room objects for display
        InventoryRoom s = new InventorySingle();
        InventoryRoom d = new InventoryDouble();
        InventoryRoom st = new InventorySuite();

        // 3. Display individual room data synced with the HashMap
        s.display();
        System.out.println(" | Current Stock: " + manager.getAvailable("Single"));

        d.display();
        System.out.println(" | Current Stock: " + manager.getAvailable("Double"));

        st.display();
        System.out.println(" | Current Stock: " + manager.getAvailable("Suite"));

        // 4. Demonstrate a stock update (e.g., a booking occurs)
        System.out.println("\n[Action] Processing booking for 1 Suite...");
        manager.updateStock("Suite", -1);

        // 5. Show full inventory state
        manager.displayFullInventory();

        System.out.println("\n----------------------------------------------");
        System.out.println("Inventory logic verified and complete.");
    }
}