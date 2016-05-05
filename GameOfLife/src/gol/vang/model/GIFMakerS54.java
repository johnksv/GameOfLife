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
 * gameboard, with the help of the GIFLib library (created by Henrik Lieng).
 *
 * @author s305054 - Trygve Vang
 */
public class GIFMakerS54 {

    private Board copiedBoard; //deep copy of board - TODO check if boundBox or not.
    byte[][] originaleArray;
    private int time; // 1000 ms = 1s - later, listener to a slider
    private int cellSize = 10;
    private int nPicturesLeft = 1; //number of pictures left - TODO nPictures cannot be less than 1
    private Color bgColor = Color.WHITE; //Standard color
    private Color cColor = Color.BLACK; //Standard color
    private GIFWriter gifWriter;
    private int gifWriterSize;

    /**
     * Set this gameboard as the board which is to be made a .gif from
     * @param originaleBoard Board to use for making the gif
     */
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

    /**
     * Specifies the cell size of the gif
     * @param cellSize The double value that is to be the cell size
     */
    public void setCellSize(int cellSize) {
        if (cellSize < 10) {
            return;
        } else {
            this.cellSize = cellSize;
        }
    }

    /**
     * Specifies hos long it will be between each frame in gif. The time is 
     * measured in ms (milliseconds)
     * @param time the time between each frame
     */
    public void setTime(double time) {
        this.time = (int) (time * 1000);
    }

    /**
     * Is used for setting both the background color of the gif, and the cellcolor
     * @param bgColor the backgroundcolor
     * @param cColor the cellcolor
     */
    public void setColor(Color bgColor, Color cColor) {
        if (bgColor != null) {
            this.bgColor = bgColor;
        }

        if (cColor != null) {
            this.cColor = cColor;
        }
    }
    
    /**
     * Used for assigning the amount of pictures that is to be used for the gif
     * @param nPictures number of pictures
     */
    public void setPictures(int nPictures) {
        if (nPictures < 1) {
            return;
        } else {
            this.nPicturesLeft = nPictures;
        }
    }

    /**
     * Initilizes the GIFWriter object
     * 
     * @param saveLocation the path for the file
     */
    public void prepareGIF(Path saveLocation) {
        try {
            gifWriterSize = (int) (copiedBoard.getArrayLength() * copiedBoard.getCellSize());
            gifWriter = new GIFWriter(gifWriterSize, gifWriterSize, saveLocation.toString(), this.time);
            gifWriter.setBackgroundColor(this.bgColor);
            gifWriter.flush(); //Flushing to set background color to the first image.

        } catch (IOException ex) {
            Logger.getLogger(GIFMakerS54.class.getName()).log(Level.SEVERE, null, ex);
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Something went wrong");
            alert.setHeaderText("There was an input/output error");
            alert.setContentText("Could not proceed. Please try again.");
            alert.showAndWait();
        }
    }

    /**
     * This method is used for creating a .gif file with a given number of generations
     * (pictures), with dimension y*x, and speed z. makeGif() uses recursion to
     * create pictures that is loaded into a gifstream. When there is one
     * picture left, it will create that last image, and close the stream.
     */
    public void makeGIF() {
        try {
            if (nPicturesLeft == 1) { //number of pictures left == 1
                for (int y = 0; y < copiedBoard.getArrayLength(); y++) {
                    for (int x = 0; x < copiedBoard.getArrayLength(y); x++) {
                        if (copiedBoard.getCellState(y, x)) {
                            if (y * cellSize + cellSize < gifWriterSize && x * cellSize + cellSize < gifWriterSize) {
                                gifWriter.fillRect(x * cellSize, x * cellSize + cellSize, y * cellSize, y * cellSize + cellSize, cColor);
                            }
                        }
                    }
                }

                gifWriter.insertAndProceed();
                gifWriter.close();
                System.out.println("Pictures left: " + nPicturesLeft + "\nDone.");
                //return the finished gif, ready to be exported            
            } else {

                for (int y = 0; y < copiedBoard.getArrayLength(); y++) {
                    for (int x = 0; x < copiedBoard.getArrayLength(y); x++) {
                        if (copiedBoard.getCellState(y, x) && (y * cellSize < gifWriterSize && y * cellSize + cellSize < gifWriterSize && x * cellSize < gifWriterSize && x * cellSize + cellSize < gifWriterSize)) {

                            gifWriter.fillRect(x * cellSize, (x * cellSize + cellSize), y * cellSize, (y * cellSize + cellSize), cColor);
                        }
                    }
                }
                gifWriter.insertAndProceed();

                copiedBoard.nextGen(); //calculates the next generation of the board.
                System.out.println("Pictures left: " + nPicturesLeft);
                nPicturesLeft -= 1;
                makeGIF(); //recursive call.
            }
        } catch (IOException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Something went wrong");
            alert.setHeaderText("There was an input/output error");
            alert.setContentText("Could not proceed. Please try again.");
            alert.showAndWait();
            System.err.println(e);
        }
    }
}
