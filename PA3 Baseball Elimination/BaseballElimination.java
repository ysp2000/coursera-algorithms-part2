import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BaseballElimination {

    private final int numberOfTeams;
    private final String[] teams;
    private final Map<String, Integer> teamIndexes;
    private final int[] wins;
    private final int[] losses;
    private final int[] remaining;
    private final int[][] against;
    private final Map<String, List<String>> cache;

    public BaseballElimination(String filename) {
        final SimpleReader in = new SimpleReader(new In(filename));
        numberOfTeams = in.nextInt();
        teams = new String[numberOfTeams];
        teamIndexes = new HashMap<String, Integer>(numberOfTeams);
        wins = new int[numberOfTeams];
        losses = new int[numberOfTeams];
        remaining = new int[numberOfTeams];
        against = new int[numberOfTeams][numberOfTeams];
        cache = new HashMap<String, List<String>>(numberOfTeams);
        for (int i = 0; i < numberOfTeams; i++) {
            teams[i] = in.nextToken();
            teamIndexes.put(teams[i], i);
            wins[i] = in.nextInt();
            losses[i] = in.nextInt();
            remaining[i] = in.nextInt();
            for (int j = 0; j < numberOfTeams; j++) {
                against[i][j] = in.nextInt();
            }
        }
        in.close();
    }

    public int numberOfTeams() {
        return numberOfTeams;
    }

    public Iterable<String> teams() {
        return new LinkedList<String>(teamIndexes.keySet());
    }

    public int wins(String team) {
        return wins[getTeamIndex(team)];
    }

    public int losses(String team) {
        return losses[getTeamIndex(team)];
    }

    public int remaining(String team) {
        return remaining[getTeamIndex(team)];
    }

    public int against(String team1, String team2) {
        return against[getTeamIndex(team1)][getTeamIndex(team2)];
    }

    public boolean isEliminated(String team) {
        return !getResult(team).isEmpty();
    }

    public Iterable<String> certificateOfElimination(String team) {
        final List<String> certificateOfElimination = getResult(team);
        if (certificateOfElimination.isEmpty()) {
            return null;
        }
        return certificateOfElimination;
    }

    private List<String> getResult(String team) {
        final int teamIndex = getTeamIndex(team);
        List<String> result = cache.get(team);
        if (result == null) {
            result = runEliminationAnalysis(teamIndex);
            cache.put(team, result);
        }
        return result;
    }

    private List<String> runEliminationAnalysis(int teamIndex) {
        final List<String> certificateOfElimination = getUnreachableTeam(teamIndex);
        if (!certificateOfElimination.isEmpty()) {
            return certificateOfElimination;
        }
        final FlowNetwork flowNetwork = getFlowNetwork(teamIndex);
        final int s = flowNetwork.V() - 2;
        final int t = s + 1;
        final FordFulkerson fordFulkerson = new FordFulkerson(flowNetwork, s, t);
        for (int i = 0; i < numberOfTeams; i++) {
            if (fordFulkerson.inCut(i)) {
                certificateOfElimination.add(teams[i]);
            }
        }
        return certificateOfElimination;
    }

    private List<String> getUnreachableTeam(int teamIndex) {
        final List<String> certificateOfElimination = new LinkedList<String>();
        final int maxWins = wins[teamIndex] + remaining[teamIndex];
        for (int i = 0; i < numberOfTeams; i++) {
            if (maxWins < wins[i]) {
                certificateOfElimination.add(teams[i]);
                break;
            }
        }
        return certificateOfElimination;
    }

    private FlowNetwork getFlowNetwork(int teamIndex) {
        int vNum = numberOfTeams + 2;
        for (int i = 0; i < numberOfTeams; i++) {
            if (i == teamIndex) {
                continue;
            }
            for (int j = i + 1; j < numberOfTeams; j++) {
                if (j == teamIndex) {
                    continue;
                }
                if (against[i][j] != 0) {
                    vNum++;
                }
            }
        }
        final FlowNetwork flowNetwork = new FlowNetwork(vNum);
        final int s = vNum - 2;
        final int t = s + 1;
        final int maxWins = wins[teamIndex] + remaining[teamIndex];
        int pnt = numberOfTeams;
        for (int i = 0; i < numberOfTeams; i++) {
            if (i == teamIndex) {
                continue;
            }
            for (int j = i + 1; j < numberOfTeams; j++) {
                if (j == teamIndex) {
                    continue;
                }
                if (against[i][j] != 0) {
                    flowNetwork.addEdge(new FlowEdge(s, pnt, against[i][j]));
                    flowNetwork.addEdge(new FlowEdge(pnt, i,
                            Double.POSITIVE_INFINITY));
                    flowNetwork.addEdge(new FlowEdge(pnt, j,
                            Double.POSITIVE_INFINITY));
                    pnt++;
                }
            }
        }
        for (int i = 0; i < numberOfTeams; i++) {
            if (i != teamIndex) {
                flowNetwork.addEdge(new FlowEdge(i, t, maxWins - wins[i]));
            }
        }
        return flowNetwork;
    }

    private int getTeamIndex(final String team) {
        final Integer teamIndex = teamIndexes.get(team);
        if (teamIndex == null) {
            throw new IllegalArgumentException();
        }
        return teamIndex.intValue();
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team))
                    StdOut.print(t + " ");
                StdOut.println("}");
            } else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
