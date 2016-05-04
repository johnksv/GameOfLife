package gol.vang.model;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Color;
import java.nio.file.Path;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import lieng.GIFWriter;

/**
 * This class is used to create a .gif file showing several generations of a GoL
 * board.
 *
 * @author Trygve Vang - s305054
 */
public class GIFWriterS305054 {

    private Board copiedBoard; //deep copy of board - TODO check if boundBox or not.
    byte[][] originaleArray;
    private int time; // 1000 ms = 1s - later, listener to a slider
    private int cellSize = 10;
    private int nPicturesLeft = 1; //number of pictures left - TODO nPictures cannot be less than 1
    private Color bgColor = Color.WHITE; //Standard color
    private Color cColor = Color.BLACK; //Standard color
    private GIFWriter gifWriter;
    private int gifWriterSize;

    public void setBoard(Board originaleBoard) {
        if (originaleBoard.getArrayLength() == 0) {
            //Error message
            return;
        }

        originaleArray = originaleBoard.getBoundingBoxBoard();
        
        if (nPicturesLeft < 10) {
            copiedBoard = new ArrayBoard(originaleArray[0].length + 10, originaleArray.length + 10);
        } else {
            copiedBoard = new ArrayBoard(originaleArray[0].length + nPicturesLeft, originaleArray.length + nPicturesLeft);
        }
        
        copiedBoard.setCellSize(this.cellSize);
        copiedBoard.insertArray(originaleArray, (int) ((copiedBoard.getArrayLength() / 2) - (originaleArray[0].length / 2)), (int) ((copiedBoard.getArrayLength() / 2) - (originaleArray.length / 2)));
    }

    public void setCellSize(int cellSize) {
        if (cellSize < 10) {
            return;
        } else {
            this.cellSize = cellSize;
        }
    }

    public void setTime(double time) {
        this.time = (int) (time * 1000);
    }

    public void setColor(Color bgColor, Color cColor) {
        if (bgColor != null) {
            this.bgColor = bgColor;
        }

        if (cColor != null) {
            this.cColor = cColor;
        }
    }

    public void setPictures(int nPictures) {
        if (nPictures < 1) {
            return;
        } else {
            this.nPicturesLeft = nPictures;
        }
    }

    /**
     * Deep copy the active gameboard into a copied gameboard. When changing the
     * copied board, the originale board will stay the same.
     *
     */
    public void prepareGIF(Path saveLocation) {
        try {
            gifWriterSize = (int) (copiedBoard.getArrayLength() * copiedBoard.getCellSize());
            gifWriter = new GIFWriter(gifWriterSize, gifWriterSize, saveLocation.toString(), this.time);
            gifWriter.setBackgroundColor(this.bgColor);
            gifWriter.flush(); //Flushing to set background color to the first image.

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
                            if (i * cellSize + cellSize < gifWriterSize && j * cellSize + cellSize < gifWriterSize) {
                                gifWriter.fillRect(j * cellSize, j * cellSize + cellSize, i * cellSize, i * cellSize + cellSize, cColor);
                            }
                        }
                    }
                }

                gifWriter.insertAndProceed();
                gifWriter.close();
                System.out.println("Pictures left: " + nPicturesLeft + "\nDone.");
                //return the finished gif, ready to be exported            
            } else {

                for (int i = 0; i < copiedBoard.getArrayLength(); i++) {
                    for (int j = 0; j < copiedBoard.getArrayLength(i); j++) {
                        if (copiedBoard.getCellState(i, j) && (i * cellSize < gifWriterSize && i * cellSize + cellSize < gifWriterSize && j * cellSize < gifWriterSize && j * cellSize + cellSize < gifWriterSize)) {

                            gifWriter.fillRect(j * cellSize, (j * cellSize + cellSize), i * cellSize, (i * cellSize + cellSize), cColor);
                        }
                    }
                }
                gifWriter.insertAndProceed();

                copiedBoard.nextGen(); //calculates the next generation of the board.
                System.out.println("Pictures left: " + nPicturesLeft);
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
