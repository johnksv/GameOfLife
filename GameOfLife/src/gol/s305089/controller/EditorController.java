/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.s305089.controller;

import gol.controller.CanvasController;
import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
import gol.s305089.model.GifMaker;
import java.io.File;
import java.io.IOException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * FXML Controller class
 *
 * @author John Kasper
 */
public class EditorController implements Initializable {

    @FXML
    private CanvasController canvasController;
    @FXML
    private CheckBox chboxAutoUpdateStrip;
    @FXML
    private HBox canvasPreviewContainer;
    @FXML
    private RadioButton rbRemoveCell;
    @FXML
    private RadioButton rbMoveGrid;

    private Board gameboard;
    private byte[][] pattern;
    private final List<ImageView> theStrip = new ArrayList<>();
    private GifMaker gifmaker;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initTheStrip();
        canvasController.setRbRemoveCell(rbRemoveCell);
        canvasController.setRbMoveGrid(rbMoveGrid);

    }

    private void initTheStrip() {
        for (int i = 0; i < 20; i++) {
            theStrip.add(i, new ImageView());
        }
        canvasPreviewContainer.getChildren().addAll(theStrip);
    }

    @FXML
    private void updateTheStrip() {

        for (int iteration = 0; iteration < 20; iteration++) {
            try {
                gameboard.nextGen();
                
                File saveLocation = File.createTempFile("golTheStripPreview", ".gif");

                gifmaker = new GifMaker();
                gifmaker.setSaveLocation(saveLocation.getAbsolutePath());
                gifmaker.setPattern(gameboard.getBoundingBoxBoard());
                gifmaker.writePatternToGIF(1);
                Image previewGif = new Image(saveLocation.toURI().toString());
                
                
                theStrip.get(iteration).setImage(previewGif);
                
                saveLocation.delete();

            } catch (IOException ex) {
                Logger.getLogger(EditorController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void setActiveBoard(Board activeBoard) {
        byte[][] activeByteBoard = activeBoard.getBoundingBoxBoard();
        setPattern(activeByteBoard);
        gameboard.setCellSize(activeBoard.getCellSize());

        canvasController.setActiveBoard(gameboard);
        canvasController.draw();
    }

    /**
     * Constructs an new Board instance, and inserts this board
     *
     * @see gol.model.Board.Board#insertArray(byte[][], int, int)
     * @param Pattern the pattern to set
     */
    public void setPattern(byte[][] Pattern) {
        //TODO dynaimc size of board
        gameboard = new ArrayBoard(100, 100);
        this.pattern = Pattern;
        gameboard.insertArray(pattern, 1, 1);
    }

}
