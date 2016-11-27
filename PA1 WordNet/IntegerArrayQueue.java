/**
 * Fast queue of int values for BFS.
 * <p>
 * This implementation uses an array to store elements.
 *
 * @author Sergey Esipenko
 */
public class IntegerArrayQueue {

    /**
     * Array for storing queue elements.
     */
    private final int[] queue;

    /**
     * Pointer to the end of queue.
     */
    private int tail;

    /**
     * Pointer to the beginning of queue.
     */
    private int head;

    /**
     * Initializes an empty queue.
     *
     * @param size
     *            pool size (maximal allowed number of pushes)
     */
    public IntegerArrayQueue(final int size) {
        this.queue = new int[size];
        clear();
    }

    /**
     * Clears queue.
     */
    public final void clear() {
        head = 0;
        tail = 0;
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
     * Is this queue empty?
     *
     * @return true if this queue is empty; false otherwise
     */
    public final boolean isEmpty() {
        return head >= tail;
    }
}
