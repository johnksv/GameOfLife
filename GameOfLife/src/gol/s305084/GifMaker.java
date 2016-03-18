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

    //TODO make a copy and compare.

    /**
     *
     * @param board
     * @param gw
     * @param width
     * @param height
     * @param bgCl
     * @param cellCl
     * @param counter
     * @throws IOException
     */
    public static void makeGif(byte[][] board, GIFWriter gw,
            int width, int height, Color bgCl, Color cellCl, int counter) throws IOException {
        
        
        cellColor = cellCl;
        gifWriter = gw;
        gifWriter.setBackgroundColor(bgCl);
        Board activeboard = new ArrayBoard(board.length, board[0].length);
        activeboard.insertArray(board, 1, 1);

        sizeCell = (height < width) ? height / activeboard.getBoundingBoxBoard().length : width / activeboard.getBoundingBoxBoard().length;
        
        System.out.println(sizeCell);
        makeFrame(activeboard, 20);
    }

    /**
     * Java does not support tail recursion
     *
     */
    private static void makeFrame(Board frame, int counter) throws IOException {
        if (counter <= 0) {
            gifWriter.close();
            return;
        }
        gifWriter.createNextImage();
        gifWriter.flush();
        System.out.println("still good");
        byte[][] boarders = frame.getBoundingBoxBoard();
        for (int i = 0; i < boarders.length; i++) {
            for (int j = 0; j < boarders[i].length; j++) {
                if (i * sizeCell + sizeCell > 100 || j * sizeCell + sizeCell > 100) {
                    break;
                } else {
                    if (boarders[i][j] == 64) {
                        if(sizeCell < 3){
                            gifWriter.setPixelValue(j, i, cellColor);
                        }
                        gifWriter.fillRect(j * sizeCell + 1, j * sizeCell + sizeCell - 2,
                                i * sizeCell + 1, i * sizeCell + sizeCell - 2, cellColor);
                    }
                }
            }
        }

        frame.nextGen();
        gifWriter.insertCurrentImage();
        makeFrame(frame, --counter);
    }
}
