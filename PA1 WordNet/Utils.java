import java.util.HashSet;
import java.util.Set;

/**
 * Utility class.
 *
 * @author Sergey Esipenko
 */
public final class Utils {

    /**
     * Private default constructor. Prevents from instancing.
     */
    private Utils() {
    }

    /**
     * Checks statement and if it is false throws runtimeException.
     *
     * @param statement
     *            statement to check
     * @param runtimeException
     *            exception that will be thrown if statement is false
     */
    public static void check(final boolean statement,
            final RuntimeException runtimeException) {
        if (!statement) {
            throw runtimeException;
        }
    }

    /**
     * Converts iterable to set.
     *
     * @param iterable
     *            iterable object
     * @param <T>
     *            type of elements
     * @return set of elements from iterable
     */
    public static <T> Set<T> iterableAsSet(final Iterable<T> iterable) {
        Set<T> set = new HashSet<T>();
        for (T element : iterable) {
            set.add(element);
        }
        return set;
    }

}
