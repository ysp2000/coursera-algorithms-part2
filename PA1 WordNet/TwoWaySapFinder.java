/**
 * Optimized helper class for the SAP data type. This class do all work to find
 * shortest ancestral path (SAP) between two given sets of vertices in given
 * digraph. It supposed to work in general case.
 * <p>
 * This implementation uses LayeredBreadthFirstDirectedSearch helper types to
 * perform two breadth first searches from both sets simultaneously layer by
 * layer. Updates answer on each layer processing.
 *
 * @author Sergey Esipenko
 */
public class TwoWaySapFinder extends AbstractSapFinder {

    /**
     * First LayeredBreadthFirstDirectedSearch helper object.
     */
    private final LayeredBreadthFirstDirectedSearch helperBfs1;

    /**
     * Second LayeredBreadthFirstDirectedSearch helper object.
     */
    private final LayeredBreadthFirstDirectedSearch helperBfs2;

    /**
     * Best answer for query.
     */
    private SapAnswer sapAnswer;

    /**
     * Constructor takes digraph.
     *
     * @param directedGraph
     *            digraph for SAP processing.
     */
    public TwoWaySapFinder(final Digraph directedGraph) {
        super(directedGraph);
        helperBfs1 = new LayeredBreadthFirstDirectedSearch(getDigraph());
        helperBfs2 = new LayeredBreadthFirstDirectedSearch(getDigraph());
        attachHandlers(helperBfs1, helperBfs2);
    }

    @Override
    public final SapAnswer sap(final int first, final int second) {
        helperBfs1.prepare(first);
        helperBfs2.prepare(second);
        return sap(helperBfs1, helperBfs2);
    }

    @Override
    public final SapAnswer sap(final Iterable<Integer> first,
            final Iterable<Integer> second) {
        helperBfs1.prepare(first);
        helperBfs2.prepare(second);
        return sap(helperBfs1, helperBfs2);
    }

    /**
     * Finds optimal ancestor and returns corresponding SapAnswer object.
     *
     * @param bfs1
     *            helper BreadthFirstDirectedPaths object for the first set of
     *            vertices
     * @param bfs2
     *            helper BreadthFirstDirectedPaths object for the second set of
     *            vertices
     * @return answer for the query stored in the SapAnswer object
     */
    private SapAnswer sap(final LayeredBreadthFirstDirectedSearch bfs1,
            final LayeredBreadthFirstDirectedSearch bfs2) {
        sapAnswer = null; // resets previous answer
        for (int layer = 0; sapAnswer == null || sapAnswer.getLength() > layer;
                layer++) {
            if (bfs1.hasNextLayer() || bfs2.hasNextLayer()) {
                bfs1.nextLayer();
                bfs2.nextLayer();
            } else {
                break;
            }
        }
        return sapAnswer;
    }

    /**
     * Connects to helper BFS objects by attaching correct handlers to both of
     * them.
     *
     * @param bfs1
     *            first helper BFS object to attaching handler
     * @param bfs2
     *            second helper BFS object to attaching handler
     */
    private void attachHandlers(final LayeredBreadthFirstDirectedSearch bfs1,
            final LayeredBreadthFirstDirectedSearch bfs2) {
        attachHandler(bfs1, bfs2);
        attachHandler(bfs2, bfs1);
    }

    /**
     * Initialize correct handler for pair of LayeredBreadthFirstDirectedSearch
     * objects and attaches it to corresponding object. When the first BFS
     * object visits vertex v handler tries to update answer by examining v in
     * the second BFS object.
     *
     * @param bfs1
     *            first BFS helper object (it visits vertex v)
     * @param bfs2
     *            second BFS helper object (it provides information about vertex
     *            v)
     */
    private void attachHandler(final LayeredBreadthFirstDirectedSearch bfs1,
            final LayeredBreadthFirstDirectedSearch bfs2) {
        bfs1.setVertexProcessingHandler(new LayeredBreadthFirstDirectedSearch
                .VertexProcessingHandler() {
            @Override
            public void onVertexVisited(final int v) {
                if (bfs2.hasPathTo(v)) {
                    final int length = bfs1.distTo(v) + bfs2.distTo(v);
                    if (sapAnswer == null || sapAnswer.getLength() > length) {
                        sapAnswer = new SapAnswer(length, v);
                    }
                }
            }
        });
    }

}
