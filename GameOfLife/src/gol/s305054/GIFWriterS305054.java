package gol.s305054;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
import lieng.GIFWriter;



/**
 * This class is used to create a .gif file showing several generations of a GoL board.
 * @author Trygve Vang - s305054
 */
public class GIFWriterS305054 {
    private Board copiedBoard; //deep copy of board - TODO check if boundBox or not.
    
    int width = 100; //Width of .gif - Hardcoded value will be changed
    int height = 100; //Height of .gif - Hardcoded value will be changed
    int time = 1000; // 1000 ms = 1s - later, listener to a slider
    String path = "testGif.gif"; //Filepath - later, normal output stream
    
    private GIFWriter gifWriter;
    
    public void setCopiedBoard(Board originaleBoard) {
        /*
        Param originaleBoard - get array, each element in originaleArray, assigned to copied array
        assigns copied array to copiedBoard.
        */
        byte[][] originaleArray = originaleBoard.getBoundingBoxBoard();
        for (int i = 0; i < originaleBoard.getArrayLength(); i++) {
            for (int j = 0; j < originaleBoard.getArrayLength(i); j++) {
                            }
        }
    }
    public void makeGif() { //TODO method should be changed to propely work recursion wise
        if() { //number of pictures left == 1
            gifWriter.insertAndProceed();
            gifWriter.close();
            //return the finished gif, ready to be exported            
        } else {
            gifWriter.createNextImage(); //creates the image
            gifWriter.insertAndProceed(); //insert the image to gifStream?
            
            copiedBoard.nextGen(); //calculates the next generation of the board.
            //number of pictures left -=1;
            makeGif(); //recursive call.
        }
    }
    
}
