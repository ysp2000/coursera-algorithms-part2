/**
 * The Outcast class is designed to outcast detection.
 *
 * @author Sergey Esipenko
 */
public class Outcast {

    /**
     * Word net with necessary nouns and connections.
     */
    private final WordNet wordNet;

    /**
     * Constructor takes a WordNet object.
     *
     * @param wordnet
     *            word net with necessary nouns and connections
     */
    public Outcast(final WordNet wordnet) {
        this.wordNet = wordnet; // wordnet is immutable
    }

    /**
     * For given set of nouns determines an outcast.
     *
     * @param nouns
     *            set of nouns
     * @return outcast
     */
    public String outcast(final String[] nouns) {
        String outcast = null;
        int outcastDist = -1;
        for (String noun : nouns) {
            int dist = 0;
            for (String test : nouns) {
                dist += wordNet.distance(noun, test);
            }
            if (outcastDist < dist) {
                outcastDist = dist;
                outcast = noun;
            }
        }
        return outcast;
    }

    /**
     * for unit testing of this class (such as the one below).
     *
     * @param args
     *            command line arguments (synsets file, hypernyms file, outcast
     *            files)
     */
    public static void main(final String[] args) {
    	System.err.println(System.getProperty("user.dir"));
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            String[] nouns = new In(args[t]).readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }

}
