package sirSlani.game.sudoku.structure;

public interface ISegment {

    int getElement(int i);

    void swapElements(int a, int b);

    void setElement(int i, int value);

    int sum();

    int product();

    int cardinality();

    int[] flatten();

    void fill(ISegment other);

    void randomFill(boolean clear);

    boolean isSafe(int i);
}
