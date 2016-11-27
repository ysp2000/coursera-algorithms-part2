/**
 * The IntegerPair class is designed for storing two integer values (in
 * non-decreasing order).
 *
 * @author Sergey Esipenko
 */
public class IntegerPair implements Comparable<IntegerPair> {

    /**
     * First (lower) element of pair.
     */
    private final int first;

    /**
     * Second (higher) element of pair.
     */
    private final int second;

    /**
     * Constructs correct IntegerPair from given arguments.
     *
     * @param a
     *            first element
     * @param b
     *            second element
     */
    public IntegerPair(final int a, final int b) {
        this.first = Math.min(a, b);
        this.second = Math.max(a, b);
    }

    @Override
    public final int compareTo(final IntegerPair p) {
        if (first != p.first) {
            return Integer.valueOf(first).compareTo(Integer.valueOf(p.first));
        }
        if (second != p.second) {
            return Integer.valueOf(second).compareTo(Integer.valueOf(p.second));
        }
        return 0;
    }
}
