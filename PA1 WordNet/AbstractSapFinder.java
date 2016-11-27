/**
 * The AbstractSapFinder abstract class defines basic features of SapFinder.
 *
 * @author Sergey Esipenko
 */
public abstract class AbstractSapFinder implements SapFinder {

    /**
     * Digraph where BFS is running.
     */
    private final Digraph digraph;

    /**
     * Constructor takes digraph.
     *
     * @param directedGraph
     *            digraph for SAP processing.
     */
    public AbstractSapFinder(final Digraph directedGraph) {
        this.digraph = directedGraph;
    }

    /**
     * Getter for digraph.
     *
     * @return digraph connected to object
     */
    public final Digraph getDigraph() {
        return digraph;
    }
}
