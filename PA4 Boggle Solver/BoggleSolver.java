/*****************************************************************************
 * BoggleSolver by Sergey Esipenko, 2013
 *****************************************************************************/
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BoggleSolver {
    private static final char EMPTY_CHAR = 0;
    private static final int TRIE_NOT_TERMINAL = -1;
    private static final int TRIE_ROOT = 0;
    private static final int TERMINAL_COUNTS_CUTOFF = 100;
    private static final int BITS_PER_LETTER = 5;
    private static final int LETTER_Q = 'Q' - 'A';
    private static final int LETTER_U = 'U' - 'A';
    private static final int[] LENGTH_SCORES = { 0, 0, 0, 1, 1, 2, 3, 5, 11 };
    private static final int[] DELTA_ROW = { -1, -1, -1,  0, +1, +1, +1,  0 };
    private static final int[] DELTA_COL = { -1,  0, +1, +1, +1,  0, -1, -1 };
    private static final int DEFAULT_BOARD_SIZE = 4 * 4;
    private static final int BITS_PER_MOVES = 3;

    private final String[] words;

    private final int[] trieParents;
    private final int[] trieTerminals;
    private final int[] trieTerminalsCount;
    private final int[] trieTransitions;

    private int trieVertsCount = 0;

    private int boardVertsCount;
    private int[] boardLetters = new int [DEFAULT_BOARD_SIZE];
    private int[] boardAdjCounts = new int [DEFAULT_BOARD_SIZE];
    private int[] boardMoves = new int [DEFAULT_BOARD_SIZE << BITS_PER_MOVES];
    private boolean[] boardUsed = new boolean [DEFAULT_BOARD_SIZE];

    private int[] curAdj = new int [DEFAULT_BOARD_SIZE];
    private int[] stack = new int [2 * DEFAULT_BOARD_SIZE];

    private int oldRows = -1;
    private int oldCols = -1;

    private final int[] curCounts;
    private final int[] curMarks;
    private int mark = 1;
    private final int[] isValidWord;

    public BoggleSolver(String[] dictionary) {
        this.words = filterShortWords(dictionary, 3);
        final int totalVerts = 1 + countVerts(0, 0, this.words.length - 1);
        trieParents = new int [totalVerts];
        this.trieTerminals = new int [totalVerts];
        this.trieTerminalsCount = new int [totalVerts];
        this.trieTransitions = new int [totalVerts << BITS_PER_LETTER];
        Arrays.fill(this.trieTerminals, TRIE_NOT_TERMINAL);
        this.trieVertsCount++; // alloc root
        buildTrie(TRIE_ROOT, 0, 0, this.words.length - 1);
        isValidWord = new int [this.words.length];
        curCounts = new int [trieVertsCount];
        curMarks = new int [trieVertsCount];
        mark = 1;
    }

    public Iterable<String> getAllValidWords(BoggleBoard board) {
        mark++;
        final int nRows = board.rows();
        final int nCols = board.cols();
        boardVertsCount = nRows * nCols;
        if (boardLetters.length < boardVertsCount) {
            boardLetters = new int [boardVertsCount];
            boardAdjCounts = new int [boardVertsCount];
            boardMoves = new int [boardVertsCount << BITS_PER_MOVES];
            boardUsed = new boolean [boardVertsCount];
            curAdj = new int [boardVertsCount];
            stack = new int [2 * boardVertsCount];
            oldRows = oldCols = -1;
        }
        for (int row = 0; row < nRows; row++) {
            for (int col = 0; col < nCols; col++) {
                final int v = row * nCols + col;
                boardLetters[v] = board.getLetter(row, col) - 'A';
            }
        }
        Arrays.fill(boardUsed, 0, boardVertsCount, false);
        if (nRows != oldRows || nCols != oldCols) {
            for (int row = 0; row < nRows; row++) {
                for (int col = 0; col < nCols; col++) {
                    final int v = row * nCols + col;
                    boardAdjCounts[v] = 0;
                    for (int d = 0; d < DELTA_ROW.length; d++) {
                        final int newRow = row + DELTA_ROW[d];
                        final int newCol = col + DELTA_COL[d];
                        if (0 <= newRow && newRow < nRows &&
                                0 <= newCol && newCol < nCols) {
                            int i = boardAdjCounts[v]++;
                            boardMoves[(v << BITS_PER_MOVES) | i] = newRow * nCols + newCol;
                        }
                    }
                }
            }
            oldRows = nRows;
            oldCols = nCols;
        }
        final List<String> validWords = new ArrayList<String>();
        for (int startBoardVertex = 0; startBoardVertex < boardVertsCount; startBoardVertex++) {
            final int startTrieVertex = nextTrieVertex(TRIE_ROOT, boardLetters[startBoardVertex]);
            if (startTrieVertex == TRIE_ROOT) continue;
            int stackPointer = 0;
            curAdj[startBoardVertex] = 0;
            stack[stackPointer++] = startBoardVertex;
            stack[stackPointer++] = startTrieVertex;
            while (stackPointer > 0) {
                final int boardVertex = stack[stackPointer - 2];
                final int trieVertex = stack[stackPointer - 1];
                if (curAdj[boardVertex] == 0) { // entering
                    boardUsed[boardVertex] = true;
                    final int wordIndex = trieTerminals[trieVertex];
                    if (wordIndex != TRIE_NOT_TERMINAL && isValidWord[wordIndex] != mark) {
                        validWords.add(words[wordIndex]);
                        isValidWord[wordIndex] = mark;
                        for (int v = trieVertex; v != TRIE_ROOT && trieTerminalsCount[v] < TERMINAL_COUNTS_CUTOFF; v = trieParents[v])
                            incCount(v);
                    }
                }
                if (curAdj[boardVertex] < boardAdjCounts[boardVertex]) {
                    if (restWords(trieVertex) > 0) {
                        final int pos = (boardVertex << BITS_PER_MOVES) | curAdj[boardVertex];
                        final int boardNextVertex = boardMoves[pos];
                        if (!boardUsed[boardNextVertex]) {
                            final int newTrieVertex = nextTrieVertex(trieVertex, boardLetters[boardNextVertex]);
                            if (newTrieVertex != TRIE_ROOT) {
                                curAdj[boardNextVertex] = 0;
                                stack[stackPointer++] = boardNextVertex;
                                stack[stackPointer++] = newTrieVertex;
                            }
                        }
                    }
                    curAdj[boardVertex]++;
                } else {
                    boardUsed[boardVertex] = false;
                    stackPointer -= 2;
                }
            }
        }
        return validWords;
    }

    public int scoreOf(String word) {
        if (contains(word)) {
            return LENGTH_SCORES[Math.min(LENGTH_SCORES.length - 1, word.length())];
        }
        return 0;
    }

    private int restWords(final int v) {
        return trieTerminalsCount[v] - curCount(v);
    }

    private int curCount(int v) {
        if (curMarks[v] != mark) {
            curMarks[v] = mark;
            curCounts[v] = 0;
        }
        return curCounts[v];
    }

    private void incCount(int v) {
        if (curMarks[v] != mark) {
            curMarks[v] = mark;
            curCounts[v] = 0;
        }
        curCounts[v]++;
    }

    private int nextTrieVertex(final int trieVertex, final int letter) {
        int nextVertex = trieTransitions[(trieVertex << BITS_PER_LETTER) | letter];
        if (nextVertex != TRIE_ROOT && letter == LETTER_Q)
            nextVertex = trieTransitions[(nextVertex << BITS_PER_LETTER) | LETTER_U];
        return nextVertex;
    }

    private int countVerts(final int pos, int lo, int hi) {
        if (lo == hi) return words[lo].length() - pos;
        int count = 0;
        while (lo <= hi) {
            while (lo <= hi && getChar(words[lo], pos) == EMPTY_CHAR) lo++;
            if (lo > hi) break;
            final char groupChar = getChar(words[lo], pos);
            int i = lo;
            while (i + 1 <= hi && getChar(words[i + 1], pos) == groupChar) i++;
            count += 1 + countVerts(pos + 1, lo, i);
            lo = i + 1;
        }
        return count;
    }

    private void buildTrie(int v, int pos, int lo, int hi) {
        if (lo == hi) {
            final String s = words[lo];
            for (int i = pos; i < s.length(); i++) {
                setTransition(v, s.charAt(i), trieVertsCount);
                v = trieVertsCount++;
            }
            setTerminal(v, lo);
            return;
        }
        while (lo <= hi) {
            if (getChar(words[lo], pos) == EMPTY_CHAR) {
                setTerminal(v, lo);
                while (lo <= hi && getChar(words[lo], pos) == EMPTY_CHAR) lo++;
                if (lo > hi) break;
            }
            final char groupChar = getChar(words[lo], pos);
            int i = lo;
            while (i + 1 <= hi && getChar(words[i + 1], pos) == groupChar) i++;
            final int nv = trieVertsCount++;
            setTransition(v, groupChar, nv);
            buildTrie(nv, pos + 1, lo, i);
            lo = i + 1;
        }
    }

    private void setTerminal(int v, final int index) {
        trieTerminals[v] = index;
        for (; v != TRIE_ROOT && trieTerminalsCount[v] < TERMINAL_COUNTS_CUTOFF; v = trieParents[v])
            trieTerminalsCount[v]++;
    }

    private void setTransition(final int v, final char c, final int nv) {
        trieParents[nv] = v;
        trieTransitions[(v << BITS_PER_LETTER) | (c - 'A')] = nv;
    }

    private boolean contains(String w) {
        int v = TRIE_ROOT;
        for (int i = 0; i < w.length(); i++) {
            v = trieTransitions[(v << BITS_PER_LETTER) | (w.charAt(i) - 'A')];
            if (v == TRIE_ROOT) return false;
        }
        return trieTerminals[v] != TRIE_NOT_TERMINAL;
    }

    private static String[] filterShortWords(String[] dictionary, int minimalWordLength) {
        final List<String> words = new ArrayList<String>(dictionary.length);
        for (final String word : dictionary) {
            if (word.length() >= minimalWordLength) {
                words.add(word);
            }
        }
        final String[] longWords = new String [words.size()];
        for (int i = 0; i < words.size(); i++) {
            longWords[i] = words.get(i);
        }
        return longWords;
    }

    private static final char getChar(final String s, final int i) {
        return i < s.length() ? s.charAt(i) : EMPTY_CHAR;
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        final String[] dictionary = in.readAllStrings();
        in.close();
        final BoggleSolver solver = new BoggleSolver(dictionary);
        final BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        solver.getAllValidWords(board);
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word + " " + solver.scoreOf(word));
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
