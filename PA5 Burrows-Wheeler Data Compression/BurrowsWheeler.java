import java.util.Arrays;

/***
 * Burrows-Wheeler data compression algorithm implementation.
 * 
 * @author Sergey Esipenko
 */
public class BurrowsWheeler {

    private static final int ALPHABET_SIZE = 256;

    // apply Burrows-Wheeler encoding, reading from standard input and writing
    // to standard output
    public static void encode() {
        final String s = BinaryStdIn.readString();
        final int length = s.length();
        final int offset = s.length() - 1;
        final CircularSuffixArray a = new CircularSuffixArray(s);
        for (int i = 0; i < length; i++) {
            if (a.index(i) == 0) {
                BinaryStdOut.write(i);
                break;
            }
        }
        for (int i = 0; i < a.length(); i++)
            BinaryStdOut.write(s.charAt((a.index(i) + offset) % length));
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler decoding, reading from standard input and writing
    // to standard output
    public static void decode() {
        final int first = BinaryStdIn.readInt();
        final char[] str = BinaryStdIn.readString().toCharArray();
        final int[] counts = new int [ALPHABET_SIZE];
        final int[][] endsLists = new int [ALPHABET_SIZE][];
        for (final char c : str) ++counts[c];
        for (int i = 0; i < endsLists.length; i++)
            endsLists[i] = new int [counts[i]];
        Arrays.fill(counts, 0);
        for (int i = 0; i < str.length; i++) {
            final char c = str[i];
            endsLists[c][counts[c]++] = i;
        }
        int pos = 0;
        for (char c = 0; c < ALPHABET_SIZE; c++)
            for (int i = 0; i < counts[c]; i++)
                str[pos++] = c;
        final int[] next = new int [str.length];
        Arrays.fill(counts, 0);
        for (int i = 0; i < str.length; i++) {
            final char c = str[i];
            next[i] = endsLists[c][counts[c]++];
        }
        for (int i = first, k = 0; k < str.length; i = next[i], k++)
            BinaryStdOut.write(str[i]);
        BinaryStdOut.close();
    }

    // if args[0] is '-', apply Burrows-Wheeler encoding
    // if args[0] is '+', apply Burrows-Wheeler decoding
    public static void main(String[] args) {
        if (args[0].equals("-")) {
            encode();
        } else if (args[0].equals("+")) {
            decode();
        } else {
            System.out.println("Usage: '-' encoding, '+' decoding");
        }
    }

    private static void test(final String s) {
        final int length = s.length();
        final int offset = s.length() - 1;
        final CircularSuffixArray a = new CircularSuffixArray(s);
        for (int i = 0; i < a.length(); i++)
            System.err.println(s.charAt((a.index(i) + offset) % length));
    }

    private static void testInvert(final int first, final String s) {
        final char[] str = s.toCharArray();
        final int[] counts = new int [ALPHABET_SIZE];
        final int[][] endsLists = new int [ALPHABET_SIZE][];
        for (final char c : str) ++counts[c];
        for (int i = 0; i < endsLists.length; i++)
            endsLists[i] = new int [counts[i]];
        Arrays.fill(counts, 0);
        for (int i = 0; i < str.length; i++) {
            final char c = str[i];
            endsLists[c][counts[c]++] = i;
        }
        int pos = 0;
        for (char c = 0; c < ALPHABET_SIZE; c++)
            for (int i = 0; i < counts[c]; i++)
                str[pos++] = c;
        final int[] next = new int [str.length];
        Arrays.fill(counts, 0);
        for (int i = 0; i < str.length; i++) {
            final char c = str[i];
            next[i] = endsLists[c][counts[c]++];
        }
        System.err.println(Arrays.toString(next));
        for (int i = first, k = 0; k < str.length; i = next[i], k++)
            System.err.println(i);
        System.err.println();
        for (int i = first, k = 0; k < str.length; i = next[i], k++)
            System.err.println(str[i]);
    }

}
