package gol.s305089.model;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;

/**
 * @author s305089
 */
public class Stats {

    private Board gameboard;
    private byte[][] pattern;
    private int[] livingCells;

    public int[][] getStatistics(int iterations) {
        int[][] stats = new int[iterations + 1][3];

        livingCells = countLiving(iterations);
        for (int i = 0; i < iterations; i++) {
            stats[i][0] = livingCells[i];
            stats[i][1] = changeInLiving(i);
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
        //TODO Move Method to helper method?
        gameboard = new ArrayBoard(10, 10);
        this.pattern = Pattern;
        gameboard.clearBoard();
        gameboard.insertArray(pattern, 2, 2);
    }
    
    /**
     *
     * If number of iteration is 0, an empty array would be returend, as
     * expected
     *
     * @param iterationNumber
     * @return
     */
    public int[] countLiving(int iterationNumber) {
        if (iterationNumber == 0) {
            return new int[]{0};
        }

        int[] countOfLiving = new int[iterationNumber];
        for (int i = 0; i < countOfLiving.length; i++) {
            int livingThisGen = 0;
            for (byte[] row : pattern) {
                for (byte value : row) {
                    if (value == 1) {
                        livingThisGen++;
                    }
                }
            }
            countOfLiving[i] = livingThisGen;
            gameboard.nextGen();
        }
        return countOfLiving;
    }

    public int changeInLiving(int time) {
        if(livingCells==null){
            countLiving(time+1);
        }
        return livingCells[time+1]-livingCells[time];
    }

}
