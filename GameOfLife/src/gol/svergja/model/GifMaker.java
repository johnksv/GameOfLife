package gol.svergja.model;

import gol.model.Board.Board;
import gol.model.Board.DynamicBoard;
import java.io.IOException;
import java.util.Random;
import javafx.scene.paint.Color;
import lieng.GIFWriter;

/**
 * The GifMaker class delivers functionality to write GIFs with the GIFLib
 * library by Henrik Lieng. It acts as an helper class between the Controller
 * {@link gol.svergja.controller.GifMakerController} and the GIF library.
 *
 * <p>
 * If no variabels are set, the default one will be used.</p>
 * <table summary="Tabel of variables with default values">
 * <tr><td>Variable</td><td>Default value</td></tr>
 * <tr><td>Gif width</td><td>200 px</td></tr>
 * <tr><td>Gif height</td><td>200 px</td></tr>
 * <tr><td>Duration each frame</td><td>500 ms</td></tr>
 * <tr><td>Cell size</td><td>10 px</td></tr>
 * <tr><td>Auto cell size</td><td>false</td></tr>
 * <tr><td>Center pattern</td><td>false</td></tr>
 * <tr><td>Random cell color</td><td>false</td></tr>
 * <tr><td>Cell color</td><td>Black</td></tr>
 * <tr><td>Background color</td><td>White</td></tr>
 * </table>
 *
 * <h3>Answers regarding tail recursion</h3>
 * Answer to questions from assignment paper. The questions relates to the
 * private method {@link #writeGIF(int)}.
 *
 * <h4>Hva er halerekursjon, og hva er fordelen</h4>
 * Halerekursjon er om man har et rekursivt kall helt til slutt i metoden (og
 * ingenting etter). Det som skjer da er at kompilatoren optimaliserer koden,
 * slik at alle rekursive kall blir lagret på samme stack frame. Dette gjør at
 * man slipper å bekymre seg for faren for stack overflow, og bruk av rekursjon
 * kan dermed bli mer naturlig.
 * <p>
 * Metoden ville dermed ikke ha vært halerekursiv om det hadde stått:
 * </p>
 * <code>
 * writeGIF(iterations);
 * System.out.println("Last instruction of method);
 * </code>
 *
 * <h4>Er metoden halerekursiv</h4>
 * Metoden er halrekursiv, siden det ikke er noen instrukser etter den når
 * tidligere kall returnerer.
 *
 * <h4>Fordeler og ulemper med rekursjon for dette problemet</h4>
 * Utfra hva jeg ser vil jeg si at det er større ulempe enn fordeler ved å løse
 * dette problemet med rekursjon. Ulempen er at man risikerer stack overflow og
 * jeg vil tro at det går tregere enn en for-løkke, i og med at man alltid må
 * bytte/hoppe mellom stack frames.
 *
 * <p>
 * Fordelen kan imidlertid være at det kan være naturlig å løse dette problemet
 * med rekursjon, fordi man kan tenke seg at neste bilde "bygger" på det
 * forrige. Koden kan også bli lettere å lese, siden man slipper å ha alt i en
 * løkke (evt i en egen metode som blir kalt for hver iterasjon).</p>
 *
 *
 * <h4>Støtter Java/JVM halerekursjon</h4>
 * Java/JVM støtter IKKE halerekursjon, noe som gjør at man burde passe på når
 * man bruker rekursjon
 *
 *
 * Created: 18.03.2016 Last edited: 05.05.2016
 *
 * @author s305089
 */
public final class GifMaker {

    private DynamicBoard activeBoard;
    private byte[][] originalPattern;

    private GIFWriter gifWriter;
    private int gifWidth = 200;
    private int gifHeight = 200;
    private int durationBetweenFrames = 500;
    private double cellSize = 10;
    private boolean centerPattern = false;
    private boolean autoCellSize = false;
    private boolean randomCellColor = false;
    private java.awt.Color cellColor = java.awt.Color.BLACK;
    private java.awt.Color backgroundColor = java.awt.Color.WHITE;

    /**
     * Class constructor.
     */
    public GifMaker() {
    }

