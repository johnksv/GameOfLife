
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
        
        StringBuilder metaData = new StringBuilder();
        StringBuilder pattern = new StringBuilder();
        metaData.append("#N " + title.getText() + "\n#O " + author.getText() + "\n#C " + description.getText() + "\nx = " + x + ", y = " + y + "\n");
        
        //Have to find better solution than using both array and board.
        for (int i = 0; i < boundingBox.length; i++) {
            for (int j = 0; j < boundingBox[i].length; j++) {
                //Need to fix so that multiple cells next to each other (dead or alive) has number of that type of cell (example: 27o)
                if(boundingBox[i][j] == 0) {
                    pattern.append("b");
                } else if(boundingBox[i][j] == 64) {
                    pattern.append("o");
                }
                
                if(j == boundingBox[i].length && i == boundingBox.length) { //Last element in array ?
                    pattern.append("!");
                    break; 
                }
                
                if(j == boundingBox[i].length) { //Last element in row ?
                    pattern.append("$");
                }
                
            }            
        }
        /*
        [o]{2,} //Regex for å finne to eller flere o ved siden av seg - bruk ala searchand replace
        [b]{2,} //Regex for b
        [$]{2,} //Regex for ny linje
        
        if(numberOfO == boundingBox[0].length) {
            replace("");
        }
        */
        /*
         * A finished .rle file is looking like this:
        
            #N Name
            #O Author
            #C Comment
            x = How many columns, y = how many rows, rule = B(what number its born on)/S(what number it survives on)
            (actual pattern where b is dead, o is alive, $ is new line, and ! ends the pattern)
         */
        
        metaData.append(pattern).toString(); //Add pattern to metadata and make it into a string?
    }
}
