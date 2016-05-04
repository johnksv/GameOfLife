package gol.stensli;

import gol.model.Board.*;
import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;
import lieng.GIFWriter;

/**
 * Draws a board to GIF format for a chosen number of generations. <br>
 * Will not finish if the board becomes empty or if the board is a 100% match to the original. 
 * 
 * @author s305084 - Stian Stensli
 */
public final class GifMaker {

    private static GIFWriter gifWriter;

    private static byte[][] orginalPattern;
    private static int cellSize;
    private static Color cellColor;
    private static int width;
    private static int height;
    
    private final static int MAXCELLSIZE = 20;
    
    /**
     * Final abstract not allowed, but is now effectively abstract.
     * The only method is static, no reason to make an object of type GifMaker.
     */
    private GifMaker() {
    }

    /**
     * <p>
     * Draws a sequence of next generations from given board to GIF formate. Stops drawing if the next
     * generation is the same as the first. Stops drawing if the next generation is empty.
     * This method "follows" the cells, it will always try to centre the pattern given to the middle of the gif.
     * It will automatically resize the cellSize to fit the gif width and height.
     * </p>
     * <b>Important note:</b> Color is ava.awt.Color not javafx.scene.paint.Color.
     *
     * @param board given pattern
     * @param gw gif writer
     * @param newWidth gif width
     * @param newHeight gif height
     * @param bgCl background color
     * @param cellCl Cell color
     * @param genCount number of remaining frames
     * @throws IOException If anything goes wrong during saving
     */
    public static void makeGif(Board board, GIFWriter gw,
            int newWidth, int newHeight, Color bgCl, Color cellCl, int genCount) throws IOException {

        cellColor = cellCl;
        gifWriter = gw;
        height = newHeight;
        width = newWidth;

        gifWriter.setBackgroundColor(bgCl);

        Board activeboard = new DynamicBoard();
        activeboard.insertArray(board.getBoundingBoxBoard());
        activeboard.setRule(board.getRule());

        orginalPattern = activeboard.getBoundingBoxBoard();
        makeFrame(activeboard, genCount);
    }

    /**
     * Tail recursion makes it possible to never get stackoverflow.
     *
     * If your method is tail-recursive then the compiler will not store the values from each method call. 
     * You can make your method tail-recursive if you do your calculations when you return your values for each call. 
     *  
     * e.g: 
     *     return x + recursive method(x);
     *
     * This method is recursive, since I chose to return if the board is empty or equal to the first board,  
     * then I do feel that a recursive method is less clunky then a for/while loop. 
     * 
     * Java does not support tail recursion. 
     *
     */
    private static void makeFrame(Board frame, int counter) throws IOException {
        if (counter <= 0) {
            gifWriter.close();
            return;
        }

        int xoffset = 0;
        int yoffset = 0;
        byte[][] boarders = frame.getBoundingBoxBoard();

        gifWriter.createNextImage();

        //Sets the cellSize to fit the gif.
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
        
        //Sets the cellSize to a maximum value if the new cellSize is bigger then the maximum
        if (cellSize > MAXCELLSIZE) {
            cellSize = MAXCELLSIZE;
            yoffset = height / 2 - (boarders.length * cellSize) / 2;
            xoffset = width / 2 - (boarders[0].length * cellSize) / 2;
        }

        //Draws the next frame.
        for (int i = 0; i < boarders.length; i++) {
            for (int j = 0; j < boarders[i].length; j++) {

                if (i * cellSize + cellSize > height && j * cellSize + cellSize > width) {
                    break;
                } else if (boarders[i][j] == 64) {
                    if (cellSize < 3) {
                        if(j != width && i != height ){
                            gifWriter.setPixelValue(j, i, cellColor);
                        }
                    } else {
                        gifWriter.fillRect(j * cellSize + 1 + xoffset, xoffset + j * cellSize + cellSize - 1,
                                yoffset + i * cellSize + 1, yoffset + i * cellSize + cellSize - 1, cellColor);

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
