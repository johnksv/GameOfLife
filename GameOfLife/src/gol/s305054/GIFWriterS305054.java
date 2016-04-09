package gol.s305054;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
import java.io.IOException;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import lieng.GIFWriter;

/**
 * This class is used to create a .gif file showing several generations of a GoL
 * board.
 *
 * @author Trygve Vang - s305054
 */
public class GIFWriterS305054 {

    private Board copiedBoard; //deep copy of board - TODO check if boundBox or not.

    private int width = 100; //Width of .gif - Hardcoded value will be changed - rows
    private int height = 100; //Height of .gif - Hardcoded value will be changed - columns
    private int time = 1000; // 1000 ms = 1s - later, listener to a slider
    private short nPicturesLeft = 5; //number of pictures left - TODO nPictures cannot be less than 1
    private Color bgColor;
    private Color cColor;
    String path = "testGif.gif"; //Filepath - later, normal output stream

    private GIFWriter gifWriter;

    /**
     * Deep copy the active gameboard into a copied gameboard. When changing the
     * copied board, the originale board will stay the same.
     * @param originaleBoard Active board that is being displayed in the Game of Life GUI.
     * @param bgColor Background color of the board
     * @param cColor Color of cell in board.
     */
    public void prepareGIF(Board originaleBoard, Color bgColor, Color cColor) throws IOException { //TODO add parameters height width
        byte[][] originaleArray = originaleBoard.getBoundingBoxBoard();
        copiedBoard = new ArrayBoard(width, height);
        /*
         Param originaleBoard - get array, each element in originaleArray, assigned to copied array
         assigns copied array to copiedBoard.
         */
        copiedBoard.insertArray(originaleArray, 1, 1); //Get boundingBox and insert it to an empty.
        this.bgColor = bgColor;
        this.cColor = cColor;
        
        gifWriter = new GIFWriter(width, height, path, time);
        
    }
    /**
     * This method is used for creating a .gif file with x generations (pictures), with dimension ixj, and speed y.
     * makeGif() uses recursion to create pictures that is loaded into a gifstream. when there is one picture left, it will
     * create that last image, and close the stream.
     */
    public void makeGIF() { //TODO method should be changed to propely work recursion wise
        try {
            if (nPicturesLeft == 1) { //number of pictures left == 1
                gifWriter.flush();
                gifWriter.createNextImage();
                gifWriter.insertCurrentImage();
                gifWriter.close();
                //return the finished gif, ready to be exported            
            } else {
                gifWriter.createNextImage(); //creates the image
                gifWriter.insertCurrentImage(); //insert the image to gifStream?

                copiedBoard.nextGen(); //calculates the next generation of the board.
                nPicturesLeft -= 1;
                makeGIF(); //recursive call.
            }
        }  catch (IOException e) { //TODO catch stackoverflow error
            e.printStackTrace();
            
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Something went wrong");
            alert.setHeaderText("There was an input/output error");
            alert.setContentText("Could not proceed. Please try again.");
            alert.showAndWait();
        }
    }

}
