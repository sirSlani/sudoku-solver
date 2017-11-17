package sirSlani.game.genetic;

import sirSlani.game.Sudoku;

import java.util.*;

public class Population {

    private List<Sudoku> sudokus;
    private int[][] unsolved;

    public Population(int populationSize, int[][] unsolved, boolean init) {
        this(populationSize, unsolved, new ArrayList<>(), init);
    }

    private Population(int populationSize, int[][] unsolved, List<Sudoku> sudokus, boolean init) {
        this.unsolved = unsolved;
        this.sudokus = sudokus;
        if (init) {
            for (int i = 0; i < populationSize; ++i) {
                this.sudokus.add(new Sudoku(unsolved));
            }
        }
        sort();
    }

    public Sudoku getFittest() {
        return sudokus.get(0);
    }

    public Sudoku getSudoku(int i) {
        return sudokus.get(i);
    }

    public void addSudoku(Sudoku s) {
        sudokus.add(s);
        sort();
    }

    private void sort() {
        sudokus.sort(new Comparator<Sudoku>() {
            @Override
            public int compare(Sudoku o1, Sudoku o2) {
                int fit1 = o1.getFitness();
                int fit2 = o2.getFitness();

                if (fit1 < fit2) return -1;
                if (fit1 > fit2) return 1;
                return 0;
            }
        });
    }

    public int getSize() {
        return sudokus.size();
    }

    public int[][] getUnsolved() {
        return unsolved;
    }

    public Sudoku tournamentSelect(int size) {
        Population tournament = new Population(size, unsolved, false);
        for (int i = 0; i < size; ++i) {
            int random = (int) (getSize() * Math.random());
            tournament.addSudoku(this.getSudoku(random));
            //tournament.addSudoku(rouletteSelection());
        }

        return tournament.getFittest();
    }

    private Sudoku rouletteSelection() {
        int sum = 0;
        for (Sudoku sudoku : sudokus) {
            sum += sudoku.getFitness();
        }
        int random = (int) (sum * Math.random());

        for (Sudoku sudoku : sudokus) {
            random -= sudoku.getFitness();
            if (random <= 0) return sudoku;
        }

        return sudokus.get(sudokus.size()-1);
    }

    public void addDecay(Population other) {
        for (Sudoku sudokuT : sudokus) {
            for (Sudoku sudokuO : other.sudokus) {
                if (sudokuO.equals(sudokuT)) {
                    sudokuT.addDecay(sudokuO);
                }
            }
        }
    }
}
