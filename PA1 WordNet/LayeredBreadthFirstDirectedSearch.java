import java.util.Arrays;

/**
 * The LayeredBreadthFirstDirectedSearch class is designed to perform layered
 * breadth first search (BFS). Its may be useful when graph should be processed
 * layer by layer.
 * <p>
 * This implementation relies on the LayeredIntegerQueue queue implementation.
 * <p>
 * This implementation uses an integer array for marks to provide fast clearing.
 *
 * @author Sergey Esipenko
 */
public class LayeredBreadthFirstDirectedSearch {

    /**
     * Initial value for marked variable.
     */
    private static final int MARKED_INITIAL_VALUE = 1;

    /**
     * Threshold value for marked variable.
     */
    private static final int MARKED_THRESHOLD_VALUE = Integer.MAX_VALUE;

    /**
     * Graph for searching.
     */
    private final Digraph graph;

    /**
     * Special queue structure which supports layering.
     */
    private final LayeredIntegerQueue layeredQueue;

    /**
     * Array of marks.
     */
    private final int[] marks;

    /**
     * Array of dists.
     */
    private final int[] dists;

    /**
     * Current marked value.
     */
    private int marked = MARKED_INITIAL_VALUE;

    /**
     * Handler for vertex processing.
     */
    private VertexProcessingHandler vertexProcessingHandler;

    /**
     * Constructor takes a directed graph.
     *
     * @param directedGraph
     *            graph for searching
     */
    public LayeredBreadthFirstDirectedSearch(final Digraph directedGraph) {
        this.graph = directedGraph;
        this.layeredQueue = new LayeredIntegerQueue(graph.V());
        this.marks = new int[graph.V()];
        this.dists = new int[graph.V()];
    }

    /**
     * Prepare this object to search from specified source.
     *
     * @param source
     *            source for BFS
     */
    public final void prepare(final int source) {
        clearMarks();
        layeredQueue.clear();
        enqueue(source, 0);
    }

    /**
     * Prepare this object to search from specified sources.
     *
     * @param sources
     *            sources for BFS
     */
    public final void prepare(final Iterable<Integer> sources) {
        clearMarks();
        layeredQueue.clear();
        for (int v : sources) {
            enqueue(v, 0);
        }
    }

    /**
     * If next layer is non-empty?
     *
     * @return true if next layer is non-empty; false otherwise
     */
    public final boolean hasNextLayer() {
        return layeredQueue.hasNextLayer();
    }

    /**
     * Process next layer.
     */
    public final void nextLayer() {
        if (hasNextLayer()) {
            layeredQueue.nextLayer();
            runBfsOnCurrentLayer();
        }
    }

    /**
     * Is there a directed path from the source <tt>s</tt> (or sources) to
     * vertex <tt>v</tt>?
     *
     * @param v
     *            the vertex
     * @return <tt>true</tt> if there is a directed path, <tt>false</tt>
     *         otherwise
     */
    public final boolean hasPathTo(final int v) {
        return marks[v] == marked;
    }

    /**
     * Returns the number of edges in a shortest path from the source <tt>s</tt>
     * (or sources) to vertex <tt>v</tt>?
     *
     * @param v
     *            the vertex
     * @return the number of edges in a shortest path
     */
    public final int distTo(final int v) {
        return dists[v];
    }

    /**
     * Clears all marks.
     */
    private void clearMarks() {
        if (marked == MARKED_THRESHOLD_VALUE) {
            marked = MARKED_INITIAL_VALUE;
            Arrays.fill(marks, marked - 1);
        } else {
            marked++;
        }
    }

    /**
     * @return the vertexProcessingHandler
     */
    public final VertexProcessingHandler getVertexProcessingHandler() {
        return vertexProcessingHandler;
    }

    /**
     * @param processingHandler
     *            the vertexProcessingHandler to set
     */
    public final void setVertexProcessingHandler(
            final VertexProcessingHandler processingHandler) {
        this.vertexProcessingHandler = processingHandler;
    }

    /**
     * Actual BFS algorithm. It traverses the current layer only.
     */
    private void runBfsOnCurrentLayer() {
        while (!layeredQueue.isLayerEmpty()) {
            final int v = layeredQueue.pop();
            if (vertexProcessingHandler != null) {
                vertexProcessingHandler.onVertexVisited(v);
            }
            for (int nv : graph.adj(v)) {
                if (marks[nv] != marked) {
                    enqueue(nv, dists[v] + 1);
                }
            }
        }
    }

    /**
     * Adds vertex v to BFS queue.
     *
     * @param v
     *            vertex to visit
     * @param dist
     *            minimal distance from any source
     */
    private void enqueue(final int v, final int dist) {
        marks[v] = marked;
        dists[v] = dist;
        layeredQueue.push(v);
    }

    /**
     * Interface for vertex processing handlers.
     *
     * @author Sergey Esipenko
     */
    public interface VertexProcessingHandler {
        /**
         * Called for each visited vertex.
         *
         * @param v
         *            visited vertex
         */
        void onVertexVisited(final int v);
    }
}
