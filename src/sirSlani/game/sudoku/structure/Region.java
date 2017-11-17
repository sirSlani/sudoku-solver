package sirSlani.game.sudoku.structure;

import java.util.Arrays;

public class Region extends AbstractSegment {
    private int x, y;

    public Region(int[][] elements, boolean[][] initialElements, int x, int y) {
        super(elements, initialElements);
        this.x = x;
        this.y = y;
    }

    public Region(int[][] elements, boolean[][] initialElements, int i) {
        this(elements, initialElements, i / 3, i % 3);
    }

    public int[][] getRegion() {
        int[][] region = new int[3][3];
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                region[i][j] = elements[x*3 + i][y*3 + j];
            }
        }
        return region;
    }

    public int[] flatten() {
        return Arrays.stream(getRegion()).flatMapToInt(row -> Arrays.stream(row)).toArray();
    }

    @Override
    public boolean isSafe(int i) {
        int rX = i / 3;
        int rY = i % 3;
        int rawX = x * 3 + rX;
        int rawY = y * 3 + rY;
        return initialElements[rawX][rawY];
    }

    @Override
    public int getElement(int i) {
        return getElement(i / 3, i % 3);
    }

    public int getElement(int i, int j) {
        return elements[x*3 + i][y*3 + j];
    }

    @Override
    public void setElement(int i, int value) {
        setElement(i / 3, i % 3, value);
    }

    public void setElement(int i, int j, int value) {
        elements[x*3 + i][y*3 + j] = value;
    }

    private void swapElements(int i1, int j1, int i2, int j2) {

        int temp = elements[x*3 + i1][y*3 + j1];
        elements[x*3 + i1][y*3 + j1] = elements[x*3 + i2][y*3 + j2];
        elements[x*3 + i2][y*3 + j2] = temp;
    }

    @Override
    public void swapElements(int a, int b) {
        if (!isSafe(a) || !isSafe(b)) return;
        int ax = a / 3;
        int ay = a % 3;
        int bx = b / 3;
        int by = b % 3;

        swapElements(ax, ay, bx, by);
    }

}
