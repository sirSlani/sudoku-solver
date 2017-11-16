package sirSlani.game;

import java.util.*;

public class Sudoku {

    private int[][] elements;
    private List<Integer> initialElements;

    private static int MAX_SUM = 45;
    private static int MAX_PRODUCT = 362880;

    public Sudoku() {
        this.elements = new int[9][9];
    }

    public Sudoku(List<Integer> initialElements) {
        this();
        this.initialElements = initialElements;
    }

    public Sudoku(int[][] elements) {
        this.initialElements = new ArrayList<>();
        this.elements = copy(elements);
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                if (elements[i][j] != 0) {
                    initialElements.add(i*9 + j);
                }
            }
        }
        fillOut();
    }

    private int[][] copy(int[][] original) {
        int[][] newar = new int[9][9];
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                newar[i][j] = original[i][j];
            }
        }
        return newar;
    }

    private Sudoku(int[][] elements, List<Integer> initialElements) {
        this.elements = elements;
        this.initialElements = initialElements;
    }

    public int[] getRow(int i) {
        return Arrays.copyOf(elements[i], 9);
    }

    public int[] getColumn(int i) {
        int[] column = new int[9];
        for (int j = 0; j < 9; ++j) {
            column[j] = elements[j][i];
        }
        return column;
    }

    public int[][] getRegion(int i, int j) {
        int[][] region = new int[3][3];
        for (int x = 0; x < 3; ++x) {
            for (int y = 0; y < 3; ++y) {
                region[x][y] = elements[i*3 + x][j*3 + y];
            }
        }
        return region;
    }

    public int[][] getRegion(int i) {
        return getRegion(i / 3, i % 3);
    }

    private int sumSegment(int[] segment) {
        return Arrays.stream(segment).sum();
    }

    private int sumSegment(int[][] segment) {
        return Arrays.stream(segment).flatMapToInt(row -> Arrays.stream(row)).sum();
    }

    private int productSegment(int[] segment) {
        int product = 1;
        for (int i = 0; i < segment.length; ++i) {
            product *= segment[i];
        }
        return product;
    }

    private int productSegment(int[][] segment) {
        return productSegment(Arrays.stream(segment).flatMapToInt(row -> Arrays.stream(row)).toArray());
    }

    private int cardinalitySegment(int[] segment) {
        boolean[] has = new boolean[9];
        for (int i = 0; i < segment.length; ++i) {
            if (segment[i] != 0) has[segment[i] - 1] = true;
        }
        int count = 0;
        for (int i = 0; i < has.length; ++i) {
            if (has[i]) count++;
        }
        return count;
    }

    private int cardinalitySegment(int[][] segment) {
        return cardinalitySegment(Arrays.stream(segment).flatMapToInt(row -> Arrays.stream(row)).toArray());
    }

    public int getFitness() {
        int sums = 0;
        int products = 0;
        int cards = 0;
        for (int i = 0; i < 9; i++) {
            sums += Math.abs(MAX_SUM - sumSegment(getRow(i)));
            sums += Math.abs(MAX_SUM - sumSegment(getColumn(i)));
            sums += Math.abs(MAX_SUM - sumSegment(getRegion(i)));

            products += Math.abs(Math.sqrt(MAX_PRODUCT - productSegment(getRow(i))));
            products += Math.abs(Math.sqrt(MAX_PRODUCT - productSegment(getColumn(i))));
            products += Math.abs(Math.sqrt(MAX_PRODUCT - productSegment(getRegion(i))));

            cards += 9 - cardinalitySegment(getRow(i));
            cards += 9 - cardinalitySegment(getColumn(i));
            cards += 9 - cardinalitySegment(getRegion(i));
        }
        return 10*sums + products + 50*cards;
    }

    public void debugFitness() {
        int sums = 0;
        int products = 0;
        int cards = 0;
        for (int i = 0; i < 9; i++) {
            sums += 10 * Math.abs(MAX_SUM - sumSegment(getRow(i)));
            sums += 10 * Math.abs(MAX_SUM - sumSegment(getColumn(i)));
            sums += 10 * Math.abs(MAX_SUM - sumSegment(getRegion(i)));

            products += Math.abs((int) Math.sqrt(MAX_PRODUCT - productSegment(getRow(i))));
            products += Math.abs((int) Math.sqrt(MAX_PRODUCT - productSegment(getColumn(i))));
            products += Math.abs((int) Math.sqrt(MAX_PRODUCT - productSegment(getRegion(i))));

            cards += 50*(9 - cardinalitySegment(getRow(i)));
            cards += 50*(9 - cardinalitySegment(getColumn(i)));
            cards += 50*(9 - cardinalitySegment(getRegion(i)));
        }
        System.out.println(sums + " " + products + " " + cards);

    }

    private void fillOut() {
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                if (elements[i][j] == 0) {
                    elements[i][j] = (int) Math.round(1 + 8 * Math.random());
                }
            }
        }
    }

    private void clear() {
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                if (!initialElements.contains(i*3 + j)) {
                    elements[i][j] = 0;
                }
            }
        }
    }

    public boolean isSolved() {
        for (int i = 0; i < 9; ++i) {
            if (cardinalitySegment(getRow(i)) != 9) return false;
            if (cardinalitySegment(getColumn(i)) != 9) return false;
            if (cardinalitySegment(getRegion(i)) != 9) return false;
        }
        return true;
    }

    private void fillRegion(int[][] segment, int i, int j) {
        for (int x = 0; x < 3; ++x) {
            for (int y = 0; y < 3; ++y) {
                int realX = i*3 + x;
                int realY = j*3 + y;

                if (elements[realX][realY] == 0) {
                    elements[realX][realY] = segment[x][y];
                }
            }
        }
    }

    private void fillRegion(int[][] segment, int i) {
        fillRegion(segment, i / 3, i % 3);
    }

    private void fillRow(int[] segment, int i) {
        for (int j = 0; j < 9; ++j) {
            if (elements[i][j] == 0) elements[i][j] = segment[j];
        }
    }

    private void fillColumn(int[] segment, int i) {
        for (int j = 0; j < 9; ++j) {
            if (elements[j][i] == 0) elements[j][i] = segment[j];
        }
    }

    private boolean isComplete() {
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                if (elements[i][j] == 0) return false;
            }
        }
        return true;
    }

    public Sudoku crossover(Sudoku other) {
        Sudoku newSudoku = new Sudoku(copy(this.elements), this.initialElements);
        newSudoku.clear();
        int n = 0;
        while (!newSudoku.isComplete() && n < 10) {
            n++;
            boolean coinFlip = Math.random() > 0.5;
            int segment = (int) (1 + 8 * Math.random());
            if (coinFlip) {
                newSudoku.fillRow(other.getRow(segment), segment);
                newSudoku.fillColumn(other.getColumn(segment), segment);
                newSudoku.fillRegion(other.getRegion(segment), segment);
            } else {
                newSudoku.fillRow(getRow(segment), segment);
                newSudoku.fillColumn(getColumn(segment), segment);
                newSudoku.fillRegion(getRegion(segment), segment);
            }
        }
        if (!newSudoku.isComplete()) {
            for (int i = 0; i < 9; ++i) {
                newSudoku.fillRow(getRow(i), i);
            }
        }

        return newSudoku;
    }

    public Sudoku mutate(double probability, int rateOfMutation) {
        Sudoku newSudoku = new Sudoku(copy(this.elements), this.initialElements);
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                boolean coinFlip = Math.random() < probability;
                if (coinFlip && !initialElements.contains(i*3 + j)) {
                    int mutated = newSudoku.elements[i][j];
                    mutated += (int) (-rateOfMutation/2 + rateOfMutation * Math.random());
                    if (mutated < 1) mutated = 1;
                    if (mutated > 9) mutated = 9;
                    newSudoku.elements[i][j] = mutated;
                }
            }
        }
        return flipDoubles(flipDoubles(newSudoku, false), true);
    }

    private Sudoku flipDoubles(Sudoku sudoku, boolean axis) {
        Sudoku newSudoku = new Sudoku(copy(sudoku.elements), sudoku.initialElements);
        for (int i = 0; i < 9; ++i) {
            Set<Integer> has = new HashSet<>();
            for (int j = 0; j < 9; ++j) {
                int x = axis ? i : j;
                int y = axis ? j : i;
                boolean coinToss = Math.random() <= 0.5;
                int flip = coinToss ? -1 : 1;
                if (has.contains(sudoku.elements[x][y])) {
                    newSudoku.elements[x][y] += flip;
                    if (newSudoku.elements[x][y] > 9) newSudoku.elements[x][y] = 8;
                    if (newSudoku.elements[x][y] < 1) newSudoku.elements[x][y] = 2;
                } else {
                    has.add(sudoku.elements[x][y]);
                }
            }
        }
        return newSudoku;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                sb.append(elements[i][j]);
                sb.append(' ');
            }
            sb.setLength(sb.length()-1);
            sb.append('\n');
        }
        sb.setLength(sb.length()-1);
        return sb.toString();
    }
}
