/**
 * The SAP class is designed to process shortest ancestral paths (SAP) queries
 * like length(a, b) to find length of SAP and ancestor(a, b) to find a common
 * ancestor which is laying on SAP.
 *
 * @author Sergey Esipenko
 */
public class SAP {

    /**
     * Digraph where all queries are performed.
     */
    private final Digraph digraph;

    /**
     * Helper object for performing queries.
     */
    private final SapFinder sapBfs;

    /**
     * Constructor takes a digraph (not necessarily a DAG).
     *
     * @param directedGraph
     *            graph where all queries are performed
     */
    public SAP(final Digraph directedGraph) {
        this.digraph = new Digraph(directedGraph);
        this.sapBfs = new TwoWaySapFinder(this.digraph);
    }

    /**
     * Calculates the length of shortest ancestral path between v and w; -1 if
     * no such path.
     *
     * @param v
     *            first vertex
     * @param w
     *            second vertex
     * @return the length of shortest ancestral path between v and w; -1 if no
     *         such path.
     */
    public int length(final int v, final int w) {
        SapAnswer sapAnswer = sap(v, w);
        if (sapAnswer == null) {
            return -1;
        }
        return sapAnswer.getLength();
    }

    /**
     * Finds a common ancestor of v and w that participates in a shortest
     * ancestral path; -1 if no such path.
     *
     * @param v
     *            first vertex
     * @param w
     *            second vertex
     * @return a common ancestor of v and w that participates in a shortest
     *         ancestral path; -1 if no such path.
     */
    public int ancestor(final int v, final int w) {
        SapAnswer sapAnswer = sap(v, w);
        if (sapAnswer == null) {
            return -1;
        }
        return sapAnswer.getAncestor();
    }

    /**
     * Calculates the length of shortest ancestral path between any vertex in v
     * and any vertex in w; -1 if no such path.
     *
     * @param v
     *            first vertex
     * @param w
     *            second vertex
     * @return the length of shortest ancestral path between any vertex in v and
     *         any vertex in w; -1 if no such path.
     */
    public int length(final Iterable<Integer> v, final Iterable<Integer> w) {
        SapAnswer sapAnswer = sap(v, w);
        if (sapAnswer == null) {
            return -1;
        }
        return sapAnswer.getLength();
    }

    /**
     * Finds a common ancestor that participates in shortest ancestral path; -1
     * if no such path.
     *
     * @param v
     *            first vertex
     * @param w
     *            second vertex
     * @return a common ancestor that participates in shortest ancestral path;
     *         -1 if no such path.
     */
    public int ancestor(final Iterable<Integer> v, final Iterable<Integer> w) {
        SapAnswer sapAnswer = sap(v, w);
        if (sapAnswer == null) {
            return -1;
        }
        return sapAnswer.getAncestor();
    }

    /**
     * For unit testing of this class.
     *
     * @param args
     *            command line arguments (digraph file)
     */
    public static void main(final String[] args) {
        In in = new In(args[0]);
        Digraph graph = new Digraph(in);
        SAP sap = new SAP(graph);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }

    /**
     * Performs SAP query for two given vertices.
     *
     * @param v
     *            first vertex
     * @param w
     *            second vertex
     * @return answer for the query stored in SapAnswer object
     */
    private SapAnswer sap(final int v, final int w) {
        checkIndex(v);
        checkIndex(w);
        SapAnswer sapAnswer = sapBfs.sap(v, w);
        return sapAnswer;
    }

    /**
     * Performs SAP query for two given sets of vertices.
     *
     * @param v
     *            first set of vertices
     * @param w
     *            second set of vertices
     * @return answer for the query stored in SapAnswer object
     */
    private SapAnswer sap(final Iterable<Integer> v, final Iterable<Integer> w) {
        checkIndexes(v);
        checkIndexes(w);
        SapAnswer sapAnswer = sapBfs.sap(v, w);
        return sapAnswer;
    }

    /**
     * Tests all indexes. Throws IndexOutOfBoundsException if at least one of
     * them violates graph boundaries.
     *
     * @param indexes
     *            to test
     */
    private void checkIndexes(final Iterable<Integer> indexes) {
        for (int index : indexes) {
            checkIndex(index);
        }
    }

    /**
     * Tests given index. Throws IndexOutOfBoundsException it violates graph
     * boundaries.
     *
     * @param index
     *            to test
     */
    private void checkIndex(final int index) {
        Utils.check(0 <= index && index < digraph.V(),
                new IndexOutOfBoundsException());
    }
}
