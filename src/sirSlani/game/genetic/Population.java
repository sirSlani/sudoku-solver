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
        Collections.sort(sudokus);
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
        }

        return tournament.getFittest();
    }
}
