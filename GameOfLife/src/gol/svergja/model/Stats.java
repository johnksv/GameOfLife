package gol.svergja.model;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;

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
     */
    public int[][] getStatistics(int iterations, boolean calcChangeLiving, boolean calcSimilarity) {
        iterations++;
        int[][] stats = new int[iterations][4];
        livingCells = new int[iterations];
        changeLivingCells = new int[iterations];
        similarityMeasure = new int[iterations][2];
        geometricFactor = new int[iterations];

        //Ensure we start with orginal pattern
        setPattern(originalPattern);
        //If similarity should be calculated, then should change in living to.
        if (calcSimilarity) {
            calcChangeLiving = true;
        }

        //First calc Living and change in living, as they are needed for similarity
        for (int ite = 0; ite < iterations; ite++) {
            calcLivingAndGeometric(ite);

            if (getLivingCells() != null) {
                stats[ite][0] = getLivingCells()[ite];
            }

            activeBoard.nextGen();
        }

        if (calcChangeLiving) {
            for (int ite = 0; ite < iterations - 1; ite++) {
                calcChangeLiving(ite);
                stats[ite][1] = getChangeLivingCells()[ite];
            }
        }

        //Can now calc similarity, then append data.
        //TODO Check first iteration of test.
        if (calcSimilarity) {
            calcSimilarity(iterations);
            for (int ite = 0; ite < iterations; ite++) {
                stats[ite][2] = getSimilarityMeasure()[ite][0];
                stats[ite][3] = getSimilarityMeasure()[ite][1];
            }
        }

        return stats;
    }

    private void calcLivingAndGeometric(int curIte) {
        //Calculate Living and geometric factor
        int livingThisGen = 0;
        byte[][] boundedBoard = activeBoard.getBoundingBoxBoard();
        for (int row = 0; row < boundedBoard.length; row++) {
            for (int col = 0; col < boundedBoard[row].length; col++) {
                if (boundedBoard[row][col] == 64) {
                    livingThisGen++;
                    //TODO Improve GeometricFactor
                    geometricFactor[curIte] += row + col;
                }
            }
        }
        livingCells[curIte] = livingThisGen;
    }

    private void calcChangeLiving(int curIte) {
        changeLivingCells[curIte] = getLivingCells()[curIte + 1] - getLivingCells()[curIte];
    }

    private void calcSimilarity(int iterationsToCalc) {
        for (int ite = 0; ite < iterationsToCalc; ite++) {
            double thetaTime1 = Math.abs(calcTheta(ite));

            int max = 0;
            int itClosestMatch = -1;
            //Checks if future or prev generations is similar
            for (int time2 = 0; time2 < iterationsToCalc; time2++) {
                if (!checkSimilarityPrevGen && time2 == 0) {
                    //Check only future generations
                    time2 = ite;
                }
                if (ite != time2) {
                    double thetaTime2 = Math.abs(calcTheta(time2));
                    double measure = Math.min(thetaTime1, thetaTime2) / Math.max(thetaTime1, thetaTime2);
                    if (Math.floor(measure * 100) > max) {
                        itClosestMatch = time2;
                        max = (int) Math.floor(measure * 100);
                        if (max == 100) {
                            time2 = ite - 1;
                        }
                    }
                }
            }
            similarityMeasure[ite][0] = max;
            similarityMeasure[ite][1] = itClosestMatch;
        }
    }

    private double calcTheta(int time) {
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
     * Set if the pattern should check similarity with a previous state of the
     * same pattern.
     *
     * @param checkSimilarityPrevGen True if the pattern should check its
     * similarity in previous states. False if pattern should only check for
     * similarity in future patterns. Default: false
     */
    public void setCheckSimilarityPrevGen(boolean checkSimilarityPrevGen) {
        this.checkSimilarityPrevGen = checkSimilarityPrevGen;
    }

    /**
     * Sets if custom values for geometric factor should be used or not
     *
     * @param shouldUseCustom true if custom values by s305089 - John Kasper
     * Svergja - should be used, false if not.
     */
    public void setShouldUseCustom(boolean shouldUseCustom) {
        this.shouldUseCustom = shouldUseCustom;
    }

    /**
     * Constructs an new Board instance, and inserts the given board bounding
     * box pattern.
     * <b>Technical info:</b> The board constructed is of type array board. This
     * is due to performance of array board vs dynamic board.
     *
     * @param boardToSet The board that should be copied. Copies the pattern and
     * rule
     * @see gol.model.Board.Board
     */
    public void setBoard(Board boardToSet) {
        originalPattern = boardToSet.getBoundingBoxBoard();
        activeBoard = new ArrayBoard(2000, 2000);

        activeBoard.setRule(boardToSet.getRule());
        setPattern(originalPattern);
    }

    private void setPattern(byte[][] pattern) {
        activeBoard.clearBoard();
        activeBoard.insertArray(pattern, 120, 120);
    }

    /**
     * Get the array containing information about living cells.
     *
     * @return The array of living cells. If stats has not been calculated, this
     * method will return null.
     */
    public int[] getLivingCells() {
        return livingCells;
    }

    /**
     * Get the array containing information about change in living cells.
     *
     * @return The array containing information about change in living cells. If
     * stats has not been calculated, this method will return null.
     */
    public int[] getChangeLivingCells() {
        return changeLivingCells;
    }

    /**
     * Get the array containing information about the similarity between each
     * generation of a pattern.
     *
     * @return The array containing information about change in living cells. If
     * stats has not been calculated, this method will return null.
     */
    public int[][] getSimilarityMeasure() {
        return similarityMeasure;
    }
}
