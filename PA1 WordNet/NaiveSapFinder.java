/**
 * Naive helper class for the SAP data type. This class do all work to find
 * shortest ancestral path (SAP) between two given sets of vertices in given
 * digraph. It works in general case, but quite slow.
 * <p>
 * This implementation uses BreadthFirstDirectedPaths helper types to perform
 * two breadth first searches from both sets and then finds optimal ancestor.
 *
 * @author Sergey Esipenko
 */
public class NaiveSapFinder extends AbstractSapFinder {

    /**
     * Constructor takes digraph.
     *
     * @param directedGraph
     *            digraph for SAP processing.
     */
    public NaiveSapFinder(final Digraph directedGraph) {
        super(directedGraph);
    }

    @Override
    public final SapAnswer sap(final int first, final int second) {
        BreadthFirstDirectedPaths bfs1 = new BreadthFirstDirectedPaths(
                getDigraph(), first);
        BreadthFirstDirectedPaths bfs2 = new BreadthFirstDirectedPaths(
                getDigraph(), second);
        return sap(bfs1, bfs2);
    }

    @Override
    public final SapAnswer sap(final Iterable<Integer> first,
            final Iterable<Integer> second) {
        BreadthFirstDirectedPaths bfs1 = new BreadthFirstDirectedPaths(
                getDigraph(), first);
        BreadthFirstDirectedPaths bfs2 = new BreadthFirstDirectedPaths(
                getDigraph(), second);
        return sap(bfs1, bfs2);
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
    private SapAnswer sap(final BreadthFirstDirectedPaths bfs1,
            final BreadthFirstDirectedPaths bfs2) {
        SapAnswer sa = null;
        for (int v = 0; v < getDigraph().V(); v++) {
            if (bfs1.hasPathTo(v) && bfs2.hasPathTo(v)) {
                final int length = bfs1.distTo(v) + bfs2.distTo(v);
                if (sa == null || sa.getLength() > length) {
                    sa = new SapAnswer(length, v);
                }
            }
        }
        return sa;
    }

}
