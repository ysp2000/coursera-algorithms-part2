import java.util.HashSet;
import java.util.Set;

/**
 * The Pair class is designed for storing two elements of type T.
 * <p>
 * This implementation uses java.lang.Set<T> to store elements.
 *
 * @author Sergey Esipenko
 * @param <T>
 *            type of elements
 */
public class Pair<T> {

    /**
     * Set for storing elements of the pair.
     */
    private Set<T> set;

    /**
     * Constructor takes to elements of type T.
     *
     * @param first
     *            first element
     * @param second
     *            second element
     */
    public Pair(final T first, final T second) {
        this.set = new HashSet<T>();
        set.add(first);
        set.add(second);
    }

    @Override
    public final int hashCode() {
        return set.hashCode();
    }

    @Override
    public final boolean equals(final Object obj) {
        if (!(obj instanceof Pair)) {
            return false;
        }
        @SuppressWarnings("rawtypes")
        Pair p = (Pair) obj;
        return set.equals(p.set);
    }

    @Override
    public final String toString() {
        return set.toString();
    }
}
