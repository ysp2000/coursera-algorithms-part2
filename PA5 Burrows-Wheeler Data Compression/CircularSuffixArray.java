
/***
 * Circular Suffix Array is a fundamental data structure, 
 * which describes the abstraction of a sorted array of 
 * the n circular suffixes of a string of length n.
 * 
 * @author Sergey Esipenko
 */
public class CircularSuffixArray {

    private static final int CUTOFF = 8;
    private static final int SHIFT_BITS = 8;
    private static final int MAX_CODE_LENGTH = 7;
    private int hashLen;

    private final int len;
    private final char[] s;
    private final int[] suffixArray;
    private final long[] codes;

    public CircularSuffixArray(String string) {
        this.s = string.toCharArray();
        this.len = s.length;
        this.codes = buildCodes();
        this.suffixArray = buildSuffixArray();
    }

    public int length() {
        return suffixArray.length;
    }

    public int index(int i) {
        return suffixArray[i];
    }

    private long[] buildCodes() {
        final long[] codes = new long [len];
        hashLen = Math.min(len, MAX_CODE_LENGTH);
        long hash = 0L;
        for (int i = 0; i < hashLen; i++)
            hash = (hash << SHIFT_BITS) | s[i];
        codes[0] = hash;
        final int shiftPow = SHIFT_BITS * (hashLen - 1);
        for (int i = 0, j = hashLen % len; i + 1 < len; i++) {
            hash -= ((long) s[i]) << shiftPow;
            hash = (hash << SHIFT_BITS) | s[j];
            codes[i + 1] = hash;
            if (++j >= len) j = 0;
        }
        return codes;
    }

    // naive O(N^2 log N) algorithm
    private int[] buildSuffixArray() {
        final int[] suffixArray = new int [len];
        for (int i = 0; i < len; i++) suffixArray[i] = i;
        sort(suffixArray, 0, len - 1);
        return suffixArray;
    }

    int cmp(int pos1, int pos2) {
        if (codes[pos1] != codes[pos2])
            return codes[pos1] < codes[pos2] ? -1 : 1;
        pos1 += hashLen; if (pos1 >= len) pos1 -= len;
        pos2 += hashLen; if (pos2 >= len) pos2 -= len;
        for (int i = hashLen; i < len; ++i) {
            if (s[pos1] != s[pos2])
                return s[pos1] - s[pos2];
            if (++pos1 == len) pos1 = 0;
            if (++pos2 == len) pos2 = 0;
        }
        return 0;
    }

    private void sort(int[] a, int lo, int hi) {
        int N = hi - lo + 1;

        // cutoff to insertion sort
        if (N <= CUTOFF) {
            insertionSort(a, lo, hi);
            return;
        }

        // use median-of-3 as partitioning element
        else if (N <= 40) {
            int m = median3(a, lo, lo + N/2, hi);
            exch(a, m, lo);
        }

        // use Tukey ninther as partitioning element
        else  {
            int eps = N/8;
            int mid = lo + N/2;
            int m1 = median3(a, lo, lo + eps, lo + eps + eps);
            int m2 = median3(a, mid - eps, mid, mid + eps);
            int m3 = median3(a, hi - eps - eps, hi - eps, hi);
            int ninther = median3(a, m1, m2, m3);
            exch(a, ninther, lo);
        }

        // Bentley-McIlroy 3-way partitioning
        int i = lo, j = hi+1;
        int p = lo, q = hi+1;
        while (true) {
            final int v = a[lo];
            while (cmp(a[++i], v) < 0)
                if (i == hi) break;
            while (cmp(v, a[--j]) < 0)
                if (j == lo) break;
            if (i >= j) break;
            exch(a, i, j);
            if (cmp(a[i], v) == 0) exch(a, ++p, i);
            if (cmp(a[j], v) == 0) exch(a, --q, j);
        }
        exch(a, lo, j);

        i = j + 1;
        j = j - 1;
        for (int k = lo+1; k <= p; k++) exch(a, k, j--);
        for (int k = hi  ; k >= q; k--) exch(a, k, i++);

        sort(a, lo, j);
        sort(a, i, hi);
    }

    // sort from a[lo] to a[hi] using insertion sort
    private void insertionSort(int[] a, int lo, int hi) {
        for (int i = lo; i <= hi; i++)
            for (int j = i; j > lo && cmp(a[j], a[j-1]) < 0; j--)
                exch(a, j, j-1);
    }


    // return the index of the median element among a[i], a[j], and a[k]
    private int median3(final int[] a, final int i, final int j, final int k) {
        return ((cmp(a[i], a[j]) < 0) ?
               ((cmp(a[j], a[k]) < 0) ? j : (cmp(a[i], a[k]) < 0) ? k : i) :
               ((cmp(a[k], a[j]) < 0) ? j : (cmp(a[k], a[i]) < 0) ? k : i));
    }

    // exchange a[i] and a[j]
    private static void exch(final int[] a, final int i, final int j) {
        final int swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }
}