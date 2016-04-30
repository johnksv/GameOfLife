
package gol.s305054.model;

import gol.model.Board.Board;
import java.io.IOException;

/**
 * @author Trygve Vang - s305054
 *
 */
public class WriteRleS305054 {
    private byte[][] boundingBox;
    //TODO support for lagring til RLE fil
    
    /**
     * Parses this board to a RLE file.
     * @param boardToParse a playable board
     */
    public void writeRLE(Board boardToParse) throws IOException {
        //TODO title, author, etc
        //TODO boundingboxBoard, parse rle from that
        boundingBox = boardToParse.getBoundingBoxBoard();
        
        //y and x (metadata)
        int y = boundingBox.length;
        int x = boardToParse.getArrayLength(0); 
        StringBuilder pattern = new StringBuilder();
    }
}
