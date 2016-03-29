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
    private int[] changeLivingCells;
    private int[] similarityMeasure;

    private final double alpha = 0.5;
    private final double beta = 3.0;
    private final double gamma = 0.25;

    public int[][] getStatistics(int iterations) {
        int[][] stats = new int[iterations + 1][3];
        livingCells = countLiving(iterations);
        changeLivingCells = changeInLiving(iterations);
        similarityMeasure = similarityMeasure(iterations);

        for (int i = 0; i < iterations; i++) {
            stats[i][0] = livingCells[i];
            stats[i][1] = changeLivingCells[i];
            stats[i][2] = similarityMeasure[i];
        }

        return stats;
    }

    /**
     *
     *
     * @param iterationsToCalcualte
     * @return
     */
    private int[] countLiving(int iterationsToCalcualte) {
        if (iterationsToCalcualte == 0) {
            return new int[]{0};
        }

        if (gameboard == null || startPattern == null) {
            throw new NullPointerException("Gameboard or pattern is not set. Be sure to call setPattern first");
        }

        int[] countOfLiving = new int[iterationsToCalcualte];
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

    private int[] changeInLiving(int iterationsToCalcualte) {
        if (livingCells == null) {
            countLiving(iterationsToCalcualte + 1);
        }
        int[] countChangeOfLiving = new int[iterationsToCalcualte];
        setPattern(startPattern);
        for (int time = 0; time < iterationsToCalcualte - 1; time++) {
            countChangeOfLiving[time] = livingCells[time + 1] - livingCells[time];
            gameboard.nextGen();
        }
        return countChangeOfLiving;
    }

    private int[] similarityMeasure(int iterationsToCalcualte) {
        int[] similarity = new int[iterationsToCalcualte + 1];

        for (int time1 = 0; time1 < iterationsToCalcualte; time1++) {
            double thetaTime1 = getTheta(time1);

            int max = 0;
            for (int time2 = 0; time2 < iterationsToCalcualte; time2++) {
                if (time1 != time2) {
                    double thetaTime2 = getTheta(time2);
                    double measure = Math.min(thetaTime1, thetaTime2) / Math.max(thetaTime1, thetaTime2);
                    if (Math.floor(measure * 100) <= max) {
                    } else {
                        max = (int) Math.floor(measure * 100);
                        if (max == 100) {
                            time2 = iterationsToCalcualte - 1;
                        }
                    }
                }
            }
            similarity[time1] = max;
        }

        return similarity;
    }

    private double getTheta(int time) {
        double theta = alpha * livingCells[time]
                + beta * changeLivingCells[time]
                + gamma * geometricFactor(time);
        return theta;
    }

    /**
     *
     * @param time
     * @return The sum of x and y coordinats of living cells
     */
    private int geometricFactor(int time) {
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
        startPattern = Pattern;
        gameboard.clearBoard();
        gameboard.insertArray(startPattern, 2, 2);
    }
}
