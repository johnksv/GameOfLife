package gol.model.Logic;

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

    public static void makeGif(byte[][] board, GIFWriter gw,
            int width, int height, Color bgCl, Color cellCl) throws IOException {
        
        sizeCell = (height < width) ? height / board.length : width / board.length;
        cellColor = cellCl;
        gifWriter = gw;
        gifWriter.setBackgroundColor(bgCl);
        Board activeboard = new ArrayBoard(height + 2, width + 2);
        activeboard.insertArray(board, 1, 1);

        makeFrame(activeboard, 20);
    }

    private static Board makeFrame(Board frame, int counter) throws IOException {
        if (counter <= 0) {
            gifWriter.close();
            return frame;
        }
        gifWriter.createNextImage();
        gifWriter.flush();

        byte[][] boarders = frame.getBoundingBoxBoard();
        for (int i = 0; i < boarders.length; i++) {
            for (int j = 0; j < boarders[i].length; j++) {
                if (boarders[i][j] == 64) {
                    gifWriter.fillRect(i * sizeCell, i * sizeCell + sizeCell,
                            j * sizeCell, j * sizeCell + sizeCell, cellColor);
                }
            }
        }

        frame.nextGen();
        gifWriter.insertCurrentImage();
        return makeFrame(frame, --counter);
    }
}
