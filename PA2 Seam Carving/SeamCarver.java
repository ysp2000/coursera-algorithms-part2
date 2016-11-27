import java.awt.Color;
import java.util.Arrays;

public class SeamCarver {

    private static final int MAX_ENERGY = 3 * 255 * 255;
    private static final int COLOR_BITS = 8;
    private static final int COLOR_MASK = (1 << COLOR_BITS) - 1;
    private static final int MIN_IMAGE_SIZE = 1;
    private static final int INFINITY = Integer.MAX_VALUE - MAX_ENERGY;

    private final Picture originalPicture;
    private Picture currentPicture;

    private boolean transposed = false;

    private int[][] colors;

    private int[][] minEnergy; // use long[][] for large image!!!
    private int[][] tMinEnergy; // use long[][] for large image!!!
    private byte[][] prevCoord;
    private byte[][] tPrevCoord;

    public SeamCarver(Picture picture) {
        this.originalPicture = new Picture(picture);
        this.currentPicture = originalPicture;
        this.colors = getColors(picture);
        prepareDynProgArrays();
    }

    public Picture picture() {
        if (currentPicture == null) {
            currentPicture = obtainCurrentPicture();
        }
        return currentPicture;
    }

    public int width() {
        if (transposed) {
            return colors.length;
        }
        return colors[0].length;
    }

    public int height() {
        if (transposed) {
            return colors[0].length;
        }
        return colors.length;
    }

    public double energy(int x, int y) {
        if (x < 0 || x >= width()) {
            throw new IndexOutOfBoundsException("Bad x=" + x + " (width="
                    + width() + ")");
        }
        if (y < 0 || y >= height()) {
            throw new IndexOutOfBoundsException("Bad y=" + y + " (height="
                    + height() + ")");
        }
        if (transposed) {
            return getEnergyAsInt(y, x);
        }
        return getEnergyAsInt(x, y);
    }

    public int[] findHorizontalSeam() {
        if (!transposed) {
            transpose();
        }
        return getVerticalSeam();
    }

    public int[] findVerticalSeam() {
        if (transposed) {
            transpose();
        }
        return getVerticalSeam();
    }

    public void removeHorizontalSeam(int[] seam) {
        if (!transposed) {
            transpose();
        }
        removeSeam(seam);
    }

    public void removeVerticalSeam(int[] seam) {
        if (transposed) {
            transpose();
        }
        removeSeam(seam);
    }

    private void prepareDynProgArrays() {
        final int width = originalPicture.width();
        final int height = originalPicture.height();
        final int maxDim = Math.max(width, height);
        final long sizeMax = (maxDim + 1L) * (maxDim + 1L);
        final long sizeCopy = width * (height + 1L) + (width + 1L) * height;
        if (sizeMax < sizeCopy) {
            minEnergy = new int[maxDim + 1][maxDim + 1];
            tMinEnergy = minEnergy;
            prevCoord = new byte[maxDim + 1][maxDim + 1];
            tPrevCoord = prevCoord;
        } else {
            minEnergy = new int[height + 1][width];
            tMinEnergy = new int[width + 1][height];
            prevCoord = new byte[height + 1][width];
            tPrevCoord = new byte[width + 1][height];
        }
    }

