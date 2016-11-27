import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * The WordNet class is designed to maintain set of synsets and relations
 * between them.
 * <p>
 * This implementation uses DagSapFinder data type for processing SAP queries.
 *
 * @author Sergey Esipenko
 */
public class WordNet {

    /**
     * Map for storing connection between nouns and associated synset
     * identifiers.
     */
    private final Map<String, List<Integer>> nounsIndexes;

    /**
     * Simple array which is used to obtain string representation of synset by
     * its identifier.
     */
    private String[] synsets;

    /**
     * Directed graph for storing ancestral relations.
     */
    private Digraph digraph;

    /**
     * SAP helper object for processing distance() and sap() operations.
     */
    private final SapFinder sapFinder;

    /**
     * Constructor takes the name of the two input files.
     *
     * @param synsetsFile
     *            name of synsets file
     * @param hypernymsFile
     *            name of synsets file
     */
    public WordNet(final String synsetsFile, final String hypernymsFile) {
        nounsIndexes = new HashMap<String, List<Integer>>();
        readSynsets(synsetsFile);
        readHypernyms(hypernymsFile);
        Utils.check(WordNetGraphChecker.check(digraph),
                new IllegalArgumentException(
                        "Given graph isn't DAG with one root"));
        sapFinder = new TwoWaySapFinder(digraph);
    }

    /**
     * The set of nouns (no duplicates), returned as an Iterable.
     *
     * @return iterable sequence of all nouns in WordNet
     */
    public Iterable<String> nouns() {
        return new LinkedList<String>(nounsIndexes.keySet());
    }

    /**
     * Is the word a WordNet noun?
     *
     * @param word
     *            word to test
     * @return true if word is noun stored in WordNet; false otherwise
     */
    public boolean isNoun(final String word) {
        return nounsIndexes.containsKey(word);
    }

    /**
     * Distance between nounA and nounB (length of ancestral path).
     *
     * @param nounA
     *            first noun
     * @param nounB
     *            second noun
     * @return shortest possible length of ancestral path between any synset of
     *         nounA and any synset of nounB.
     */
    public int distance(final String nounA, final String nounB) {
        Iterable<Integer> aIndexes = getNounIndexes(nounA);
        Iterable<Integer> bIndexes = getNounIndexes(nounB);
        return sapFinder.sap(aIndexes, bIndexes).getLength();
    }

    /**
     * A synset (second field of synsets.txt) that is the common ancestor of
     * nounA and nounB in a shortest ancestral path.
     *
     * @param nounA
     *            first noun
     * @param nounB
     *            second noun
     * @return synset which is ancestor of nounA and nounB and lay on any
     *         shortest ancestral path.
     */
    public String sap(final String nounA, final String nounB) {
        Iterable<Integer> aIndexes = getNounIndexes(nounA);
        Iterable<Integer> bIndexes = getNounIndexes(nounB);
        int ancestor = sapFinder.sap(aIndexes, bIndexes).getAncestor();
        return synsets[ancestor]; // ancestor != -1
    }

    /**
     * For unit testing of this class.
     *
     * @param args
     *            command line arguments (synsets file, hypernyms file)
     */
    public static void main(final String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        while (!StdIn.isEmpty()) {
            String nounA = StdIn.readString();
            String nounB = StdIn.readString();
            int distance = wordnet.distance(nounA, nounB);
            String sap = wordnet.sap(nounA, nounB);
            StdOut.printf("distance = %d, sap = %s\n", distance, sap);
        }
    }

    /**
     * Reads file with synsets description and builds DAG to maintain ancestral
     * relation.
     *
     * @param synsetsFile
     *            file with synstes description
     */
    private void readSynsets(final String synsetsFile) {
        List<String> synsetsDescriptions = new ArrayList<String>();
        In in = new In(synsetsFile);
        while (in.hasNextLine()) {
            synsetsDescriptions.add(in.readLine());
        }
        in.close();
        synsets = new String[synsetsDescriptions.size()];
        digraph = new Digraph(synsetsDescriptions.size());
        for (String synsetDescription : synsetsDescriptions) {
            parseSynsetDescription(synsetDescription);
        }
    }

    /**
     * Reads file with description of ancestral relations and adds appropriate
     * directed edges to DAG. All edges go from hyponym (more specific synset)
     * to hypernym (more general synset).
     *
     * @param hypernymsFile
     *            file with ancestral relations
     */
    private void readHypernyms(final String hypernymsFile) {
        In in = new In(hypernymsFile);
        while (in.hasNextLine()) {
            StringTokenizer hypernymsTok = new StringTokenizer(in.readLine(),
                    ",");
            int hyponym = Integer.parseInt(hypernymsTok.nextToken());
            while (hypernymsTok.hasMoreTokens()) {
                int hypernym = Integer.parseInt(hypernymsTok.nextToken());
                try {
                    digraph.addEdge(hyponym, hypernym);
                } catch (IndexOutOfBoundsException ex) {
                    throw new IllegalArgumentException(
                            "Graph description is incorrect");
                }
            }
        }
        in.close();
    }

    /**
     * Reads string description of synset and store all necessary information in
     * appropriate data structures.
     *
     * @param synsetDescription
     *            description of synset
     */
    private void parseSynsetDescription(final String synsetDescription) {
        StringTokenizer synsetDescriptionTok = new StringTokenizer(
                synsetDescription, ",");
        int synsetId = Integer.parseInt(synsetDescriptionTok.nextToken());
        String synset = synsetDescriptionTok.nextToken();
        synsets[synsetId] = synset;
        for (String noun : synset.split(" ")) {
            addNounIndex(noun, synsetId);
        }
    }

    /**
     * Connects noun with its synset id.
     *
     * @param noun
     *            connecting noun
     * @param synsetId
     *            synset id of noun
     */
    private void addNounIndex(final String noun, final int synsetId) {
        List<Integer> indexes = nounsIndexes.get(noun);
        if (indexes == null) {
            indexes = new ArrayList<Integer>();
            nounsIndexes.put(noun, indexes);
        }
        indexes.add(synsetId);
    }

    /**
     * For given noun retrieves all connected synsets.
     *
     * @param noun
     *            given noun
     * @return iterable sequence of synset indexes for specified noun
     */
    private Iterable<Integer> getNounIndexes(final String noun) {
        Iterable<Integer> nounIndexes = nounsIndexes.get(noun);
        Utils.check(nounIndexes != null, new IllegalArgumentException("Word "
                + noun + " isn't a noun"));
        return nounIndexes;
    }
}
