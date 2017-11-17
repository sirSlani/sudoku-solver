package sirSlani.game.sudoku;

import sirSlani.game.sudoku.structure.Column;
import sirSlani.game.sudoku.structure.Region;
import sirSlani.game.sudoku.structure.Row;

import java.util.*;

public class Sudoku {

    private int[][] elements = new int[9][9];
    private boolean[][] initialElements = new boolean[9][9];

    private static int MAX_SUM = 45;
    private static int MAX_PRODUCT = 362880;
    private int decay;

    private List<Region> regions;
    private List<Row> rows;
    private List<Column> columns;

    public Sudoku() {
        this.elements = new int[9][9];
        initSegments();
    }

    public Sudoku(boolean[][] initialElements) {
        this();
        this.initialElements = initialElements;
    }

    public Sudoku(int[][] elements) {
        this.initialElements = new boolean[9][9];
        this.elements = copy(elements);
        initSegments();
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                if (elements[i][j] != 0) {
                    initialElements[i][j] = true;
                }
            }
        }
        fillOut();
    }

    private Sudoku(int[][] elements, boolean[][] initialElements) {
        this.elements = elements;
        this.initialElements = initialElements;

        initSegments();
    }

    private void initSegments() {
        regions = new ArrayList<>();
        rows = new ArrayList<>();
        columns = new ArrayList<>();
        for (int i = 0; i < 9; ++i) {
            regions.add(new Region(elements, initialElements, i));
            rows.add(new Row(elements, initialElements, i));
            columns.add(new Column(elements, initialElements, i));
        }
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

    public int getFitness() {
        int sums = 0;
        int products = 0;
        int cards = 0;
        for (int i = 0; i < 9; i++) {
            //sums += Math.abs(MAX_SUM - sumSegment(getRow(i)));
            //sums += Math.abs(MAX_SUM - sumSegment(getColumn(i)));
            //sums += Math.abs(MAX_SUM - sumSegment(getRegion(i)));
//
            //products += Math.abs((int) Math.sqrt(MAX_PRODUCT - productSegment(getRow(i))));
            //products += Math.abs((int) Math.sqrt(MAX_PRODUCT - productSegment(getColumn(i))));
            //products += Math.abs((int) Math.sqrt(MAX_PRODUCT - productSegment(getRegion(i))));
//
            cards += 9 - rows.get(i).cardinality();
            cards += 9 - columns.get(i).cardinality();
            cards += 9 - regions.get(i).cardinality();
        }
        //return 10*sums + products + 50*cards + (int) (0.05 * decay);
        return cards;
    }

    public void debugFitness() {
        int sums = 0;
        int products = 0;
        int cards = 0;
        for (int i = 0; i < 9; i++) {
            sums += 10 * Math.abs(MAX_SUM - rows.get(i).sum());
            sums += 10 * Math.abs(MAX_SUM - columns.get(i).sum());
            sums += 10 * Math.abs(MAX_SUM - regions.get(i).sum());

            products += Math.abs((int) Math.sqrt(MAX_PRODUCT - rows.get(i).product()));
            products += Math.abs((int) Math.sqrt(MAX_PRODUCT - columns.get(i).product()));
            products += Math.abs((int) Math.sqrt(MAX_PRODUCT - regions.get(i).product()));

            cards += 9 - rows.get(i).cardinality();
            cards += 9 - columns.get(i).cardinality();
            cards += 9 - regions.get(i).cardinality();
        }
        //System.out.println(sums + " " + products + " " + cards + " " + decay);

    }

    private void fillOut() {
        for (int i = 0; i < 9; ++i) {
            regions.get(i).randomFill(true);
        }
    }

    private void clear() {
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                if (!initialElements[i][j]) {
                    elements[i][j] = 0;
                }
            }
        }
    }

    public boolean isSolved() {
        for (int i = 0; i < 9; ++i) {
            if (rows.get(i).cardinality() != 9) return false;
            if (columns.get(i).cardinality() != 9) return false;
            if (regions.get(i).cardinality() != 9) return false;
        }
        return true;
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

        boolean coinFlip = Math.random() <= 0.5;
        for (int i = 0; i < 9; ++i) {
            if (coinFlip ^ (i % 2 == 0)) {
                newSudoku.regions.get(i).fill(regions.get(i));
            } else {
                newSudoku.regions.get(i).fill(other.regions.get(i));
            }
        }

        return newSudoku;
    }

    public Sudoku mutate(double probability, int rateOfMutation) {
        Sudoku newSudoku = new Sudoku(copy(this.elements), this.initialElements);
        for (int i = 0; i < rateOfMutation; ++i) {
            boolean mutate = Math.random() <= probability;
            if (mutate) {
                int region = (int) (Math.random() * 8);
                int first = (int) (Math.random() * 8);
                int second = (int) (Math.random() * 8);

                regions.get(region).swapElements(first, second);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Sudoku sudoku = (Sudoku) o;

        return Arrays.deepEquals(elements, sudoku.elements);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(elements);
    }

    public void addDecay() {
        this.decay++;
    }

    public void addDecay(Sudoku other) {
        this.decay += (other.decay > 0) ? other.decay : 1;
    }

    public int[] getRow(int i) {
        return rows.get(i).flatten();
    }

    public int[] getColumn(int i) {
        return columns.get(i).flatten();
    }

    public int[][] getRegion(int i) {
        return regions.get(i).getRegion();
    }
}
