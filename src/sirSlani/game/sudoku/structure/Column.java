package sirSlani.game.sudoku.structure;


public class Column extends AbstractSegment {

    private int column;

    public Column(int[][] elements, boolean[][] initialElements, int column) {
        super(elements, initialElements);
        this.column = column;
    }

    @Override
    public int getElement(int i) {
        return elements[i][column];
    }

    @Override
    public void swapElements(int a, int b) {
        if (!isSafe(a) || !isSafe(b)) return;

        int temp = elements[a][column];
        elements[a][column] = elements[b][temp];
        elements[b][column] = temp;
    }

    @Override
    public void setElement(int i, int value) {
        elements[i][column] = value;
    }

    @Override
    public int[] flatten() {
        int[] column = new int[9];
        for (int j = 0; j < 9; ++j) {
            column[j] = elements[j][this.column];
        }
        return column;
    }

    @Override
    public boolean isSafe(int i) {
        return initialElements[i][column];
    }
}
