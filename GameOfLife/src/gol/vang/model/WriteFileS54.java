package gol.vang.model;

import gol.model.Board.Board;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;

/**
 * @author s305054 - Trygve Vang
 *
 */
public class WriteFileS54 {
    
    public WriteFileS54() {
    }

    private byte[][] boundingBox;
    //TODO support for lagring til RLE fil

    /**
     * Parses this board to a RLE file using a buffered writer. This method
     * parses the boundingbox of a board into a playable .rle-file.
     * 
     * <p> Below is an overview of the different elements that could be found in
     * a .rle file
     * </p>
     * <table>
     *      <tr>
     *          <td>Symbol</td>
     *          <td>Meaning</td>
     *      </tr>
     *      <tr>
     *          <td>#N</td>
     *          <td>Title of pattern</td>
     *      </tr>
     *      <tr>
     *          <td>#O</td>
     *          <td>Author</td>
     *      </tr>
     *      <tr>
     *           <td>#C</td>
     *           <td>Comment</td>
     *      </tr>
     *      <tr>
     *          <td>b</td>
     *          <td>"Alive" cell</td>
     *      </tr>
     *      <tr>
     *          <td>o</td>
     *          <td>"Dead" cell</td>
     *      </tr>
     *      <tr>
     *          <td>$</td>
     *          <td>New line</td>
     *      </tr>
     *      <tr>
     *          <td>!</td>
     *          <td>End of Pattern</td>
     *      </tr>
     *      <tr><td columnspan="2">Note that a number befor either a b, or an o
     *      is the equivalent of writing that number of b's or o's. E.g. 3o = ooo
     *      </td</tr>
     * </table>
     *
     * @param boardToParse a playable board
     * @param title title of this pattern
     * @param author author of this pattern
     * @param description description of this pattern
     * @param sLocation path to where the file is to be saved
     * @throws IOException if there is an input/output error
     */
    public void writeRLE(Board boardToParse, TextField title, TextField author, TextField description, Path sLocation) throws IOException {

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
