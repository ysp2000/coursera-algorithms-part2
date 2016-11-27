/**
 * The LayeredIntegerQueue class is a fast queue implementation with layering.
 * <p>
 * This implementation uses an array to store elements.
 *
 * @author Sergey Esipenko
 */
public class LayeredIntegerQueue {

    /**
     * Array for storing queue elements.
     */
    private final int[] queue;


    /**
     * Pointer to the beginning of queue.
     */
    private int head;

    /**
     * Pointer to the end of queue.
     */
    private int tail;

    /**
     * Pointer to the end of current layer.
     */
    private int layerEnd;

    /**
     * Initializes an empty queue.
     *
     * @param poolSize
     *            pool size (maximal allowed number of pushes)
     */
    public LayeredIntegerQueue(final int poolSize) {
        queue = new int [poolSize];
    }

    /**
     * Clears queue.
     */
    public final void clear() {
        head = 0;
        tail = 0;
        layerEnd = 0;
    }

    /**
     * Adds element to the end of this queue.
     *
     * @param x
     *            the element to add
     */
    public final void push(final int x) {
        queue[tail++] = x;
    }

    /**
     * Removes and returns the element from the beginning of this queue.
     *
     * @return the element from the beginning of this queue
     */
    public final int pop() {
        return queue[head++];
    }

    /**
     * Is the current layer empty?
     *
     * @return true if the current layer is empty; false otherwise
     */
    public final boolean isLayerEmpty() {
        return head >= layerEnd;
    }

    /**
     * Is the next layer non-empty?
     *
     * @return true if the next layer is not empty; false otherwise
     */
    public final boolean hasNextLayer() {
        return layerEnd < tail;
    }

    /**
     * Switches to next layer.
     */
    public final void nextLayer() {
        layerEnd = tail;
    }
}
