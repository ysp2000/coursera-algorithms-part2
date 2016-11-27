import java.io.IOException;
import java.util.StringTokenizer;

public class SimpleReader {
    private final In in;
    private StringTokenizer st = new StringTokenizer("");

    public SimpleReader(In in) {
        this.in = in;
    }

    public String nextToken() {
        while (!st.hasMoreTokens())
            st = new StringTokenizer(in.readLine());
        return st.nextToken();
    }

    public int nextInt() {
        return Integer.parseInt(nextToken());
    }

    public long nextLong() {
        return Long.parseLong(nextToken());
    }

    public double nextDouble() {
        return Double.parseDouble(nextToken());
    }

    public boolean EOF() throws IOException {
        while (!st.hasMoreTokens()) {
            String s = in.readLine();
            if (s == null)
                return true;
            st = new StringTokenizer(s);
        }
        return false;
    }

    public void close() {
        in.close();
    }
}