public class UseCase10PalindromeCheckerApp {
    public static void main(String[] args) {
        // UC1: System Initialization
        System.out.println("Welcome to the Palindrome Checker Management System\n");
        System.out.println("Version : 1.0");
        System.out.println("System initialized successfully\n");

        /* UC10: Case-Insensitive & Space-Ignored Palindrome */
        String rawInput = "Race car";
        System.out.println("Original string: '" + rawInput + "'");

        // 1. Normalize string: Remove spaces using Regex and convert to lowercase
        // "\\s+" is a regular expression that targets all whitespace characters
        String normalizedInput = rawInput.replaceAll("\\s+", "").toLowerCase();
        System.out.println("Normalized string: '" + normalizedInput + "'");

        // 2. Apply previous logic (Two-Pointer Technique from UC4)
        char[] charArray = normalizedInput.toCharArray();
        int left = 0;
        int right = charArray.length - 1;
        boolean isPalindrome = true;

        while (left < right) {
            if (charArray[left] != charArray[right]) {
                isPalindrome = false;
                break;
            }
            left++;
            right--;
        }

        // 3. Output Result
        if (isPalindrome) {
            System.out.println("Result: It is a palindrome.");
        } else {
            System.out.println("Result: It is not a palindrome.");
        }
    }
}