package gol.s305089.model;

import gol.model.Board.Board;
import gol.model.Board.DynamicBoard;

/**
 * @author s305089 - John Kasper Svergja
 */
public class Stats {

    private Board activeBoard;
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

    /**
     * Calculates all statistics provided by this class. The provided statistics
     * are living cells, change in living cells, and similarity with future and
     * previous patterns.
     *
     * @param iterations The number of iteration the statistics should cover
     * @param calcChangeLiving If change in living cells should be calculated or
     * not.
     * @param calcSimilarity If similarity with future patterns should be
     * calculated.
     * @return The result from the calculations. The data has the following
     * index: Living cells: 0, Change in living cells: 1, Similarity: 3, closest
     * similar generation: 4.
     * @see #calcCountLiving(int)
     * @see #calcChangeInLiving(int)
     * @see #getSimilarityMeasure(int)
     */
    public int[][] getStatistics(int iterations, boolean calcChangeLiving, boolean calcSimilarity) {
        int[][] stats = new int[iterations + 1][4];

        calcCountLiving(iterations);
        if (calcChangeLiving) {
            calcChangeInLiving(iterations);
        }
        if (calcSimilarity) {
            if (changeLivingCells == null) {
                changeLivingCells = calcChangeInLiving(iterations);
            }
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
     * Calculate the living cells for the iterations given.
     *
     * @param iterationsToCalcualte The number of iterations to calculate
     * @return The array containing the values. The length of the array
     * corresponds to the number of iteration. Index 0 refers to the 0-th
     * generation.
     */
    public int[] calcCountLiving(int iterationsToCalcualte) {
        if (iterationsToCalcualte == 0) {
            return new int[]{0};
        }

        int[] countOfLiving = new int[iterationsToCalcualte];
        for (int i = 0; i < countOfLiving.length; i++) {
            if (i == 0) {
                setPattern(originalPattern);
            }

            int livingThisGen = 0;
            for (byte[] row : (byte[][]) activeBoard.getBoundingBoxBoard()) {
                for (byte value : row) {
                    if (value == 64) {
                        livingThisGen++;
                    }
                }
            }
            countOfLiving[i] = livingThisGen;
            activeBoard.nextGen();
        }
        livingCells = countOfLiving;
        return countOfLiving;
    }

    public int[] calcChangeInLiving(int iterationsToCalcualte) {
        if (livingCells == null) {
            calcCountLiving(iterationsToCalcualte + 1);
        }
        int[] countChangeOfLiving = new int[iterationsToCalcualte];
        setPattern(originalPattern);
        for (int time = 0; time < iterationsToCalcualte - 1; time++) {
            countChangeOfLiving[time] = livingCells[time + 1] - livingCells[time];
            activeBoard.nextGen();
        }
        changeLivingCells = countChangeOfLiving;
        return countChangeOfLiving;
    }

    /**
     * Calculates the similarity of a pattern in the iterations given.
     * <p>
     * By default this method will only calculate similarity with patterns in
     * the future. This can be changed by calling {@link #setCheckSimilarityPrevGen(boolean)
     * }.
     * </p>
     *
     * @param iterationsToCalcualte The number of iterations to calculate
     * @return First index: similarity measure, second index: iteration number
     * of closest match (this value is -1 if no match was found).
     */
    public int[][] getSimilarityMeasure(int iterationsToCalcualte) {
        int[][] similarity = new int[iterationsToCalcualte + 1][2];
        if (changeLivingCells == null) {
            changeLivingCells = calcChangeInLiving(iterationsToCalcualte);
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
        if (shouldUseCustom) {
            theta = alphaCustom * livingCells[time]
                    + betaCustom * changeLivingCells[time]
                    + gammaCustom * geometricFactor[time];
        } else {
            theta = alpha * livingCells[time]
                    + beta * changeLivingCells[time]
                    + gamma * geometricFactor[time];
        }
        return theta;
    }

    /**
     * Calculates the geometric factor for the pattern.
     *
     * @param iterationsToCalcualte the number of iterations to calculate
     */
    private void calculateGeometricFactor(int iterationsToCalcualte) {
        geometricFactor = new int[iterationsToCalcualte + 1];
        setPattern(originalPattern);

        for (int iterations = 0; iterations < iterationsToCalcualte; iterations++) {
            byte[][] boundedBoard = activeBoard.getBoundingBoxBoard();
            for (int row = 0; row < boundedBoard.length; row++) {
                for (int column = 0; column < boundedBoard[row].length; column++) {
                    if (boundedBoard[row][column] == 64) {
                        //TODO Improve GeometricFactor
                        geometricFactor[iterations] += row + column;
                    }
                }
            }
            activeBoard.nextGen();
        }
    }

    /**
     * Constructs an new Board instance, and inserts the given board bounding
     * box pattern.
     *
     * @param boardToSet The board that should be copied. Copies the pattern and
     * rule
     * @see gol.model.Board.Board#insertArray(byte[][], int, int)
     */
    public void setBoard(Board boardToSet) {
        originalPattern = boardToSet.getBoundingBoxBoard();
        activeBoard = new DynamicBoard(5, 5);
        activeBoard.setRule(boardToSet.getRule());
        setPattern(originalPattern);
    }

    private void setPattern(byte[][] pattern) {
        activeBoard.clearBoard();
        activeBoard.insertArray(pattern);
    }

    public void setCheckSimilarityPrevGen(boolean checkSimilarityPrevGen) {
        this.checkSimilarityPrevGen = checkSimilarityPrevGen;
    }

    public void setShouldUseCustom(boolean shouldUseCustom) {
        this.shouldUseCustom = shouldUseCustom;
    }
}
