package src;

import com.qqwing.QQWing;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import org.jgap.Chromosome;
import org.jgap.Configuration;
import org.jgap.FitnessFunction;
import org.jgap.Gene;
import org.jgap.Genotype;
import org.jgap.impl.CrossoverOperator;
import org.jgap.impl.DefaultConfiguration;
import org.jgap.impl.IntegerGene;

public class Main {
    private static final int POPULATION_SIZE = 1000;
    private static final double MUTATION_RATE = 0.3;
    boolean solutionFound = false;


    public static void main(String[] args) throws Exception {
        FileWriter fileWriter = new FileWriter("output.txt");
        try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            Random rnd = new Random();

            // Using QQWing library Sudoku board generator
            QQWing qq = new QQWing();
            qq.generatePuzzle();
            qq.printPuzzle();
            qq.solve();



            int[] solution = qq.getSolution();
            int[] initial = qq.getPuzzle();
            HashSet<Integer> set = new HashSet<Integer>();
            int from = 0;

            bufferedWriter.write(SudokuToString(createSudoku(initial))); // Print the initial Sudoku
            System.out.println(SudokuToString(createSudoku(initial)));
            
            // Create a set of random positions to remove numbers from the initial Sudoku solution
            for (int i = 0; i < 9; i++) {
                set.clear();
                while (set.size() < 3) {
                    int n = rnd.nextInt(9);
                    if (set.contains(n) || isArrayIndex(n, Arrays.copyOfRange(initial, from, from + 9)))
                        continue;
                    set.add(n);
                }
                for (Integer hole_idx : set) {
                    solution[i * 9 + hole_idx] = 0;
                }
                from += 9;
            }

            

            int[] puzzle = solution;

            int[][] board = createSudoku(puzzle);
            int[][] initialBoard = new int[9][9];
            for (int i = 0; i < 9; i++) {
                System.arraycopy(board[i], 0, initialBoard[i], 0, 9);
            }



            int[] blankSpace = getBlankSpace(board); // Get blank spaces of our Sudoku
            int chromosomeSize = getMissingNumbers(blankSpace);

            Configuration config = new DefaultConfiguration();

            FitnessFunction sudokuFitnessFunction = new SudokuFitnessFunction(board);
            config.setFitnessFunction(sudokuFitnessFunction);

            Gene[] genes = new Gene[chromosomeSize];
            int counter = 0;


            for (int row = 0; row < 9; row++) {
                int[] sequence = generateMissingNumbers(board, row);

                // Check for duplicates in the sequence
                for (int i = 0; i < sequence.length; i++) {
                    for (int j = i + 1; j < sequence.length; j++) {
                        if (sequence[i] == sequence[j]) {
                            // Found a duplicate, swap the number with a non-duplicate number
                            for (int k = j + 1; k < sequence.length; k++) {
                                if (sequence[k] != sequence[i]) {
                                    int temp = sequence[j];
                                    sequence[j] = sequence[k];
                                    sequence[k] = temp;
                                    break;
                                }
                            }
                        }
                    }
                }

                // Assign the numbers from the sequence to the genes
                int sequenceIndex = 0;
                for (int column = 0; column < 9; column++) {
                    if (board[row][column] == 0) {
                        int value = sequence[sequenceIndex];
                        sequenceIndex++;

                        genes[counter] = new IntegerGene(config, 1, 9);
                        genes[counter].setAllele(value);
                        counter++;
                    }
                }
            }



            // Create empty chromosomes
            Chromosome[] sudokuChromosomes = new Chromosome[2];

            sudokuChromosomes[0] = new Chromosome(config);
            sudokuChromosomes[1] = new Chromosome(config);

            Gene[] genes1 = genes.clone();
            Gene[] genes2 = genes.clone();

            sudokuChromosomes[0].setGenes(genes1);
            sudokuChromosomes[1].setGenes(genes2);

            config.setSampleChromosome(sudokuChromosomes[0]);
            config.addGeneticOperator(new CrossoverOperator(config));
            config.addGeneticOperator(new SudokuMutationOperator(config, MUTATION_RATE));
            config.setPopulationSize(POPULATION_SIZE);

            Genotype population = new Genotype(config, sudokuChromosomes);

            for (int i = 0; i < 5000; i++) {
                // Evolve the population
                population.evolve();
                var fittestChromosome = population.getFittestChromosome();
                System.out.println("Best fitness value of generation " + (i + 1) + ": " + fittestChromosome.getFitnessValue());
                bufferedWriter.write("Best fitness value of generation " + (i + 1) + ": " + fittestChromosome.getFitnessValue() + "\n");

                int[][] solvedBoard = createSudoku(puzzle);
                int currentGene = 0;

                // Fill Sudoku with the results of the solution
                for (int row = 0; row < 9; row++) {
                    for (int column = 0; column < 9; column++) {
                        if (solvedBoard[row][column] == 0) {
                            solvedBoard[row][column] = (Integer) fittestChromosome.getGene(currentGene).getAllele();
                            currentGene++;
                        }
                    }
                }

                if(fittestChromosome.getFitnessValue() == 162){
                    System.out.println("\nFound solution to Sudoku after " + (i + 1) + " generations:");
                    bufferedWriter.write("\nFound solution to Sudoku after " + (i + 1) + " generations:\n");
                    System.out.println(SudokuToString(solvedBoard));
                    bufferedWriter.write("\n" + SudokuToString(solvedBoard) + "\n");
                    bufferedWriter.close();
                    return;
                }
            

                
            }
            
            System.out.println("Impossible to find a solution after 5000 evolutions");
            bufferedWriter.write("Impossible to find a solution after 5000 evolutions");
            
            
        }
    }


    // Function to get the blank spaces of the Sudoku
    public static int[] getBlankSpace(int[][] sudoku) {
        int[] countArray = new int[9];
        for (int row = 0; row < sudoku.length; row++) {
            for (int column = 0; column < sudoku[0].length; column++) {
                if (sudoku[row][column] == 0) {
                    countArray[row]++;
                }
            }
        }
        return countArray;
    }

    // Function to count the number of empty cells in the Sudoku
    public static int getMissingNumbers(int[] blankSpace) {
        int count = 0;
        for (int row = 0; row < blankSpace.length; row++) {
            count += blankSpace[row];
        }
        return count;
    }

    // Function to convert the Sudoku to a string representation
    public static String SudokuToString(int[][] sudoku) {
        String string = "\n";
        for (int row = 0; row < sudoku.length; row++) {
            string += "| ";
            for (int column = 0; column < sudoku[0].length; column++) {
                string += sudoku[row][column];
                string += " ";

                if (column % 3 == 2) {
                    string += "| ";
                }
            }
            string += "\n";

            if (row == 5 || row == 2) {
                string += "-------------------------\n";
            }
        }
        return string;
    }

    // Function to create the board of the Sudoku
    private static int[][] createSudoku(int[] puzzle) {
        int board_length = (int) Math.sqrt(puzzle.length);
        int[][] board = new int[board_length][board_length];
        for (int i = 0; i < board_length; i++) {
            for (int j = 0; j < board_length; j++) {
                int index = i * board_length + j;
                board[i][j] = puzzle[index];
            }
        }
        return board;
    }

    // Function to check if a number exists in an array
    private static boolean isArrayIndex(int n, int[] array) {
        if (array[n] != 0) return true;
        return false;
    }

    // Function to generate missing numbers for a row of the Sudoku
    private static int[] generateMissingNumbers(int[][] board, int row) {
        HashSet<Integer> numbersInRow = new HashSet<>();
        for (int column = 0; column < 9; column++) {
            numbersInRow.add(board[row][column]);
        }

        ArrayList<Integer> missingNumbers = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            if (!numbersInRow.contains(i)) {
                missingNumbers.add(i);
            }
        }

        int[] sequence = new int[missingNumbers.size()];
        for (int i = 0; i < missingNumbers.size(); i++) {
            sequence[i] = missingNumbers.get(i);
        }

        return sequence;
}

}
 /* 
The code appears to be implementing a genetic algorithm to solve a Sudoku puzzle. Here's a summary of the main steps and functionality:

1. The code imports necessary libraries and initializes some constants for the population size and mutation rate.

2. The main method starts by setting up a FileWriter and BufferedWriter to write the output to a file.

3. A Sudoku puzzle is generated using the QQWing library. The initial puzzle and its solution are printed.

4. Some random numbers from the solution are removed to create empty cells in the puzzle. The modified puzzle is printed.

5. The code checks if the puzzle has a solution using the QQWing solver. If it has a solution, the genetic algorithm is applied to find the solution.

6. The blank spaces of the puzzle are identified, and a chromosome is created for each missing number in the puzzle.

7. The genetic algorithm is set up using the JGAP library. The fitness function is defined based on the completeness of the Sudoku solution.

8. Chromosomes are initialized with genes representing the missing numbers in the puzzle.

9. The genetic algorithm is run for a specified number of generations, evolving the population at each step.

10. If a solution with a fitness value of 162 (indicating a complete solution) is found, the solution is printed, written to the output file, and the program exits.

11. If no solution is found after 5000 generations, a message is printed indicating that it was impossible to find a solution.

12. Several helper functions are defined to support the main functionality, such as getting blank spaces, converting the Sudoku to a string representation, checking for numbers in rows, and generating missing numbers for rows.

*/