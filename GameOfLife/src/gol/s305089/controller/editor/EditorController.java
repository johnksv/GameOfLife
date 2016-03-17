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
    private Board activeBoard;
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

        this.activeBoard = activeBoard;
        canvasController.setActiveBoard(activeBoard);
        canvasController.draw();
    }

}
