package gol.s305089.model;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;

/**
 * @author s305089
 */
public class Stats {

    private Board gameboard;
    private byte[][] pattern;

    public int[][] getStatistics(int iterations) {
        int[][] stats = new int[iterations+1][3];
                
        for (int itNumber = 0; itNumber <= iterations; itNumber++) {
            
            stats[itNumber][0] = countLiving(itNumber);
            stats[itNumber][1] = changeInLiving(itNumber);
                // stats[itNumber][0] = similarityMeasure();

            gameboard.nextGen();
        }

        return stats;
    }

    /**
     * Constructs an new Board instance, and inserts this board
     *
     * @see gol.model.Board.Board#insertArray(byte[][], int, int)
     * @param Pattern the pattern to set
     */
    public void setPattern(byte[][] Pattern) {
        //TODO dynaimc size of board
        gameboard = new ArrayBoard(10, 10);
        this.pattern = Pattern;
        gameboard.insertArray(pattern, 2, 2);
    }

    public int countLiving(int iterationNumber) {
        int countOfLiving = 0;
        for (byte[] row : pattern) {
            for (byte value : row) {
                if (value == 1) {
                    countOfLiving++;
                }
            }
        }
        return countOfLiving;
    }

    public int changeInLiving(int time) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
