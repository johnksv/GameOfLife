package gol.s305084;

import gol.model.Board.*;
import java.awt.Color;
import java.io.IOException;
import lieng.GIFWriter;

/**
 *
 * @author s305084
 */
public class GifMaker {

    private static GIFWriter gifWriter;

    private static int sizeCell;
    private static Color cellColor;
    private static int width;
    private static int height;
    //TODO make a copy and compare.

    /**
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
        Board activeboard = new ArrayBoard(board.length, board[0].length);
        activeboard.insertArray(board, 1, 1);

        makeFrame(activeboard, counter);
    }

    /**
     * Java does not support tail recursion
     *
     */
    private static void makeFrame(Board frame, int counter) throws IOException {
        int xoffset = 0;
        int yoffset = 0;

        if (counter <= 0) {
            gifWriter.close();
            return;
        }
        gifWriter.createNextImage();
        gifWriter.flush();
        byte[][] boarders = frame.getBoundingBoxBoard();
        //sizeCell = (height > width) ? height / boarders.length : width / boarders[0].length;
        if (height / boarders.length < width / boarders[0].length) {
            sizeCell = height / boarders.length;
            xoffset = width/2 - (boarders[0].length * sizeCell)/2;
            if(xoffset < 0){
                xoffset = 0;
            }
        } else {
            sizeCell = width / boarders[0].length;
            yoffset = height/2 - (boarders.length * sizeCell)/2;
            if(yoffset < 0){
                yoffset = 0;
            }
        }
        for (int i = 0; i < boarders.length; i++) {
            for (int j = 0; j < boarders[i].length; j++) {

                if (i * sizeCell + sizeCell > height && j * sizeCell + sizeCell > width) {
                    break;
                } else {

                    if (boarders[i][j] == 64) {
                        if (sizeCell < 3) {
                            gifWriter.setPixelValue(j, i, cellColor);
                        } else {
                            System.out.println(j * sizeCell + sizeCell - 2);
                            gifWriter.fillRect(j * sizeCell + 1 + xoffset, xoffset + j * sizeCell + sizeCell - 1,
                                    yoffset + i * sizeCell + 1, yoffset + i * sizeCell + sizeCell - 1, cellColor);

                        }
                    }
                }
            }
        }
        System.out.println("next gen");
        frame.nextGen();
        gifWriter.insertCurrentImage();
        makeFrame(frame, --counter);
    }
}
