package gol.s305054.model;

import gol.model.Board.Board;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;

/**
 * @author Trygve Vang - s305054
 *
 */
public class WriteRleS305054 {
    
    public WriteRleS305054() {
    }

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
    public void writeRLE(Board boardToParse, TextField title, TextField author, TextField description, Path sLocation) throws IOException {
        //TODO title, author, etc
        //TODO boundingboxBoard, parse rle from that

        if (boardToParse.getArrayLength() == 0) {
            Alert error = new Alert(AlertType.ERROR);
            error.setContentText("Can't create a pattern from an empty gameboard.");
            return;
        }

        boundingBox = boardToParse.getBoundingBoxBoard();

        //y and x (metadata)
        int y = boundingBox.length;
        int x = boundingBox[0].length;
        int counter = 0;

        BufferedWriter metaData = Files.newBufferedWriter(sLocation);
        StringBuilder pattern = new StringBuilder();
        StringBuilder formatedPattern = new StringBuilder();

        if (title.getText() != null && !title.equals("") && !title.equals(" ")) {
            metaData.append("#N " + title.getText());
        } else {
            metaData.append("#N Unknown");
        }

        if (author.getText() != null && !author.equals("") && !author.equals(" ")) {
            metaData.append("\n#O " + author.getText());
        } else {
            metaData.append("\n#O Unknown");
        }

        if (description.getText() != null && !description.equals("") && !description.equals(" ")) {
            metaData.append("\n#C " + description.getText());
        } 

        metaData.append("\nx = " + x + ", y = " + y + "\n");

        for (int row = 0; row < boundingBox.length; row++) {
            for (int column = 0; column < boundingBox[row].length; column++) {
                if (boundingBox[row][column] == 0) {
                    pattern.append("b");
                } 
                
                if (boundingBox[row][column] == 64) {
                    pattern.append("o");
                }

                if (column == boundingBox[0].length-1 && row == boundingBox.length-1) { 
                    pattern.append("!");
                    break;
                }

                if (column == boundingBox[row].length-1) { 
                    pattern.append("$");
                }

            }
        }
        
        for (int index = 0; index < pattern.length(); index++) {
            if (pattern.charAt(index) == '!') {
                formatedPattern.append("!");
                break;
            }
            if (pattern.charAt(index) == pattern.charAt(index + 1)) {
                counter++;
            }
            if (pattern.charAt(index) != pattern.charAt(index + 1)) {
                if (counter > 0) {
                    counter++;
                    formatedPattern.append(counter);
                    counter = 0;
                }
                formatedPattern.append(pattern.charAt(index));
            }
            if (counter == (x - 1) && pattern.charAt(index) == 'b') {
                //TODO fjern linjer som er helt tom.
            }

        }
        System.out.println(formatedPattern);
        metaData.append(formatedPattern); //Add pattern to metadata and make it into a string?
        metaData.close();

    }
}
