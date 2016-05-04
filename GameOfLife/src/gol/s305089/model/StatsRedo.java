package gol.s305089.model;

import gol.model.Board.Board;
import gol.model.Board.DynamicBoard;

/**
 * @author s305089 - John Kasper Svergja
 */
public class StatsRedo {

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

            if (livingCells != null) {
                stats[ite][0] = livingCells[ite];
            }

            activeBoard.nextGen();
        }

        if (calcChangeLiving) {
            for (int ite = 0; ite < iterations - 1; ite++) {
                calcChangeLiving(ite);
                stats[ite][1] = changeLivingCells[ite];
            }
        }

        //Can now calc similarity, then append data.
        //TODO Check first iteration of test.
        if (calcSimilarity) {
            calcSimilarity(iterations);
            for (int ite = 0; ite < iterations; ite++) {
                stats[ite][2] = similarityMeasure[ite][0];
                stats[ite][3] = similarityMeasure[ite][1];
            }
        }

        return stats;
    }

    /**
     *
     * @param curIte the current iteration.
     */
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

    /**
     *
     * @param curIte current iteration
     */
    private void calcChangeLiving(int curIte) {
        changeLivingCells[curIte] = livingCells[curIte + 1] - livingCells[curIte];
    }

    /**
     *
     * @param curIte current iteration
     * @param iterationsToCalc how many iteration that should be calculated.
     */
    private void calcSimilarity(int iterationsToCalc) {
        for (int ite = 0; ite < iterationsToCalc; ite++) {
            double thetaTime1 = Math.abs(getTheta(ite));

            int max = 0;
            int itClosestMatch = -1;
            //Checks if future or prev generations is similar
            for (int time2 = 0; time2 < iterationsToCalc; time2++) {
                if (!checkSimilarityPrevGen && time2 == 0) {
                    //Check only future generations
                    time2 = ite;
                }
                if (ite != time2) {
                    double thetaTime2 = Math.abs(getTheta(time2));
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
