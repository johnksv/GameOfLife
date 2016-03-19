package gol.s305089.controller.gif;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
import java.awt.Color;
import java.io.IOException;
import javafx.stage.FileChooser;
import lieng.GIFWriter;

/**
 * @author s305089
 *
 * Last edited: 18.03.2016
 */
public final class GifWriter {

    private static Board gameboard;
    private static GIFWriter gifWriter;
    private static int gifWidth = 100;
    private static int gifHeight = 100;
    private static String saveLocation;
    private static int durationBetweenFrames = 1000;
    private static int cellSize = 10;
    private static int iterations = 20;

    public static void writeBoardtoGIF(byte[][] pattern) throws IOException {
      
        GifWriter.gameboard = new ArrayBoard(10,10);
        gameboard.insertArray(pattern, 3, 3);

        gifWriter = new GIFWriter(gifWidth, gifHeight, saveLocation, durationBetweenFrames);

        writeGIF(iterations);
    }
    

    public static void writeGIF(int iterations) throws IOException {
        if (iterations > 0) {
            iterations--;
            for (int y = 0; y < gameboard.getArrayLength(); y++) {
                for (int x = 0; x < gameboard.getArrayLength(y); x++) {
                    if (gameboard.getCellState(y, x)) {
                        gifWriter.fillRect(y*cellSize, y*cellSize + cellSize, x*cellSize, x*cellSize + cellSize, Color.BLACK);
                    }
                }
            }
            gifWriter.insertAndProceed();
            gameboard.nextGen();
            writeGIF(iterations);
        } else {
            gifWriter.close();
        }
    }

    /**
     * @param aGifWidth the gifWidth to set
     */
    public static void setGifWidth(int aGifWidth) {
        gifWidth = aGifWidth;
    }

    /**
     * @param aGifHeight the gifHeight to set
     */
    public static void setGifHeight(int aGifHeight) {
        gifHeight = aGifHeight;
    }

    /**
     * @param aSaveLocation the saveLocation to set
     */
    public static void setSaveLocation(String aSaveLocation) {
        saveLocation = aSaveLocation;
    }

    /**
     * @param aDurationBetweenFrames the durationBetweenFrames to set
     */
    public static void setDurationBetweenFrames(int aDurationBetweenFrames) {
        durationBetweenFrames = aDurationBetweenFrames;
    }

    /**
     * @param aCellSize the cellSize to set
     */
    public static void setCellSize(int aCellSize) {
        cellSize = aCellSize;
    }

    /**
     * @param aIterations the iterations to set
     */
    public static void setIterations(int aIterations) {
        iterations = aIterations;
    }
}
