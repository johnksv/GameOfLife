package gol.s305089.model;

import gol.model.Board.Board;
import gol.model.Board.DynamicBoard;

/**
 * @author s305089
 */
public class Stats {

    private Board gameboard;
    private byte[][] originalPattern;
    private int[] livingCells;
    private int[] changeLivingCells;
    private int[][] similarityMeasure;
    private int[] geometricFactor;

    private final double alpha = 0.5;
    private final double beta = 3.0;
    private final double gamma = 0.25;
    private final double alphaCustom = 0.5;
    private final double betaCustom = 3.0;
    private final double gammaCustom = 0.25;
    private boolean shouldUseCustom = false;
    private boolean checkSimilarityPrevGen;

    public int[][] getStatistics(int iterations, boolean calcChangeLiving, boolean calcSimilarity) {
        int[][] stats = new int[iterations + 1][4];

        livingCells = getCountLiving(iterations);
        changeLivingCells = calcChangeLiving ? getChangeInLiving(iterations) : null;
        if (calcSimilarity) {
            changeLivingCells = getChangeInLiving(iterations);
            similarityMeasure = getSimilarityMeasure(iterations);
        } else {
            similarityMeasure = null;
        }
        for (int i = 0; i < iterations; i++) {
            stats[i][0] = livingCells[i];

            if (changeLivingCells != null) {
                stats[i][1] = changeLivingCells[i];
            }
            if (similarityMeasure != null) {
                stats[i][2] = similarityMeasure[i][0];
                stats[i][3] = similarityMeasure[i][1];
            }
        }

        return stats;
    }

    /**
     *
     * @param iterationsToCalcualte
     * @return
     */
    public int[] getCountLiving(int iterationsToCalcualte) {
        if (iterationsToCalcualte == 0) {
            return new int[]{0};
        }

        int[] countOfLiving = new int[iterationsToCalcualte];
        for (int i = 0; i < countOfLiving.length; i++) {
            if (i == 0) {
                setPattern(originalPattern);
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

    public int[] getChangeInLiving(int iterationsToCalcualte) {
        if (livingCells == null) {
            getCountLiving(iterationsToCalcualte + 1);
        }
        int[] countChangeOfLiving = new int[iterationsToCalcualte];
        setPattern(originalPattern);
        for (int time = 0; time < iterationsToCalcualte - 1; time++) {
            countChangeOfLiving[time] = livingCells[time + 1] - livingCells[time];
            gameboard.nextGen();
        }
        changeLivingCells = countChangeOfLiving;
        return countChangeOfLiving;
    }

    /**
     *
     * @param iterationsToCalcualte
     * @return First index: similarity measure, second index: iteration number
     * of closest match.
     */
    public int[][] getSimilarityMeasure(int iterationsToCalcualte) {
        int[][] similarity = new int[iterationsToCalcualte + 1][2];
        if (changeLivingCells == null) {
            getChangeInLiving(iterationsToCalcualte);
        }
        calculateGeometricFactor(iterationsToCalcualte);

        setPattern(originalPattern);

        for (int time1 = 0; time1 < iterationsToCalcualte; time1++) {
            double thetaTime1 = Math.abs(getTheta(time1));

            int max = 0;
            int itClosestMatch = -1;
            //Checks if future or prev generations is similar
            for (int time2 = 0; time2 < iterationsToCalcualte; time2++) {
                if (!checkSimilarityPrevGen && time2 == 0) {
                    //Check only future generations
                    time2 = time1;
                }
                if (time1 != time2) {
                    double thetaTime2 = Math.abs(getTheta(time2));
                    double measure = Math.min(thetaTime1, thetaTime2) / Math.max(thetaTime1, thetaTime2);
                    if (Math.floor(measure * 100) > max) {
                        itClosestMatch = time2;
                        max = (int) Math.floor(measure * 100);
                        if (max == 100) {
                            time2 = iterationsToCalcualte - 1;
                        }
                    }
                }
            }
            similarity[time1][0] = max;
            similarity[time1][1] = itClosestMatch;
        }

        return similarity;
    }

    private double getTheta(int time) {
        double theta;
        if(shouldUseCustom){
            theta = alphaCustom * livingCells[time]
                + betaCustom * changeLivingCells[time]
                + gammaCustom * geometricFactor[time];
        }else{
            theta = alpha * livingCells[time]
                + beta * changeLivingCells[time]
                + gamma * geometricFactor[time];
        }
        return theta;
    }

    /**
     *
     * @param time
     * @return The sum of x and y coordinates of living cells
     */
    private void calculateGeometricFactor(int iterationsToCalcualte) {
        geometricFactor = new int[iterationsToCalcualte + 1];
        setPattern(originalPattern);

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
        originalPattern = Pattern;
        gameboard.insertArray(originalPattern, 1, 1);
    }

    public void setCheckSimilarityPrevGen(boolean checkSimilarityPrevGen) {
        this.checkSimilarityPrevGen = checkSimilarityPrevGen;
    }

    public void setShouldUseCustom(boolean shouldUseCustom) {
        this.shouldUseCustom = shouldUseCustom;
    }
}
