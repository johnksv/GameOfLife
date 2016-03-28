package gol.s305089.model;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
import java.util.Arrays;

/**
 * @author s305089
 */
public class Stats {

    private Board gameboard;
    private byte[][] startPattern;
    private int[] livingCells;

    public int[][] getStatistics(int iterations) {
        int[][] stats = new int[iterations + 1][3];
        livingCells = countLiving(iterations);

        for (int i = 0; i < iterations; i++) {
            stats[i][0] = livingCells[i];
            stats[i][1] = changeInLiving(i);
            stats[i][2] = 0;
        }

        return stats;
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

        if (gameboard == null || startPattern == null) {
            throw new NullPointerException("Gameboard or pattern is not set. Be sure to call setPattern first");
        }

        int[] countOfLiving = new int[iterationNumber];
        for (int i = 0; i < countOfLiving.length; i++) {
            if (i == 0) {
                setPattern(startPattern);
            }

            int livingThisGen = 0;
            for (byte[] row : (byte[][]) gameboard.getGameBoard()) {
                for (byte value : row) {
                    if (value == 64) {
                        livingThisGen++;
                    }
                }
            }
            countOfLiving[i] = livingThisGen;
            gameboard.nextGen();
        }
        livingCells = countOfLiving;
        return countOfLiving;
    }

    public int changeInLiving(int time) {
        if (livingCells == null) {
            countLiving(time + 1);
        }
        if (time == 0) {
            return livingCells[time];
        }

        return livingCells[time - 1] - livingCells[time];
    }

    /**
     * Constructs an new Board instance, and inserts this board
     *
     * @see gol.model.Board.Board#insertArray(byte[][], int, int)
     * @param Pattern the startPattern to set
     */
    public void setPattern(byte[][] Pattern) {
        //TODO dynaimc size of board
        //TODO Move Method to helper method?
        gameboard = new ArrayBoard(100, 100);
        this.startPattern = Pattern;
        gameboard.clearBoard();
        gameboard.insertArray(startPattern, 2, 2);
    }
}
