package gol.s305054;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.awt.Color;
import java.util.Arrays;
import lieng.GIFWriter;

/**
 * This class is used to create a .gif file showing several generations of a GoL
 * board.
 *
 * @author Trygve Vang - s305054
 */
public class GIFWriterS305054 {

    private Board copiedBoard; //deep copy of board - TODO check if boundBox or not.

    private int width = 10; //Width of .gif - Hardcoded value will be changed - rows
    private int height = 10; //Height of .gif - Hardcoded value will be changed - columns
    private int time = 300; // 1000 ms = 1s - later, listener to a slider
    private int cellSize = 10;
    private short nPicturesLeft = 15; //number of pictures left - TODO nPictures cannot be less than 1
    private Color bgColor = Color.WHITE; //Standard color
    private Color cColor = Color.BLACK; //Standard color
    String path = "testGif.gif"; //Filepath - later, normal output stream
    private GIFWriter gifWriter;
    /**
     * Deep copy the active gameboard into a copied gameboard. When changing the
     * copied board, the originale board will stay the same.
     *
     * @param originaleBoard Active board that is being displayed in the Game of
     * Life GUI.
     * @param bgColor Background color of the board
     * @param cColor Color of cell in board.
     */
    public void prepareGIF(Board originaleBoard, int cellSize, Color bgColor, Color cColor) { //TODO add parameters height width
        try {
            byte[][] originaleArray = originaleBoard.getBoundingBoxBoard();
            copiedBoard = new ArrayBoard(width, height);
            /*
             Param originaleBoard - get array, each element in originaleArray, assigned to copied array
             assigns copied array to copiedBoard.
             */
            copiedBoard.insertArray(originaleArray, 3, 3); //Get boundingBox and insert it to an empty.

            if (bgColor != null) {
                this.bgColor = bgColor;
            }

            if (cColor != null) {
                this.cColor = cColor;
            }

            if (cellSize < 10) {
                this.cellSize = 10;
            } else {
                this.cellSize = cellSize;
            }

            gifWriter = new GIFWriter(200, 200, path, time);
            gifWriter.setBackgroundColor(this.bgColor);

        } catch (IOException ex) {
            Logger.getLogger(GIFWriterS305054.class.getName()).log(Level.SEVERE, null, ex);

            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Something went wrong");
            alert.setHeaderText("There was an input/output error");
            alert.setContentText("Could not proceed. Please try again.");
            alert.showAndWait();
        }

    }

    /**
     * This method is used for creating a .gif file with x generations
     * (pictures), with dimension ixj, and speed y. makeGif() uses recursion to
     * create pictures that is loaded into a gifstream. when there is one
     * picture left, it will create that last image, and close the stream.
     */
    public void makeGIF() { //TODO method should be changed to propely work recursion wise
        try {
            if (nPicturesLeft == 1) { //number of pictures left == 1
                for (int i = 0; i < copiedBoard.getArrayLength(); i++) {
                    for (int j = 0; j < copiedBoard.getArrayLength(i); j++) {
                        if (copiedBoard.getCellState(i, j)) {
                            gifWriter.fillRect(i * cellSize, i * cellSize + cellSize, j * cellSize, j * cellSize + cellSize, cColor);
                        }
                    }
                }

                gifWriter.insertAndProceed();
                gifWriter.close();
                System.out.println("Bilder igjen: " + nPicturesLeft + "\nDone.");
                //return the finished gif, ready to be exported            
            } else {

                for (int i = 0; i < copiedBoard.getArrayLength(); i++) {
                    for (int j = 0; j < copiedBoard.getArrayLength(i); j++) {
                        if (copiedBoard.getCellState(i, j)) {
                            gifWriter.fillRect(i * cellSize, i * cellSize + cellSize, j * cellSize, j * cellSize + cellSize, cColor);
                        }
                    }
                }
                gifWriter.insertAndProceed();

                copiedBoard.nextGen(); //calculates the next generation of the board.
                System.out.println("Bilder igjen: " + nPicturesLeft);
                nPicturesLeft -= 1;
                makeGIF(); //recursive call.
            }
        } catch (IOException e) { //TODO catch stackoverflow error
            e.printStackTrace();

            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Something went wrong");
            alert.setHeaderText("There was an input/output error");
            alert.setContentText("Could not proceed. Please try again.");
            alert.showAndWait();
        }
    }

}
