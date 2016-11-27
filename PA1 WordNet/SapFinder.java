/**
 * Interface for SAP finding data type.
 *
 * @author Sergey Esipenko
 */
public interface SapFinder {
    /**
     * Performs a SAP query for two vertices.
     *
     * @param first
     *            first vertex
     * @param second
     *            second vertex
     * @return answer for the query as stored in the SapAnswer object
     */
    SapAnswer sap(final int first, final int second);

    /**
     * Performs a SAP query for two set of vertices.
     *
     * @param first
     *            first set of vertices
     * @param second
     *            second set of vertices
     * @return answer for the query stored in the SapAnswer object
     */
    SapAnswer sap(final Iterable<Integer> first,
            final Iterable<Integer> second);
}
