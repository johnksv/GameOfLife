/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.s305089.controller;

import gol.controller.CanvasController;
import gol.controller.GameController;
import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * FXML Controller class
 *
 * @author John Kasper
 */
public class EditorController implements Initializable {

    @FXML
    private CanvasController canvasController;
    @FXML
    private HBox canvasContainer;
    private Board gameboard;
    private byte[][] pattern;
    private final List<Canvas> theStrip = new ArrayList<>();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initTheStrip();

    }

    private void initTheStrip() {
        for (int i = 0; i < 20; i++) {
            theStrip.add(new Canvas(50, 50));
        }
        canvasContainer.getChildren().addAll(theStrip);
    }

    public void setActiveBoard(Board activeBoard) {
        byte[][] activeByteBoard = activeBoard.getBoundingBoxBoard();
        setPattern(activeByteBoard);
        
        //TODO FIx bug, canvas not showing
        canvasController.setActiveBoard(gameboard);
        canvasController.draw();
        System.out.println(gameboard);
        
    }
    
        /**
     * Constructs an new Board instance, and inserts this board
     * @see gol.model.Board.Board#insertArray(byte[][], int, int) 
     * @param Pattern the pattern to set
     */
    public void setPattern(byte[][] Pattern) {
        //TODO dynaimc size of board
        gameboard = new ArrayBoard(10, 10);
        this.pattern = Pattern;
        gameboard.insertArray(pattern, 2, 2);
    }

}
