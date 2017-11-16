package sirSlani.game;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Sudoku implements Comparable<Sudoku> {

    private int[][] elements;
    private int[][] cleanElements;
    private List<Integer> initialElements;

    private static int MAX_SUM = 45;
    private static int MAX_PRODUCT = 362880;

    private int fitness = -1;

    public Sudoku() {
        this.elements = new int[9][9];
    }

    public Sudoku(List<Integer> initialElements) {
        this();
        this.initialElements = initialElements;
    }

    public Sudoku(int[][] elements) {
        this.elements = Arrays.copyOf(elements, elements.length);
        this.cleanElements = elements;
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                if (elements[i][j] != 0) {
                    initialElements.add(i*9 + j);
                }
            }
        }
        fillOut();
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
        for (int j = 0; i < 9; ++i) {
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
        if (this.fitness > -1) return this.fitness;
        int fitness = 0;
        for (int i = 0; i < 9; i++) {
            fitness += 10 * (MAX_SUM - sumSegment(getRow(i)));
            fitness += 10 * (MAX_SUM - sumSegment(getColumn(i)));
            fitness += 10 * (MAX_SUM - sumSegment(getRegion(i)));

            fitness += Math.sqrt(MAX_PRODUCT - productSegment(getRow(i)));
            fitness += Math.sqrt(MAX_PRODUCT - productSegment(getColumn(i)));
            fitness += Math.sqrt(MAX_PRODUCT - productSegment(getRegion(i)));

            fitness += 50 * (9 - cardinalitySegment(getRow(i)));
            fitness += 50 * (9 - cardinalitySegment(getColumn(i)));
            fitness += 50 * (9 - cardinalitySegment(getRegion(i)));
        }

        return fitness;
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

    private boolean isSolved() {
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
        Sudoku newSudoku = new Sudoku(this.elements, this.initialElements);
        newSudoku.clear();

        while (!newSudoku.isComplete()) {
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

        return newSudoku;
    }

    public Sudoku mutate(double probability, int rateOfMutation) {
        Sudoku newSudoku = new Sudoku(this.elements, this.initialElements);
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
        return newSudoku;
    }

    @Override
    public int compareTo(Sudoku o) {
        return Integer.compare(this.getFitness(), o.getFitness());
    }
}
