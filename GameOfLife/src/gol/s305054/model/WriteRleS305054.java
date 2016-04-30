
package gol.s305054.model;

import gol.model.Board.Board;
import java.io.IOException;
import javafx.scene.control.TextField;

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
     * @param title title of this pattern
     * @param author author of this pattern
     * @param description description of this pattern
     */
    public void writeRLE(Board boardToParse, TextField title, TextField author, TextField description) throws IOException {
        //TODO title, author, etc
        //TODO boundingboxBoard, parse rle from that
        boundingBox = boardToParse.getBoundingBoxBoard();
        
        //y and x (metadata)
        int y = boundingBox.length;
        int x = boardToParse.getArrayLength(0); 
        
        StringBuilder pattern = new StringBuilder();
        pattern.append("#N " + title.getText() + "\n#O " + author.getText() + "\n#C " + description.getText() + "\nx = " + x + ", y = " + y);
        /*
         * A finished .rle file is looking like this:
        
            #N Name
            #O Author
            #C Comment
            x = How many columns, y = how many rows, rule = B(what number its born on)/S(what number it survives on)
            (actual pattern where b is dead, o is alive, $ is new line, and ! ends the pattern)
         */
    }
}
