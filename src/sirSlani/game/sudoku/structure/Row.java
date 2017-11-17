package sirSlani.game.sudoku.structure;

public class Row extends AbstractSegment {

    private int row;

    public Row(int[][] elements, boolean[][] initialElements, int row) {
        super(elements, initialElements);
        this.row = row;
    }

    @Override
    public int getElement(int i) {
        return elements[row][i];
    }

    @Override
    public void swapElements(int a, int b) {
        if (!isSafe(a) || !isSafe(b)) return;

        int temp = elements[row][a];
        elements[row][a] = elements[row][b];
        elements[row][b] = temp;
    }

    @Override
    public void setElement(int i, int value) {
        elements[row][i] = value;
    }

    @Override
    public int[] flatten() {
        return elements[row];
    }

    @Override
    public boolean isSafe(int i) {
        return initialElements[row][i];
    }
}
