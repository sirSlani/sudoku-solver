package sirSlani.game.sudoku.structure;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public abstract class AbstractSegment implements ISegment {

    protected int[][] elements;
    protected boolean[][] initialElements;

    public AbstractSegment(int[][] elements, boolean[][] initialElements) {
        this.elements = elements;
        this.initialElements = initialElements;
    }

    @Override
    public int sum() {
        return Arrays.stream(flatten()).sum();
    }

    @Override
    public int product() {
        int product = 1;
        for (int element : flatten()) {
            product *= element;
        }
        return product;
    }

    @Override
    public int cardinality() {
        boolean[] has = new boolean[9];
        for (int element : flatten()) {
            if (element != 0) has[element - 1] = true;
        }
        int count = 0;
        for (int i = 0; i < has.length; ++i) {
            if (has[i]) count++;
        }
        return count;
    }

    @Override
    public void fill(ISegment other) {
        for (int i = 0; i < 9; ++i) {
            if (isSafe(i)) {
                setElement(i, other.getElement(i));
            }
        }
    }

    @Override
    public void randomFill(boolean clear) {
        int[] vals = flatten();

        if (clear) {
            int[] newVals = new int[9];
            Set<Integer> nohas = new HashSet<>();
            for (int i = 0; i < 9; ++i) {
                if (!isSafe(i)) {
                    nohas.add(i+1);
                }
            }

            Iterator<Integer> it = nohas.iterator();

            for (int i = 0; i < 9; ++i) {
                if (!isSafe(i)) {
                    newVals[i] = it.next();
                } else {
                    newVals[i] = vals[i];
                }
            }

            vals = newVals;
        }
        int[] newVals = permute(vals, 25);

        for (int i = 0; i < 9; ++i) {
            setElement(i, newVals[i]);
        }
    }

    private int[] permute(int[] vals, int iters) {
        int[] newVals = Arrays.copyOf(vals, 9);
        for (int i = 0; i < iters; ++i) {
            int first = (int) (8 * Math.random());
            int second = (int) (8 * Math.random());

            if (first == second) continue;
            if (!isSafe(first) && !isSafe(second)) {
                int temp = newVals[first];
                newVals[first] = newVals[second];
                newVals[second] = temp;
            }
        }
        return newVals;
    }
}
