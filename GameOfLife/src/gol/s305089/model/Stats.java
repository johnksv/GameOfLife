package gol.s305089.model;

import gol.model.Board.Board;
import gol.model.Board.DynamicBoard;

/**
 * @author s305089
 */
public class Stats {

    private Board gameboard;
    private byte[][] startPattern;
    private int[] livingCells;
    private int[] changeLivingCells;
    private int[] similarityMeasure;
    private int[] geometricFactor;

    private final double alpha = 0.5;
    private final double beta = 3.0;
    private final double gamma = 0.25;
    private boolean checkSimilarityPrevGen;

    public int[][] getStatistics(int iterations, boolean calcChangeLiving, boolean calcSimilarity) {
        int[][] stats = new int[iterations + 1][3];

        livingCells = countLiving(iterations);
        changeLivingCells = calcChangeLiving ? changeInLiving(iterations) : null;
        if (calcSimilarity) {
            changeLivingCells = changeInLiving(iterations);
            similarityMeasure = similarityMeasure(iterations);
        } else {
            similarityMeasure = null;
        }
        for (int i = 0; i < iterations; i++) {
            stats[i][0] = livingCells[i];

            if (changeLivingCells != null) {
                stats[i][1] = changeLivingCells[i];
            }
            if (similarityMeasure != null) {
                stats[i][2] = similarityMeasure[i];
            }
        }

        return stats;
    }

    /**
     *
     *
     * @param iterationsToCalcualte
     * @return
     */
    public int[] countLiving(int iterationsToCalcualte) {
        if (iterationsToCalcualte == 0) {
            return new int[]{0};
        }

        int[] countOfLiving = new int[iterationsToCalcualte];
        for (int i = 0; i < countOfLiving.length; i++) {
            if (i == 0) {
                setPattern(startPattern);
            }

            int livingThisGen = 0;
            for (byte[] row : (byte[][]) gameboard.getBoundingBoxBoard()) {
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

    public int[] changeInLiving(int iterationsToCalcualte) {
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

    public int[] similarityMeasure(int iterationsToCalcualte) {
        int[] similarity = new int[iterationsToCalcualte + 1];
        calculateGeometricFactor(iterationsToCalcualte);

        setPattern(startPattern);

        for (int time1 = 0; time1 < iterationsToCalcualte; time1++) {
            double thetaTime1 = getTheta(time1);

            int max = 0;
            //Checks if future generations is similar
            for (int time2 = 0; time2 < iterationsToCalcualte; time2++) {
                if (!checkSimilarityPrevGen && time2 == 0) {
                    time2 = time1;
                }
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
                + gamma * geometricFactor[time];
        return theta;
    }

    /**
     *
     * @param time
     * @return The sum of x and y coordinates of living cells
     */
    private void calculateGeometricFactor(int iterationsToCalcualte) {
        geometricFactor = new int[iterationsToCalcualte + 1];
        setPattern(startPattern);

        for (int iterations = 0; iterations < iterationsToCalcualte; iterations++) {
            byte[][] boundedBoard = gameboard.getBoundingBoxBoard();
            for (int row = 0; row < boundedBoard.length; row++) {
                for (int column = 0; column < boundedBoard[row].length; column++) {
                    if (boundedBoard[row][column] == 64) {
                        //TODO Improve GeometricFactor
                        geometricFactor[iterations] += row + column;
                    }
                }
            }
            gameboard.nextGen();
        }
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
        gameboard = new DynamicBoard(10, 10);
        startPattern = Pattern;
        gameboard.clearBoard();
        gameboard.insertArray(startPattern, 1, 1);
    }

    public void setCheckSimilarityPrevGen(boolean checkSimilarityPrevGen) {
        this.checkSimilarityPrevGen = checkSimilarityPrevGen;
    }
}
