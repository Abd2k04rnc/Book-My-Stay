import java.util.Stack;

// 1. Encapsulated Service Class (Single Responsibility Principle)
class PalindromeChecker {

    // Expose a clean, public method to the outside world
    public boolean checkPalindrome(String input) {
        if (input == null) return false;

        // Normalize the string (from UC10)
        String normalizedInput = input.replaceAll("\\s+", "").toLowerCase();

        // Internal Data Structure (Stack) hidden from the main app
        Stack<Character> stack = new Stack<>();
        for (char c : normalizedInput.toCharArray()) {
            stack.push(c);
        }

        String reversed = "";
        while (!stack.isEmpty()) {
            reversed += stack.pop();
        }

        return normalizedInput.equals(reversed);
    }
}

// 2. Main Application Class
public class UseCase11PalindromeCheckerApp {
    public static void main(String[] args) {
        // UC1: System Initialization
        System.out.println("Welcome to the Palindrome Checker Management System\n");
        System.out.println("Version : 1.0");
        System.out.println("System initialized successfully\n");

        /* UC11: Object-Oriented Palindrome Service */
        String testString = "Race car";
        System.out.println("Checking string: '" + testString + "'");

        // Instantiate the object
        PalindromeChecker checker = new PalindromeChecker();

        // Call the exposed method
        boolean isPalindrome = checker.checkPalindrome(testString);

        // Output Result
        if (isPalindrome) {
            System.out.println("Result: It is a palindrome.");
        } else {
            System.out.println("Result: It is not a palindrome.");
        }
    }
}