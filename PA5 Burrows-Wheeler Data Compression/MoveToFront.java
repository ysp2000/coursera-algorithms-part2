
/***
 * Move-to-front encoding and decoding. 
 * 
 * The main idea of move-to-front encoding is to maintain an ordered sequence of 
 * the characters in the alphabet, and repeatedly read in a character from the 
 * input message, print out the position in which that character appears, and 
 * move that character to the front of the sequence.
 * 
 * @author Sergey Esipenko
 */
public class MoveToFront {

    private static final int ALPHABET_SIZE = 256;

    // apply move-to-front encoding, reading from standard input and writing to
    // standard output
    public static void encode() {
        // initialize permutation
        final int[] permutation = permutation(ALPHABET_SIZE);
        while (!BinaryStdIn.isEmpty()) {
            final char c = BinaryStdIn.readChar();
            for (int i = 0; i < permutation.length; i++) {
                if (permutation[i] == c) {
                    System.arraycopy(permutation, 0, permutation, 1, i);
                    permutation[0] = c;
                    BinaryStdOut.write((byte) i);
                    break;
                }
            }
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to
    // standard output
    public static void decode() {
        final int[] permutation = permutation(ALPHABET_SIZE);
        // naive O(R*N) encoding
        while (!BinaryStdIn.isEmpty()) {
            final int i = BinaryStdIn.readChar();
            final int c = permutation[i];
            BinaryStdOut.write((byte) c);
            System.arraycopy(permutation, 0, permutation, 1, i);
            permutation[0] = c;
        }
        BinaryStdOut.close();
    }

    private static int[] permutation(final int n) {
        final int[] p = new int [n];
        for (int i = 0; i < p.length; i++) p[i] = i;
        return p;
    }

    // if args[0] is '-', apply move-to-front encoding
    // if args[0] is '+', apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-")) {
            encode();
        } else if (args[0].equals("+")) {
            decode();
        } else {
            System.out.println("Usage: '-' encoding, '+' decoding");
        }
    }
}