/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.controller;

import gol.controller.patternEditor.EditorController;
import gol.model.Board.Board;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 * FXML Controller class
 *
 * @author John Kasper
 */
public class CanvasController implements Initializable {

    @FXML
    private Canvas canvas;
    private GraphicsContext gc;
    private Board activeBoard;
    private Color backgroundColor = Color.web("#F4F4F4");
    private Color cellColor = Color.BLACK;
    private boolean isInitialized = false;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gc = canvas.getGraphicsContext2D();
        mouseInit();
    }

    public void setActiveBoard(Board activeBoard) {
        this.activeBoard = activeBoard;
        System.out.println(activeBoard);
        isInitialized = true;
        draw();
    }

    //MouseEvent
    private void mouseInit() {
        //Registers clicks on scene
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
                (MouseEvent e) -> {
                    handleMouseClick(e);
                });
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                (MouseEvent e) -> {
                    handleMouseClick(e);
                });
    }

    private void handleMouseClick(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();
        activeBoard.setCellState(x, y, true);
        draw();
    }

    public void draw() {
        if (isInitialized) {
            gc.setFill(backgroundColor);
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.setFill(cellColor);
            for (int i = 1; i < activeBoard.getArrayLength(); i++) {
                if (canvas.getHeight() < i * activeBoard.getCellSize() + i * activeBoard.getGridSpacing()) {
                    break;
                }
                for (int j = 1; j < activeBoard.getArrayLength(i); j++) {
                    if (activeBoard.getCellState(i, j)) {
                        if (canvas.getWidth() < j * activeBoard.getCellSize() + j * activeBoard.getGridSpacing()) {
                            break;
                        }
                        gc.fillRect(j * activeBoard.getCellSize() + j * activeBoard.getGridSpacing(),
                                i * activeBoard.getCellSize() + i * activeBoard.getGridSpacing(),
                                activeBoard.getCellSize(),
                                activeBoard.getCellSize());
                    }
                }
            }
        }
    }

    /**
     * @param backgroundColor the backgroundColor to set
     */
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * @param cellColor the cellColor to set
     */
    public void setCellColor(Color cellColor) {
        this.cellColor = cellColor;
    }

}
