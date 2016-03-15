/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.controller;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.RadioButton;
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
    private Board activeBoard = new ArrayBoard(10, 10);
    private Color cellColor = Color.BLACK;
    private Color backgroundColor = Color.web("#F4F4F4");
    private final double[] moveGridValues = {0, 0, -1, -1}; //Offset x, offset y, old x, old y
    private RadioButton rbRemoveCell = new RadioButton();
    private RadioButton rbMoveGrid = new RadioButton();

    private boolean isinitialized = false;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gc = canvas.getGraphicsContext2D();
        mouseInit();
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
                    if (rbMoveGrid.isSelected()) {
                        moveGrid(e);
                    } else {
                        handleMouseClick(e);
                    }
                });
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED,
                (MouseEvent e) -> {
                    if (rbMoveGrid.isSelected()) {
                        moveGridValues[2] = -1;
                        moveGridValues[3] = -1;
                    }
                });

        //offset
        //Stian's proposition that is somewhat bugged if you zoom, but the zoom needs to be fixed any way.
//        moveGridValues[0] = -(activeBoard.getArrayLength() * activeBoard.getCellSize() * activeBoard.getGridSpacing()) / 2;
//        moveGridValues[1] = -(activeBoard.getArrayLength() * activeBoard.getCellSize() * activeBoard.getGridSpacing()) / 2;
    }

    public void draw() {
        if (isinitialized) {
            gc.setFill(backgroundColor);
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.setFill(cellColor);
            for (int i = 1; i < activeBoard.getArrayLength(); i++) {
                if (canvas.getHeight() < i * activeBoard.getCellSize() + i * activeBoard.getGridSpacing()) {

                }
                for (int j = 1; j < activeBoard.getArrayLength(i); j++) {
                    if (activeBoard.getCellState(i, j)) {
                        if (canvas.getWidth() < j * activeBoard.getCellSize() + j * activeBoard.getGridSpacing()) {

                        }
                        gc.fillRect(j * activeBoard.getCellSize() + j * activeBoard.getGridSpacing() + moveGridValues[0],
                                i * activeBoard.getCellSize() + i * activeBoard.getGridSpacing() + moveGridValues[1],
                                activeBoard.getCellSize(),
                                activeBoard.getCellSize());
                    }
                }
            }
        }

    }

    //Over complicated for the sake of smoothness, this code may have huge potensial for improment. 

    private void moveGrid(MouseEvent e) {
        if (moveGridValues[2] < 0) {
            moveGridValues[2] = e.getX();
            moveGridValues[3] = e.getY();
        } else {
            if (moveGridValues[0] + e.getX() - moveGridValues[2] < 0) {
                if (moveGridValues[1] + e.getY() - moveGridValues[3] < 0) {

                    moveGridValues[0] += e.getX() - moveGridValues[2]; //Offset x = x position - old y
                    moveGridValues[1] += e.getY() - moveGridValues[3]; //Offset y = y position - old y
                } else {
                    moveGridValues[1] = 0;
                    moveGridValues[0] += e.getX() - moveGridValues[2]; //Offset x = x position - old y
                }
            } else {
                moveGridValues[0] = 0;
                if (moveGridValues[1] + e.getY() - moveGridValues[3] < 0) {
                    moveGridValues[1] += e.getY() - moveGridValues[3]; //Offset y = y position - old y
                }

            }
            moveGridValues[2] = e.getX();
            moveGridValues[3] = e.getY();
        }

        draw();

    }

    private void handleMouseClick(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        if (rbRemoveCell.isSelected()) {
            activeBoard.setCellState(y, x, false, moveGridValues[0], moveGridValues[1]);
        } else if (rbMoveGrid.isSelected()) {
            System.out.println("Moving gird ");
        } else {
            activeBoard.setCellState(y, x, true, moveGridValues[0], moveGridValues[1]);

        }
        draw();
    }

    /**
     * @param activeBoard the activeBoard to set
     */
    public void setActiveBoard(Board activeBoard) {
        this.activeBoard = activeBoard;
        isinitialized = true;
    }

    /**
     * @param cellColor the cellColor to set
     */
    public void setCellColor(Color cellColor) {
        this.cellColor = cellColor;
    }

    /**
     * @param backgroundColor the backgroundColor to set
     */
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * @param rbRemoveCell the rbRemoveCell to set
     */
    public void setRbRemoveCell(RadioButton rbRemoveCell) {
        this.rbRemoveCell = rbRemoveCell;
    }

    /**
     * @param rbMoveGrid the rbMoveGrid to set
     */
    public void setRbMoveGrid(RadioButton rbMoveGrid) {
        this.rbMoveGrid = rbMoveGrid;
    }

    //Does not calc gridspacing yet.

    public void calcNewOffset(double cellSize, double newCellSize) {
        if (cellSize != 0) {
            moveGridValues[0] = (moveGridValues[0] / cellSize) * newCellSize;
            moveGridValues[1] = (moveGridValues[1] / cellSize) * newCellSize;
        }
    }

}
