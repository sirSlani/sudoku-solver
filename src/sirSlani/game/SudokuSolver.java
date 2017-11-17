package sirSlani.game;

import sirSlani.game.genetic.GeneticAlgorithm;
import sirSlani.game.genetic.Population;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SudokuSolver {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";

    public static void main(String[] args) {
        int[][] sudoku = new int[][]{
                {0,3,0, 0,9,0, 1,4,0},
                {1,0,6, 7,2,0, 0,0,0},
                {0,7,0, 0,0,0, 0,6,8},

                {7,0,9, 6,4,1, 0,0,0},
                {0,2,0, 9,0,5, 0,7,0},
                {0,0,0, 8,7,2, 5,0,4},

                {3,8,0, 0,0,0, 0,1,0},
                {0,0,0, 0,8,4, 3,0,9},
                {0,9,1, 0,5,0, 0,8,0}
        };

        Population population = new Population(50, sudoku, true);

        int generationCount = 0;
        while (!population.getFittest().isSolved() || generationCount < 25000) {
            Population oldPop = population;
            generationCount++;
            if (generationCount %100 == 0) {
                System.out.println("Generation #" + generationCount + ": fitness = " + population.getFittest().getFitness());
                for (int i = 0; i < 50; ++i) {
                    System.out.print(population.getSudoku(i).getFitness() + " ");
                }
                System.out.println();
                Sudoku fittest = population.getFittest();
                //fittest.debugFitness();
                System.out.println(fittest);
                evaluate(fittest);
            }
            population = GeneticAlgorithm.evolve(population);
            //population.addDecay(oldPop);
        }

        System.out.println("Solution: ");
        Sudoku fittest = population.getFittest();
        for (int i = 0; i < 0; ++i) {
            for (int e : fittest.getRow(i)) {
                System.out.print(e + " ");
            }
            System.out.println();
        }

    }

    private static void evaluate(Sudoku fittest) {
        int correctRows = 0;
        int correctCols = 0;
        int correctRegs = 0;

        for (int i = 0; i < 9; ++i) {
            correctRows += evaluateSegment(fittest.getRow(i)) ? 1 : 0;
            correctCols += evaluateSegment(fittest.getColumn(i)) ? 1 : 0;
            correctRegs += evaluateSegment(fittest.getRegion(i)) ? 1 : 0;
        }

        System.out.println(correctRows + " " + correctCols + " " + correctRegs);
    }

    private static boolean evaluateSegment(int[][] seg) {
        return evaluateSegment(Arrays.stream(seg).flatMapToInt(x -> Arrays.stream(x)).toArray());
    }

    private static boolean evaluateSegment(int[] seg) {
        Set<Integer> has = new HashSet<>();
        for (int i = 0; i < 9; ++i) {
            if (has.contains(seg[i])) return false;
            has.add(seg[i]);
        }
        return true;
    }
    public String formattedSudoku(Sudoku sudoku) {
        int[][] evaluation = new int[9][9];
        for (int i = 0; i < 9; ++i) {
            boolean row, col, reg;
            row = evaluateSegment(sudoku.getRow(i));
            col = evaluateSegment(sudoku.getColumn(i));
            reg = evaluateSegment(sudoku.getRegion(i));

            if (row) {
                for (int j = 0; j < 9; ++j) {
                    evaluation[i][j]++;
                }
            }

            if (col) {
                for (int j = 0; j < 9; ++j) {
                    evaluation[j][i]++;
                }
            }

            if (reg) {
                for (int j = 0; j < 3; ++j) {
                    for (int k = 0; k < 3; ++k) {
                        evaluation[(i/3)*3 +j][(i%3)*3+k]++;
                    }
                }
            }
        }

        

        return "";
    }
}
