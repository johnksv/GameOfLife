package gol.s305089;

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
public final class BoardToGif {

    private static Board gameboard;
    private static GIFWriter gifWriter;
    private static int cellSize = 9;

    public static void writeBoardtoGIF(byte[][] pattern) throws IOException {
        FileChooser filechooser = new FileChooser();
        //TODO set filter to .gif
        String saveLocation = filechooser.showSaveDialog(null).toPath().toString();
        BoardToGif.gameboard = new ArrayBoard(10,10);
        gameboard.insertArray(pattern, 1, 1);

        gifWriter = new GIFWriter(100, 100, saveLocation, 500);

        writeGIF(20);
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
}
