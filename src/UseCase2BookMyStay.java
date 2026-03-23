/**
 * Use Case 2: Room Abstraction & Inheritance
 * Goal: Model the domain using OOP principles.
 */
abstract class BaseRoom {
    private String type;
    private double price;

    public BaseRoom(String type, double price) {
        this.type = type;
        this.price = price;
    }

    public void showDetails() {
        System.out.println("Room: " + type + " | Price per Night: $" + price);
    }
}

class Single extends BaseRoom { public Single() { super("Single", 100.0); } }
class DoubleR extends BaseRoom { public DoubleR() { super("Double", 180.0); } }

public class UseCase2HotelBookingApp {
    public static void main(String[] args) {
        System.out.println("Use Case 2: Domain Modeling");

        BaseRoom r1 = new Single();
        BaseRoom r2 = new DoubleR();

        r1.showDetails();
        r2.showDetails();

        System.out.println("Availability (Static): 10 Rooms remaining.");
    }
}
