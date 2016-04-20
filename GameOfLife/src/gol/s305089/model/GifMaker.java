package gol.s305089.model;

import gol.controller.UsefullMethods;
import gol.model.Board.Board;
import gol.model.Board.DynamicBoard;
import java.io.IOException;
import java.util.Random;
import javafx.scene.paint.Color;
import lieng.GIFWriter;

/**
 * The GifMaker class delivers functionality to write GIFs with the GIFLib
 * library by Henrik Lieng. It acts as an helper class between the Controller
 * {@link gol.s305089.controller.GifMakerController} and the GIF library.
 * <p>
 * For answers regarding recursion (from assignment paper) see
 * {@link  #writePatternToGIF(int)}.
 * <p>
 * Created: 18.03.2016 Last edited: 17.04.2016
 *
 * @author s305089
 */
public final class GifMaker {

    private Board activeBoard;
    private byte[][] originalPattern;

    private GIFWriter gifWriter;
    private int gifWidth = 200;
    private int gifHeight = 200;
    private String saveLocation;
    private int durationBetweenFrames = 500;
    private double[] moveGridValues;
    private double cellSize = 10;
    private boolean centerPattern = false;
    private boolean followTopLeft = false;
    private boolean autoCellSize = false;
    private boolean randomColor = false;
    private java.awt.Color cellColor = java.awt.Color.BLACK;
    private java.awt.Color backgroundColor = java.awt.Color.WHITE;

    /**
     * Class constructor.
     */
    public GifMaker() {
    }

    /**
     * //TODO Svar på spørmål om halerekursjon Forstå hva halerekursjon (eng:
     * tail recursion) er og fordelen er med slik rekursjon. Test om metoden
     * dere har implementert over utfører slik halerekursjon. Ut ifra denne
     * testen, diskuter nå fordeler/ulemper med rekursjon for dette problemet.
     * Til slutt, bruk Internett til å finne ut om Java/JVM støtter
     * halerekursjon og eventuelt hvordan.
     *
     * @param iterations Number of iterations to draw in GIF.
     * @throws java.io.IOException
     */
    public void writePatternToGIF(int iterations) throws IOException {

        gifWriter = new GIFWriter(gifWidth, gifHeight, saveLocation, durationBetweenFrames);
        if (activeBoard != null || originalPattern != null) {
            //If not called, manipulations is done on the current gen of this activeBoard.
            //Makes an new board with the "start" originalPattern.
            setPattern(originalPattern);
            gifWriter.setBackgroundColor(backgroundColor);
            writeGIF(iterations);
        } else {
            System.err.println("Pattern is not set. Be sure that setPattern() is called first");
        }
    }

    private void writeGIF(int iterations) throws IOException {
        if (iterations > 0) {
            iterations--;
            if (autoCellSize) {
                calculateCellSize();
            }
            gifWriter.flush();

            for (int y = 1; y < activeBoard.getArrayLength(); y++) {
                for (int x = 1; x < activeBoard.getArrayLength(y); x++) {
                    if (activeBoard.getCellState(y, x)) {
                        if (randomColor) {
                            Random random = new Random();
                            cellColor = new java.awt.Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
                        }
                        
                        int x1 = (int) (x * cellSize);
                        int x2 = (int) (x * cellSize + cellSize);
                        int y1 = (int) (y * cellSize);
                        int y2 = (int) (y * cellSize + cellSize);
                        if (!followTopLeft) {
                            //Need moveGridValues so the GIF dosn't follow top left when expanding.
                            x1 += (int) moveGridValues[0];
                            x2 += (int) moveGridValues[0];
                            y1 += (int) moveGridValues[1];
                            y2 += (int) moveGridValues[1];
                        }

                        if (x1 >= 0 && x2 >= 0 && y1 >= 0 && y2 >= 0) {
                            if (x1 < gifWidth && x2 < gifWidth && y1 < gifHeight && y2 < gifHeight) {
                                gifWriter.fillRect(x1, x2, y1, y2, cellColor);
                            }
                        }
                    }
                }
            }
            gifWriter.insertAndProceed();
            activeBoard.nextGen();
            writeGIF(iterations);
        } else {
            gifWriter.close();
        }
    }

