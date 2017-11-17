package sirSlani.game;

import java.util.*;

public class Sudoku {

    private int[][] elements = new int[9][9];
    private List<Integer> initialElements;

    private static int MAX_SUM = 45;
    private static int MAX_PRODUCT = 362880;
    private int decay;

    private List<Region> regions;

    {
        regions = new ArrayList<>();
        for (int i = 0; i < 9; ++i) regions.add(new Region(elements, i));
    }

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

    private Sudoku(int[][] elements, List<Integer> initialElements) {
        this.elements = elements;
        this.initialElements = initialElements;
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

    private class Region {
        private int[][] elements;
        private int x, y;

        public Region(int[][] elements, int x, int y) {
            this.elements = elements;
            this.x = x;
            this.y = y;
        }

        public Region(int[][] elements, int i) {
            this.elements = elements;
            this.x = i / 3;
            this.y = i % 3;
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

        public int getElement(int i, int j) {
            return elements[x*3 + i][y*3 + j];
        }

        public void setElement(int i, int j, int value) {
            elements[x*3 + i][y*3 + j] = value;
        }

        public void swapElements(int i1, int j1, int i2, int j2) {
            int temp = elements[x*3 + i1][y*3 + j1];
            elements[x*3 + i1][y*3 + j1] = elements[x*3 + i2][y*3 + j2];
            elements[x*3 + i2][y*3 + j2] = temp;
        }

        public void swapElements(int a, int b) {
            int i = x*3 + a/3;
            int j = y*3 + b%3;
        }
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
            //sums += Math.abs(MAX_SUM - sumSegment(getRow(i)));
            //sums += Math.abs(MAX_SUM - sumSegment(getColumn(i)));
            //sums += Math.abs(MAX_SUM - sumSegment(getRegion(i)));
//
            //products += Math.abs((int) Math.sqrt(MAX_PRODUCT - productSegment(getRow(i))));
            //products += Math.abs((int) Math.sqrt(MAX_PRODUCT - productSegment(getColumn(i))));
            //products += Math.abs((int) Math.sqrt(MAX_PRODUCT - productSegment(getRegion(i))));
//
            cards += 9 - cardinalitySegment(getRow(i));
            cards += 9 - cardinalitySegment(getColumn(i));
            cards += 9 - cardinalitySegment(getRegion(i));
        }
        //return 10*sums + products + 50*cards + (int) (0.05 * decay);
        return cards;
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
        //System.out.println(sums + " " + products + " " + cards + " " + decay);

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
        //int n = 0;
        /*
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
        }*/

        boolean coinFlip = Math.random() <= 0.5;
        for (int i = 0; i < 9; ++i) {
            if (coinFlip ^ (i % 2 == 0)) {
                newSudoku.fillRegion(this.getRegion(i), i);
            } else {
                newSudoku.fillRegion(other.getRegion(i), i);
            }
        }

        return newSudoku;
    }

    public Sudoku mutate(double probability, int rateOfMutation) {
        Sudoku newSudoku = new Sudoku(copy(this.elements), this.initialElements);
        /*for (int i = 0; i < 3; ++i) {
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
        }*/

        int num = (int) (Math.random() * rateOfMutation);

        for (int i = 0; i < num; ++i) {
            boolean coinFlip = Math.random() <= probability;
            int region = (int) (Math.random() * 8);
            int first = (int) (Math.random() * 8);
            int second = (int) (Math.random() * 8);
            int x1 = first / 3;
            int y1 = first % 3;
            int x2 = second / 3;
            int y2 = second % 3;

            if (coinFlip && !initialElements.contains(flatId(x1, y1, region)) && !initialElements.contains(flatId(x2, y2, region))) {
                x1 = coorRegion(x1, regionX(region));
                y1 = coorRegion(y1, regionY(region));
                x2 = coorRegion(x2, regionX(region));
                y2 = coorRegion(y2, regionY(region));

                int temp = elements[x1][y1];
                elements[x1][y1] = elements[x2][y2];
                elements[x2][y2] = temp;
            }
        }
        return flipDoubles(flipDoubles(flipDoubles(newSudoku, 0), 1), 2);
    }

    private int coorRegion(int coor, int regionCoor) {
        return coor + regionCoor * 3;
    }

    private int regionX(int region) {
        return region / 3;
    }

    private int regionY(int region) {
        return region % 3;
    }

    private int flatId(int x, int y) {
        return y * 9 + x;
    }

    private int flatId(int x, int y, int regionX, int regionY) {
        return flatId(coorRegion(x, regionX), coorRegion(y, regionY));
    }

    private int flatId(int x, int y, int region) {
        return flatId(x, y, regionX(region), regionY(region));
    }

    private Sudoku flipDoubles(Sudoku sudoku, int axis) {
        Sudoku newSudoku = new Sudoku(copy(sudoku.elements), sudoku.initialElements);
        for (int i = 0; i < 9; ++i) {
            Set<Integer> has = new HashSet<>();
            for (int j = 0; j < 9; ++j) {
                int x, y;
                switch (axis) {
                    case 0: {
                        x = i;
                        y = j;
                    } break;
                    case 1: {
                        x = j;
                        y = i;
                    } break;
                    case 2: {
                        x = coorRegion(regionX(j), regionX(i));
                        y = coorRegion(regionY(j), regionY(j));
                    } break;
                    default: {
                        x = 0; y = 0;
                    }
                }

                boolean coinFlip = Math.random() > 0.5;
                int flip = coinFlip ? -1 : 1;
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
}
