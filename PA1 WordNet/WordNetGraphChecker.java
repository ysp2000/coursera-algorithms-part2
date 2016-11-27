/**
 * Helper class for testing given digraph. It check that given graph is DAG and
 * contains only one sink (vertex without outgoing edges).
 *
 * @author Sergey Esipenko
 */
public final class WordNetGraphChecker {

    /**
     * Private constructor. Prevents from instancing.
     */
    private WordNetGraphChecker() {
    }

    /**
     * Checks whether given digraph is a DAG with exactly one sink (vertex
     * without edges).
     *
     * @param digraph
     *            digraph to check
     * @return true if graph is DAG with exactly one sink; false otherwise
     */
    public static boolean check(final Digraph digraph) {
        if (digraph.V() == 0) {
            return true;
        }
        DirectedCycle finder = new DirectedCycle(digraph);
        if (finder.hasCycle()) {
            return false;
        }
        int sinksCounter = 0;
        for (int v = 0; v < digraph.V(); v++) {
            if (!digraph.adj(v).iterator().hasNext()) {
                sinksCounter++;
            }
        }
        return sinksCounter == 1;
    }

}