    private void calculateCellSize() {
        double spacing = 5;
        int longestRow = UsefullMethods.longestRow(activeBoard.getBoundingBoxBoard());
        cellSize = Math.floor(gifHeight / (activeBoard.getBoundingBoxBoard().length + spacing));
        if (cellSize > gifWidth / (longestRow + spacing)) {
            cellSize = Math.floor(gifWidth / (longestRow + spacing));
        }
    }

    /**
     * Set the size for each cell in the GIF.
     *
     * @param cellSize the size given in pixels
     */
    public void setCellSize(double cellSize) {
        this.cellSize = cellSize;
    }

    /**
     * Sets the duration between each frame in the GIF.
     *
     * @param durationBetweenFrames Given in milliseconds. If this value is 0 or
     * less, the value is set to 1.
     */
    public void setDurationBetweenFrames(int durationBetweenFrames) {
        this.durationBetweenFrames = durationBetweenFrames <= 0 ? 1 : durationBetweenFrames;
    }

    /**
     * @param gifWidth set the width of the GIF, in pixels
     */
    public void setGifWidth(int gifWidth) {
        this.gifWidth = gifWidth;
    }

    /**
     * @param gifHeight set the height of the GIF, in pixels
     */
    public void setGifHeight(int gifHeight) {
        this.gifHeight = gifHeight;
    }

    /**
     * @param SaveLocation set the save location to be used when generating a
     * GIF.
     */
    public void setSaveLocation(String SaveLocation) {
        this.saveLocation = SaveLocation;
    }

    /**
     * Constructs an new Board instance, and inserts this board
     *
     * @see gol.model.Board.Board#insertArray(byte[][], int, int)
     * @param patternToSet the originalPattern to set
     */
    public void setPattern(byte[][] patternToSet) {
        activeBoard = new DynamicBoard(10, 10);
        moveGridValues = activeBoard.getMoveGridValues();
        this.originalPattern = patternToSet;
        placePattern(patternToSet);
        activeBoard.setCellSize(cellSize);
    }

    private void placePattern(byte[][] patternToSet) {
        if (centerPattern) {
            int y = (int) ((gifHeight / cellSize) / 2 - patternToSet.length / 2);
            int x = (int) ((gifWidth / cellSize) / 2 - patternToSet[0].length / 2);
            activeBoard.insertArray(patternToSet, y, x);
        } else {
            activeBoard.insertArray(patternToSet, 2, 2);
        }
    }

    /**
     * Set if the originalPattern should be centered on GIF or not.
     *
     * @param centerPattern If false, the originalPattern will be placed at
     * top-left corner
     */
    public void setCenterPattern(boolean centerPattern) {
        this.centerPattern = centerPattern;
    }

    /**
     * Calculates the maximum cell size so all cells in first generation will
     * fill the gif.
     *
     * Be aware that this only set the cell size based on the length/size of the
     * first generation of the board.
     *
     * If this method is set to false under runtime, be sure to call
     * {@link #setCellSize(double)} to not use this value.
     *
     * @param autoCellSize
     */
    public void setAutoCellSize(boolean autoCellSize) {
        this.autoCellSize = autoCellSize;
    }

    public void setCellColor(Color cellColor) {
        //Converts each rgb double values to int (in domain 0-255).
        java.awt.Color newColor = new java.awt.Color((int) (cellColor.getRed() * 255),
                (int) (cellColor.getGreen() * 255),
                (int) (cellColor.getBlue() * 255));
        this.cellColor = newColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        //Converts each rgb double values to int (in domain 0-255).
        java.awt.Color newColor = new java.awt.Color((int) (backgroundColor.getRed() * 255),
                (int) (backgroundColor.getGreen() * 255),
                (int) (backgroundColor.getBlue() * 255));
        this.backgroundColor = newColor;
    }

    public void setRandomColor(boolean randomColor) {
        this.randomColor = randomColor;
    }

    public void setFollowTopLeft(boolean followTopLeft) {
        this.followTopLeft = followTopLeft;
    }
}
