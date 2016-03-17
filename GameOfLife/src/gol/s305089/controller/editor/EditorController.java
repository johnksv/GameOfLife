/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.s305089.controller.editor;

import gol.controller.CanvasController;
import gol.controller.GameController;
import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

/**
 * FXML Controller class
 *
 * @author John Kasper
 */
public class EditorController implements Initializable {

    @FXML
    private CanvasController canvasController;
    private Board activeBoard;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("EDITOR INIT");
    }

    public void setActiveBoard(Board activeBoard) {

        this.activeBoard = activeBoard;
        canvasController.setActiveBoard(activeBoard);
        canvasController.draw();
    }

}
