package gol.s305054.model;

import gol.model.Board.Board;
import java.io.IOException;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
     *
     * @param boardToParse a playable board
     * @param title title of this pattern
     * @param author author of this pattern
     * @param description description of this pattern
     */
    public void writeRLE(Board boardToParse, TextField title, TextField author, TextField description) {
        //TODO title, author, etc
        //TODO boundingboxBoard, parse rle from that
        
        if(boardToParse.getArrayLength() == 0) {
            Alert error = new Alert(AlertType.ERROR);
            error.setContentText("Can't create a pattern from an empty gameboard.");
            return;
        }
        
        boundingBox = boardToParse.getBoundingBoxBoard();
        
        //y and x (metadata)
        int y = boundingBox.length;
        int x = boundingBox[0].length;
        int counter = 0;

        StringBuilder metaData = new StringBuilder();
        StringBuilder pattern = new StringBuilder();
        metaData.append("#N " + title.getText() + "\n#O " + author.getText() + "\n#C " + description.getText() + "\nx = " + x + ", y = " + y + "\n");

        for (int i = 0; i < boundingBox.length; i++) {
            for (int j = 0; j < boundingBox[i].length; j++) {
                //Save last value, and compare next value to last value. If the same counter ++. keeps going until not the same. One last counter++, set counter before the value, and counter = 0
                if (boundingBox[i][j] == 0) {
                    pattern.append("b");
                } else if (boundingBox[i][j] == 64) {
                    pattern.append("o");
                }

                if (j == boundingBox[i].length && i == boundingBox.length) { //Last element in array ?
                    pattern.append("!");
                    break;
                }

                if (j == boundingBox[i].length) { //Last element in row ?
                    pattern.append("$");
                }

            }
        }
        System.out.println(pattern); //Sjekke før parsing
        for (int index = 0; index < pattern.length(); index++) {
            if(pattern.charAt(index) == '!') {
                break;
            }
            if(pattern.charAt(index) == pattern.charAt(index + 1)) {
                counter++;
            }
            if(pattern.charAt(index) != pattern.charAt(index + 1)) {
                if(counter > 0) {
                    pattern.replace((index-counter), index, String.valueOf((counter+1) + pattern.charAt(index)));
                    //TODO slik at hele døde linjer blir gjort om til ""
                }
            }
            
        }
        System.out.println(pattern); //Sjekke etter parsing

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
