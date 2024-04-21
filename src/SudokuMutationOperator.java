package src;

import org.jgap.Configuration;
import org.jgap.Gene;
import org.jgap.IChromosome;
import org.jgap.impl.MutationOperator;

import java.util.List;
import java.util.Random;

public class SudokuMutationOperator extends MutationOperator {

    private static final long serialVersionUID = 1L;
    private double mutationRate;

    public SudokuMutationOperator(Configuration a_config, double mutationRate) throws Exception {
        super(a_config);
        this.mutationRate = mutationRate;
    }

    /**
     * Mutation operator for Sudoku chromosomes. Enhances the mutation operator
     * to perform a variety of mutations, such as swapping values between genes
     * in different blocks, block inversions, or value transpositions.
     */
    public void operate(IChromosome a_chromosome, List<IChromosome> a_offspring, Random a_random) {
        int blockSize = 3;

        // Check mutation rate
        if (a_random.nextDouble() > mutationRate) {
            return; // No mutation
        }

        // Perform different mutations randomly
        int mutationType = a_random.nextInt(3);
        switch (mutationType) {
            case 0:
                // Swap values between two genes in different blocks
                swapValuesBetweenBlocks(a_chromosome, a_offspring, a_random, blockSize);
                break;
            case 1:
                // Invert values within a block
                invertBlockValues(a_chromosome, a_offspring, a_random, blockSize);
                break;
            case 2:
                // Transpose values between two blocks
                transposeBlockValues(a_chromosome, a_offspring, a_random, blockSize);
                break;
            default:
                // No mutation
                break;
        }
    }

    private void swapValuesBetweenBlocks(IChromosome a_chromosome, List<IChromosome> a_offspring, Random a_random, int blockSize) {
        int block1 = a_random.nextInt(3);
        int block2 = a_random.nextInt(3);
        while (block1 == block2) {
            block2 = a_random.nextInt(3);
        }

        int rowOffset1 = block1 * blockSize;
        int colOffset1 = block1 * blockSize;
        int rowOffset2 = block2 * blockSize;
        int colOffset2 = block2 * blockSize;

        IChromosome mutatedChromosome = (IChromosome) a_chromosome.clone();

        for (int row = 0; row < blockSize; row++) {
            for (int col = 0; col < blockSize; col++) {
                int geneIndex1 = (rowOffset1 + row) * 9 + colOffset1 + col;
                int geneIndex2 = (rowOffset2 + row) * 9 + colOffset2 + col;

                Gene gene1 = a_chromosome.getGene(geneIndex1);
                Gene gene2 = a_chromosome.getGene(geneIndex2);
                mutatedChromosome.getGene(geneIndex1).setAllele(gene2.getAllele());
                mutatedChromosome.getGene(geneIndex2).setAllele(gene1.getAllele());
            }
        }

        a_offspring.add(mutatedChromosome);
    }

    private void invertBlockValues(IChromosome a_chromosome, List<IChromosome> a_offspring, Random a_random, int blockSize) {
        int block = a_random.nextInt(3);
        int rowOffset = block * blockSize;
        int colOffset = block * blockSize;
    
        IChromosome mutatedChromosome = (IChromosome) a_chromosome.clone();
    
        for (int row = 0; row < blockSize; row++) {
            for (int col = 0; col < blockSize / 2; col++) {
                int geneIndex1 = (rowOffset + row) * 9 + colOffset + col;
                int geneIndex2 = (rowOffset + row) * 9 + colOffset + blockSize - 1 - col;
    
                Gene gene1 = a_chromosome.getGene(geneIndex1);
                Gene gene2 = a_chromosome.getGene(geneIndex2);
                mutatedChromosome.getGene(geneIndex1).setAllele(gene2.getAllele());
                mutatedChromosome.getGene(geneIndex2).setAllele(gene1.getAllele());
            }
        }
    
        a_offspring.add(mutatedChromosome);
    }


    private void transposeBlockValues(IChromosome a_chromosome, List<IChromosome> a_offspring, Random a_random, int blockSize) {
        int block = a_random.nextInt(3);
        int rowOffset = block * blockSize;
        int colOffset = block * blockSize;
    
        IChromosome mutatedChromosome = (IChromosome) a_chromosome.clone();
    
        for (int row = 0; row < blockSize; row++) {
            for (int col = 0; col < blockSize; col++) {
                int geneIndex1 = (rowOffset + row) * 9 + colOffset + col;
                int geneIndex2 = (rowOffset + col) * 9 + colOffset + row;
    
                Gene gene1 = a_chromosome.getGene(geneIndex1);
                Gene gene2 = a_chromosome.getGene(geneIndex2);
                mutatedChromosome.getGene(geneIndex1).setAllele(gene2.getAllele());
                mutatedChromosome.getGene(geneIndex2).setAllele(gene1.getAllele());
            }
        }
    
        a_offspring.add(mutatedChromosome);
    }
    
    
}
    