    /**
     * Writes the currently active board (given by {@link #setBoard(gol.model.Board.Board)
     * }) to an GIF. The default values will be used if no values has been
     * changed with the set methods.
     *
     * @param iterations number of iterations to draw in GIF
     * @param saveLocation the location (absolute path) where the file should be
     * stored
     * @throws java.io.IOException if the file could not be created
     */
    public void writePatternToGIF(int iterations, String saveLocation) throws IOException {

        gifWriter = new GIFWriter(gifWidth, gifHeight, saveLocation, durationBetweenFrames);
        if (activeBoard != null || originalPattern != null) {
            //If not called, manipulations is done on the current gen of this activeBoard.
            //Makes an new board with the originalPattern.
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
                        if (randomCellColor) {
                            Random random = new Random();
                            cellColor = new java.awt.Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
                        }

                        int x1 = (int) (x * cellSize);
                        int x2 = (int) (x * cellSize + cellSize);
                        int y1 = (int) (y * cellSize);
                        int y2 = (int) (y * cellSize + cellSize);

                        //Need offsetValues so the GIF dosn't follow top left when expanding.
                        x1 += (int) activeBoard.offsetValues[0];
                        x2 += (int) activeBoard.offsetValues[0];
                        y1 += (int) activeBoard.offsetValues[1];
                        y2 += (int) activeBoard.offsetValues[1];

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

    /**
     * Needs to be called while center pattern is true. Else the pattern will be
     * way off, due to the offset.
     */
    private void calculateCellSize() {
        if (centerPattern) {
            double spacing = 5;
            byte[][] currentGenBoard = activeBoard.getBoundingBoxBoard();
            int rowLength = currentGenBoard[0].length;
            cellSize = gifHeight / (currentGenBoard.length + spacing);

            if (cellSize > gifWidth / (rowLength + spacing)) {
                cellSize = gifWidth / (rowLength + spacing);
            }
            setPattern(currentGenBoard);
        }
    }

    /**
     * Calculates the maximum cell size so all cells will fill the gif.
     * <b>NB:</b> Auto cell size will only be calculated if center pattern is
     * true. This is due to board implementation.
     *
     * @param autoCalcCellSize if true a new cell size will be calculated each
     * iteration of the gif. This will overwrite the current cell size.<p>
     * If false the previous cell size will be used.</p>
     * @see #setCellSize(double)
     */
    public void setAutoCalcCellSize(boolean autoCalcCellSize) {
        this.autoCellSize = autoCalcCellSize;
    }

    /**
     * Sets the background color of the GIF to this color.
     *
     * @param backgroundColor The background color the gif will have when
     * generated
     */
    public void setBackgroundColor(Color backgroundColor) {
        //Converts each rgb double values to int (in domain 0-255).
        java.awt.Color newColor = new java.awt.Color((int) (backgroundColor.getRed() * 255),
                (int) (backgroundColor.getGreen() * 255),
                (int) (backgroundColor.getBlue() * 255));
        this.backgroundColor = newColor;
    }

    public void setCellColor(Color cellColor) {
        //Converts each rgb double values to int (in domain 0-255).
        java.awt.Color newColor = new java.awt.Color((int) (cellColor.getRed() * 255),
                (int) (cellColor.getGreen() * 255),
                (int) (cellColor.getBlue() * 255));
        this.cellColor = newColor;
    }

    /**
     * Set if the originalPattern should be centered on GIF or not.
     *
     * @param centerPattern if false, the originalPattern will be placed at
     * top-left corner
     */
    public void setCenterPattern(boolean centerPattern) {
        this.centerPattern = centerPattern;
    }

    /**
     * Set the size for each cell in the GIF. This value will be overwritten if
     * automatic cell size has been sat to true.
     *
     * @param cellSize the cell size given in pixels
     * @see #setAutoCalcCellSize(boolean)
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
     * * The height that a new GIF should have.
     *
     * @param gifHeight set the height of the GIF, in pixels
     */
    public void setGifHeight(int gifHeight) {
        this.gifHeight = gifHeight;
    }

    /**
     * The width that a new GIF should have.
     *
     * @param gifWidth set the width of the GIF, in pixels
     */
    public void setGifWidth(int gifWidth) {
        this.gifWidth = gifWidth;
    }

    /**
     * Set if a random cell color should be used for each cell and generation.
     * This option will suppress the current cell color.
     *
     * @param value true for random cell color each generation. False for not.
     * Default: false.
     * @see #setCellColor(javafx.scene.paint.Color)
     */
    public void setRandomColor(boolean value) {
        this.randomCellColor = value;
    }

    /**
     * Constructs an new Board instance, and inserts the given boards pattern.
     *
     * @param boardToSet The board that should be copied. Copies the pattern and
     * rule
     * @see gol.model.Board.Board#insertArray(byte[][], int, int)
     */
    public void setBoard(Board boardToSet) {
        originalPattern = boardToSet.getBoundingBoxBoard();;
        this.activeBoard = new DynamicBoard(10, 10);
        this.activeBoard.setRule(boardToSet.getRule());
        setPattern(originalPattern);
        this.activeBoard.setCellSize(cellSize);
    }

    private void setPattern(byte[][] patternToSet) {
        activeBoard.clearBoard();
        activeBoard.offsetValues[0] = 0;
        activeBoard.offsetValues[1] = 0;
        if (centerPattern) {
            int y = (int) ((gifHeight / cellSize) / 2 - patternToSet.length / 2);
            int x = (int) ((gifWidth / cellSize) / 2 - patternToSet[0].length / 2);
            activeBoard.insertArray(patternToSet, y, x);
        } else {
            activeBoard.insertArray(patternToSet);
        }
    }

}
