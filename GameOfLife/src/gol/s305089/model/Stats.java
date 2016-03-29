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

    private final double alpha = 0.5;
    private final double beta = 3.0;
    private final double gamma = 0.25;

    public int[][] getStatistics(int iterations) {
        int[][] stats = new int[iterations + 1][3];
        livingCells = countLiving(iterations);

        for (int i = 0; i < iterations; i++) {
            stats[i][0] = livingCells[i];
            stats[i][1] = changeInLiving(i);
            stats[i][2] = 0;
        }
        
        geometricFactor(iterations);
        
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
        if (time < livingCells.length - 1) {
            return livingCells[time + 1] - livingCells[time];
        }
        return 0;
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
        gameboard = new ArrayBoard(1800, 1800);
        this.startPattern = Pattern;
        gameboard.clearBoard();
        gameboard.insertArray(startPattern, 2, 2);
    }

    double similarityMeasure(int time) {
        double theta = alpha * livingCells[time]
                + beta * changeInLiving(time)
                + gamma * geometricFactor(time);

        return theta;
    }

    /**
     *
     * @param time
     * @return The sum of x and y coordinats of living cells
     */
    public int geometricFactor(int time) {
        int result = 0;

        setPattern(startPattern);
        for (int i = 0; i < time; i++) {
            gameboard.nextGen();
        }
        
        byte[][] boundedBoard = gameboard.getBoundingBoxBoard();
        for (int i = 0; i < boundedBoard.length; i++) {
            for (int j = 0; j < boundedBoard[i].length; j++) {
                if (boundedBoard[i][j] == 64) {
                    result += i + j;
                }
            }
        }
        return result;
    }

}
