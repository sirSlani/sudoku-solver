package sirSlani.game.genetic;

import sirSlani.game.sudoku.Sudoku;

public class GeneticAlgorithm {

    public static Population evolve(Population population, double mutationProbability, int mutationRate, int tournamentSize, int elitism) {
        Population newPopulation = new Population(population.getSize(), population.getUnsolved(),false);

        for (int i = 0; i < elitism; ++i) {
            newPopulation.addSudoku(population.getSudoku(i));
        }

        for (int i = elitism; i < population.getSize(); ++i) {
            Sudoku first = population.tournamentSelect(tournamentSize);
            Sudoku second = population.tournamentSelect(tournamentSize);
            Sudoku child = first.crossover(second);
            Sudoku mutant = child.mutate(mutationProbability, mutationRate);
            newPopulation.addSudoku(mutant);
        }

        return newPopulation;
    }

    public static Population evolve(Population population) {
        return evolve(population, 0.60, 45, 5, 1);
    }
}