    private Picture obtainCurrentPicture() {
        if (transposed) {
            transpose();
        }
        final Picture picture = new Picture(width(), height());
        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                picture.set(x, y, new Color(colors[y][x]));
            }
        }
        return picture;
    }

    private int getEnergyAsInt(int x, int y) {
        if (x == 0 || x + 1 == colors[0].length || y == 0
                || y + 1 == colors.length) {
            return MAX_ENERGY;
        }
        return delta(colors[y][x - 1], colors[y][x + 1])
                + delta(colors[y - 1][x], colors[y + 1][x]);
    }

    private int delta(final int color1, final int color2) {
        int delta = 0;
        int c1 = color1;
        int c2 = color2;
        for (int i = 0; i < 3; i++) {
            final int colDelta = (c1 & COLOR_MASK) - (c2 & COLOR_MASK);
            delta += colDelta * colDelta;
            c1 >>= COLOR_BITS;
            c2 >>= COLOR_BITS;
        }
        return delta;
    }

    private void transpose() {
        this.transposed = !transposed;
        this.colors = transposition(colors);
        swapMinEnergyArrays();
        swapPrevCooordArrays();
    }

    private void swapMinEnergyArrays() {
        final int[][] tmp = minEnergy;
        minEnergy = tMinEnergy;
        tMinEnergy = tmp;
    }

    private void swapPrevCooordArrays() {
        final byte[][] tmp = prevCoord;
        prevCoord = tPrevCoord;
        tPrevCoord = tmp;
    }

    private void invalidateCurrentPicture() {
        currentPicture = null;
    }

    private int[] getVerticalSeam() {
        final int width = colors[0].length;
        final int height = colors.length;
        Arrays.fill(minEnergy[0], 0, width, 0);
        for (int y = 1; y <= height; y++) {
            Arrays.fill(minEnergy[y], 0, width, INFINITY);
        }
        for (int y = 0; y < height; y++) {
            relaxWithChecks(0, y);
            for (int x = 1; x + 1 < width; x++) {
                final int newEnergy = minEnergy[y][x] + getEnergyAsInt(x, y);
                for (int dx = -1; dx <= +1; dx++) {
                    final int nx = x + dx;
                    if (minEnergy[y + 1][nx] > newEnergy) {
                        minEnergy[y + 1][nx] = newEnergy;
                        prevCoord[y + 1][nx] = (byte) dx;
                    }
                }
            }
            relaxWithChecks(width - 1, y);
        }
        int cx = -1;
        for (int x = 0; x < width; x++) {
            if (cx == -1 || minEnergy[height][cx] > minEnergy[height][x]) {
                cx = x;
            }
        }
        final int[] seam = new int[height];
        for (int y = height - 1; y >= 0; y--) {
            cx -= prevCoord[y + 1][cx];
            seam[y] = cx;
        }
        return seam;
    }

    private void relaxWithChecks(final int x, final int y) {
        final int newEnergy = minEnergy[y][x] + getEnergyAsInt(x, y);
        for (int dx = -1; dx <= +1; dx++) {
            final int nx = x + dx;
            if (0 <= nx && nx < colors[0].length) {
                if (minEnergy[y + 1][nx] > newEnergy) {
                    minEnergy[y + 1][nx] = newEnergy;
                    prevCoord[y + 1][nx] = (byte) dx;
                }
            }
        }
    }

    private void removeSeam(int[] seam) {
        checkImageSize();
        checkSeam(seam);
        invalidateCurrentPicture();
        final int width = colors[0].length;
        for (int y = 0; y < seam.length; y++) {
            final int x = seam[y];
            final int[] src = colors[y];
            colors[y] = Arrays.copyOf(src, width - 1);
            System.arraycopy(src, x + 1, colors[y], x, width - 1 - x);
        }
    }

    private void checkImageSize() {
        if (Math.min(width(), height()) <= MIN_IMAGE_SIZE) {
            throw new IllegalArgumentException("Image is too small!");
        }
    }

    private void checkSeam(int[] seam) {
        final int width = colors[0].length;
        final int height = colors.length;
        if (seam.length != height) {
            throw new IllegalArgumentException("Seam length is not valid");
        }
        int px = -1;
        for (int y = 0; y < height; y++) {
            final int x = seam[y];
            if (x < 0 || x >= width) {
                throw new IllegalArgumentException(
                        "Some coordinate is out of range");
            }
            if (px != -1 && Math.abs(px - x) > 1) {
                throw new IllegalArgumentException("Seam is not connected");
            }
            px = x;
        }
    }

    private static int[][] getColors(final Picture picture) {
        final int width = picture.width();
        final int height = picture.height();
        final int[][] colors = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                colors[y][x] = picture.get(x, y).getRGB();
            }
        }
        return colors;
    }

    private static int[][] transposition(int[][] matrix) {
        final int rows = matrix.length;
        final int cols = matrix[0].length;
        final int[][] transposed = new int[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                transposed[j][i] = matrix[i][j];
            }
        }
        return transposed;
    }
}
