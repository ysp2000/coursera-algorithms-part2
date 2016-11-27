/**
 * The SapAnswer class is designed for storing answer for sap query (length and
 * ancestor index).
 *
 * @author Sergey Esipenko
 */
public class SapAnswer {

    /**
     * Length of SAP.
     */
    private final int mLength;

    /**
     * Ancestor's index.
     */
    private final int mAncestor;

    /**
     * Constructor takes length and ancestor index.
     *
     * @param length
     *            length of SAP
     * @param ancestor
     *            ancestor's index
     */
    public SapAnswer(final int length, final int ancestor) {
        this.mLength = length;
        this.mAncestor = ancestor;
    }

    /**
     * Returns length of SAP.
     * @return length of SAP
     */
    public final int getLength() {
        return mLength;
    }

    /**
     * Returns ancestor's index.
     * @return ancestor's index
     */
    public final int getAncestor() {
        return mAncestor;
    }
}
