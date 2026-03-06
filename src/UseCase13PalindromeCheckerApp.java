import java.util.Stack;

public class UseCase13PalindromeCheckerApp {
    public static void main(String[] args) {
        // UC1: System Initialization
        System.out.println("Welcome to the Palindrome Checker Management System\n");
        System.out.println("Version : 1.0");
        System.out.println("System initialized successfully\n");

        /* UC13: Performance Comparison using System.nanoTime() */

        // We use a long string to make the time differences noticeable
        String baseString = "A man a plan a canal Panama".replaceAll("\\s+", "").toLowerCase();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append(baseString);
        }
        String testString = sb.toString(); // A long palindrome

        System.out.println("Running Performance Comparison on a large string (Length: " + testString.length() + ")...\n");

        // --- 1. Built-in StringBuilder Reverse ---
        long startTime1 = System.nanoTime();
        boolean res1 = new StringBuilder(testString).reverse().toString().equals(testString);
        long endTime1 = System.nanoTime();
        long duration1 = endTime1 - startTime1;

        // --- 2. Two-Pointer Technique (UC4) ---
        long startTime2 = System.nanoTime();
        boolean res2 = twoPointerCheck(testString);
        long endTime2 = System.nanoTime();
        long duration2 = endTime2 - startTime2;

        // --- 3. Stack-Based Technique (UC5) ---
        long startTime3 = System.nanoTime();
        boolean res3 = stackCheck(testString);
        long endTime3 = System.nanoTime();
        long duration3 = endTime3 - startTime3;

        // --- Display Results ---
        System.out.println("--- Performance Results (in nanoseconds) ---");
        System.out.println("1. Built-in Reverse   : " + duration1 + " ns");
        System.out.println("2. Two-Pointer Array  : " + duration2 + " ns");
        System.out.println("3. Stack Structure    : " + duration3 + " ns");

        System.out.println("\nConclusion: The Two-Pointer approach is usually the fastest custom algorithm because it avoids creating heavy objects like Stacks!");
    }

    // Helper method for Two-Pointer logic
    private static boolean twoPointerCheck(String str) {
        char[] arr = str.toCharArray();
        int left = 0, right = arr.length - 1;
        while (left < right) {
            if (arr[left] != arr[right]) return false;
            left++;
            right--;
        }
        return true;
    }

    // Helper method for Stack logic
    private static boolean stackCheck(String str) {
        Stack<Character> stack = new Stack<>();
        for (char c : str.toCharArray()) stack.push(c);

        int i = 0;
        while (!stack.isEmpty()) {
            if (stack.pop() != str.charAt(i)) return false;
            i++;
        }
        return true;
    }
}