package src;

import org.jgap.FitnessFunction;
import org.jgap.IChromosome;
import org.jgap.Gene;

import java.util.HashSet;

public class SudokuFitnessFunction extends FitnessFunction {
    private int[][] sudoku;

    public SudokuFitnessFunction(int[][] sudoku) {
        this.sudoku = sudoku;
    }

    @Override
    protected double evaluate(IChromosome sudokuChromosome) {
        int fitness = 0;

        // Create a new Sudoku grid to combine the original Sudoku and the chromosome values
        int[][] sudokuWithChromosomeCombined = new int[9][9];

        // Fill sudokuWithChromosomeCombined with the values of sudokuChromosome
        Gene[] genes = sudokuChromosome.getGenes();
        int currentGene = 0;
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                sudokuWithChromosomeCombined[row][column] = sudoku[row][column];

                // If the cell in the original Sudoku is empty (0), replace it with the chromosome value
                if (sudoku[row][column] == 0) {
                    sudokuWithChromosomeCombined[row][column] = (Integer) genes[currentGene].getAllele();
                    currentGene++;
                }
            }
        }

        // Evaluate fitness based on the uniqueness of values in columns and blocks
        fitness += getNumberOfUniqueValuesPerColumns(sudokuWithChromosomeCombined);
        fitness += getNumberOfUniqueValuesPerBlocks(sudokuWithChromosomeCombined);
       
        return fitness;
    }

    // Calculate the number of unique values in each column of the Sudoku grid
    private int getNumberOfUniqueValuesPerColumns(int[][] sudokuWithChromosome) {
        int count = 0;
        for (int column = 0; column < 9; column++) {
            HashSet<Integer> uniqueValuesInColumn = new HashSet<>();
            for (int row = 0; row < 9; row++) {
                uniqueValuesInColumn.add(sudokuWithChromosome[row][column]);
            }
            count += uniqueValuesInColumn.size();
        }
        return count;
    }

    // Calculate the number of unique values in each block of the Sudoku grid
    private int getNumberOfUniqueValuesPerBlocks(int[][] sudokuWithChromosome) {
        int count = 0;
        for (int blockRow = 0; blockRow < 3; blockRow++) {
            for (int blockColumn = 0; blockColumn < 3; blockColumn++) {
                HashSet<Integer> uniqueValuesInBlock = new HashSet<>();
                for (int row = blockRow * 3; row < (blockRow + 1) * 3; row++) {
                    for (int column = blockColumn * 3; column < (blockColumn + 1) * 3; column++) {
                        uniqueValuesInBlock.add(sudokuWithChromosome[row][column]);
                    }
                }
                count += uniqueValuesInBlock.size();
            }
        }
        return count;
    }
}
