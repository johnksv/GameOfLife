package gol.s305084;

import gol.model.Board.*;
import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;
import lieng.GIFWriter;

/**
 * Draws a board to GIF format.
 *
 * @author s305084
 */
public class GifMaker {

    private static GIFWriter gifWriter;

    private static byte[][] orginalPattern;
    private static int cellSize;
    private static Color cellColor;
    private static int width;
    private static int height;

    private final static int maxCellSize = 20;

    /**
     * Draws a boards next generations on a GIF. Stops drawing if the next
     * generation is the same as the first. Stops drawing if the next generation
     * is empty.
     *
     * @param board
     * @param gw
     * @param newWidth
     * @param newHeight
     * @param bgCl
     * @param cellCl
     * @param counter
     * @throws IOException
     */
    public static void makeGif(byte[][] board, GIFWriter gw,
            int newWidth, int newHeight, Color bgCl, Color cellCl, int counter) throws IOException {

        cellColor = cellCl;
        gifWriter = gw;
        height = newHeight;
        width = newWidth;

        gifWriter.setBackgroundColor(bgCl);
        
        //Makes an array that can handle every possibility of how the pattern evolves 
        //After chosen number of generations.
        // More info see https://en.wikipedia.org/wiki/Speed_of_light_(cellular_automaton)
        Board activeboard = new ArrayBoard(board.length + counter * 2, board[0].length + counter * 2);
        activeboard.insertArray(board, counter, counter);

        orginalPattern = activeboard.getBoundingBoxBoard();
        makeFrame(activeboard, counter);
    }

    /**
     * Java does not support tail recursion
     *
     */
    private static void makeFrame(Board frame, int counter) throws IOException {
        int xoffset = 0;
        int yoffset = 0;
        byte[][] boarders = frame.getBoundingBoxBoard();

        if (counter <= 0) {
            gifWriter.close();
            return;
        }
        gifWriter.createNextImage();
        
        if (height / boarders.length < width / boarders[0].length) {
            cellSize = height / boarders.length;
            xoffset = width / 2 - (boarders[0].length * cellSize) / 2;
            if (xoffset < 0) {
                xoffset = 0;
            }
        } else {
            cellSize = width / boarders[0].length;
            yoffset = height / 2 - (boarders.length * cellSize) / 2;
            if (yoffset < 0) {
                yoffset = 0;
            }
        }
        //Sets the cell size to a maximum value if the new cell size is bigger then the maximum
        if (cellSize > maxCellSize) {
            cellSize = maxCellSize;
            yoffset = height / 2 - (boarders.length * cellSize) / 2;
            xoffset = width / 2 - (boarders[0].length * cellSize) / 2;
        }

        for (int i = 0; i < boarders.length; i++) {
            for (int j = 0; j < boarders[i].length; j++) {

                if (i * cellSize + cellSize > height && j * cellSize + cellSize > width) {
                    break;
                } else {

                    if (boarders[i][j] == 64) {
                        if (cellSize < 3) {
                            gifWriter.setPixelValue(j, i, cellColor);
                        } else {
                            gifWriter.fillRect(j * cellSize + 1 + xoffset, xoffset + j * cellSize + cellSize - 1,
                                    yoffset + i * cellSize + 1, yoffset + i * cellSize + cellSize - 1, cellColor);

                        }
                    }
                }
            }
        }
        gifWriter.insertCurrentImage();
        frame.nextGen();

        //Returns if the next generation is the same as the first
        if (Arrays.deepEquals(orginalPattern, frame.getBoundingBoxBoard())) {
            gifWriter.close();
            return;
        } else if (frame.getBoundingBox()[1] - frame.getBoundingBox()[0] < 0) {
            gifWriter.close();
            return;
        }
        makeFrame(frame, --counter);
    }
}
