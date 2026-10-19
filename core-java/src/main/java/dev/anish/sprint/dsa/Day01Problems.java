package dev.anish.sprint.dsa;

import java.util.Arrays;

/**
 * DAY 1 WARM-UP -- 30 minutes, two problems. Pattern: hash map frequency counting.
 *
 * Run:
 *   mvn -pl core-java compile exec:java -Dexec.mainClass=dev.anish.sprint.dsa.Day01Problems
 *
 * Before you write anything, answer these out loud:
 *   - What is the brute-force approach and its complexity?
 *   - What is the repeated work the brute force does?
 *   - What data structure removes that repetition?
 *
 * That third question is the whole of this pattern. A hash map turns "search
 * the rest of the array" (O(n)) into "look it up" (O(1)), which collapses
 * O(n^2) to O(n). You already know this instinct: it is the difference
 * between a nested loop and a hash join.
 */
public class Day01Problems {

    // =================================================================
    // PROBLEM 1 -- Two Sum
    //
    // Given an array of integers and a target, return the INDICES of the two
    // numbers that add to the target. Exactly one solution exists. You may not
    // use the same element twice. Order of the returned indices does not matter.
    //
    //   twoSum([2,7,11,15], 9)  -> [0,1]
    //   twoSum([3,2,4], 6)      -> [1,2]
    //   twoSum([3,3], 6)        -> [0,1]
    //
    // Brute force: two nested loops, O(n^2) time, O(1) space.
    // Target:      one pass, O(n) time, O(n) space.
    //
    // HINT: as you walk the array, for each value ask "have I already seen
    //       (target - value)?" Store value -> index as you go. The answer is
    //       found BEFORE you have stored the current element, which is also
    //       what stops you using the same element twice.
    // =================================================================
    public static int[] twoSum(int[] nums, int target) {
        throw new UnsupportedOperationException("DSA 1 -- implement me");
    }

    // =================================================================
    // PROBLEM 2 -- First non-repeating character
    //
    // Return the index of the first character that appears exactly once.
    // Return -1 if there is none.
    //
    //   firstUnique("leetcode")  -> 0   ('l')
    //   firstUnique("loveleetcode") -> 2   ('v')
    //   firstUnique("aabb")      -> -1
    //
    // HINT: two passes. Count every character first, then walk the string
    //       again and return the first index whose count is 1.
    //
    // THINK: why two passes and not one? Because "first" is only knowable
    //        once you have seen the whole string. The same reason a median
    //        needs the full dataset.
    //
    // FOLLOW-UP they will ask: what if the input is a stream you cannot
    //        rewind? Then you need a LinkedHashMap to preserve order while
    //        counting -- which is exactly the LRU-cache trick in disguise.
    // =================================================================
    public static int firstUnique(String input) {
        throw new UnsupportedOperationException("DSA 2 -- implement me");
    }

    // -----------------------------------------------------------------
    // Self-check. No dependencies.
    // -----------------------------------------------------------------
    public static void main(String[] args) {
        int passed = 0;

        if (run("twoSum [2,7,11,15] t=9", () -> sorted(twoSum(new int[]{2, 7, 11, 15}, 9)), "[0, 1]")) passed++;
        if (run("twoSum [3,2,4] t=6",     () -> sorted(twoSum(new int[]{3, 2, 4}, 6)),      "[1, 2]")) passed++;
        if (run("twoSum [3,3] t=6",       () -> sorted(twoSum(new int[]{3, 3}, 6)),         "[0, 1]")) passed++;
        if (run("twoSum [-1,-2,-3] t=-5", () -> sorted(twoSum(new int[]{-1, -2, -3}, -5)),  "[1, 2]")) passed++;

        if (run("firstUnique leetcode",     () -> String.valueOf(firstUnique("leetcode")),     "0"))  passed++;
        if (run("firstUnique loveleetcode", () -> String.valueOf(firstUnique("loveleetcode")), "2"))  passed++;
        if (run("firstUnique aabb",         () -> String.valueOf(firstUnique("aabb")),         "-1")) passed++;
        if (run("firstUnique (empty)",      () -> String.valueOf(firstUnique("")),             "-1")) passed++;

        System.out.println();
        System.out.println(passed + " of 8 passing.");
        System.out.println();
        System.out.println("State the complexity of each solution out loud before you move on.");
        System.out.println("If you cannot, you have memorised an answer rather than learned a pattern.");
    }

    private static String sorted(int[] a) {
        int[] copy = a.clone();
        Arrays.sort(copy);
        return Arrays.toString(copy);
    }

    private static boolean run(String label, ThrowingSupplier body, String expected) {
        try {
            String actual = body.get();
            boolean ok = expected.equals(actual);
            System.out.printf("  %s  %-28s expected %-8s got %s%n",
                    ok ? "PASS" : "FAIL", label, expected, actual);
            return ok;
        } catch (UnsupportedOperationException ex) {
            System.out.printf("  TODO  %-28s (%s)%n", label, ex.getMessage());
            return false;
        } catch (Exception ex) {
            System.out.printf("  ERROR %-28s %s%n", label, ex);
            return false;
        }
    }

    @FunctionalInterface
    private interface ThrowingSupplier {
        String get() throws Exception;
    }
}
