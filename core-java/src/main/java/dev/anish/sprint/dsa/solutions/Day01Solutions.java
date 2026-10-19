package dev.anish.sprint.dsa.solutions;

import java.util.HashMap;
import java.util.Map;

/** Reference solutions. Open after you have attempted both. */
public class Day01Solutions {

    /**
     * One pass, O(n) time, O(n) space.
     *
     * The key move: check BEFORE inserting. That ordering is what prevents
     * an element pairing with itself, and it is the detail an interviewer
     * watches for.
     */
    public static int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> seen = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            Integer j = seen.get(target - nums[i]);
            if (j != null) {
                return new int[]{j, i};
            }
            seen.put(nums[i], i);
        }
        throw new IllegalArgumentException("no pair sums to " + target);
    }

    /**
     * Two passes, O(n) time. Space is O(1) in the strict sense -- the array is
     * fixed at 128 regardless of input length -- which is worth saying out loud.
     *
     * An int[128] beats a HashMap here for ASCII input: no boxing, no hashing,
     * better cache behaviour. Mention the trade-off (it does not handle Unicode)
     * and you sound like someone who has thought about it rather than recited it.
     */
    public static int firstUnique(String input) {
        int[] counts = new int[128];
        for (int i = 0; i < input.length(); i++) {
            counts[input.charAt(i)]++;
        }
        for (int i = 0; i < input.length(); i++) {
            if (counts[input.charAt(i)] == 1) {
                return i;
            }
        }
        return -1;
    }
}
