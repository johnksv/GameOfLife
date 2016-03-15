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
 * This class has all the methods related to the canvas
 *
 * @author s305054, s305084, s305089
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gc = canvas.getGraphicsContext2D();
        mouseInit();
    }

    /**
     * Initiates all relevant mouseevents.
     */
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
        moveGridValues[0] = -(activeBoard.getArrayLength() * activeBoard.getCellSize() * activeBoard.getGridSpacing()) / 2;
        moveGridValues[1] = -(activeBoard.getArrayLength() * activeBoard.getCellSize() * activeBoard.getGridSpacing()) / 2;
    }

    void drawGrid() {
        gc.setFill(Color.BLUE);
        //TODO Så den ikke tegner det som er utenfor
        double spacing = activeBoard.getCellSize() + activeBoard.getGridSpacing();
        for (int i = 0; i < activeBoard.getArrayLength(); i++) {
            gc.strokeLine(i * spacing, 0, i * spacing, canvas.getHeight());

            for (int j = 0; j < activeBoard.getArrayLength(i); j++) {

                gc.strokeLine(0, j * spacing, canvas.getWidth(), j * spacing);

            }
        }

    }

    public void draw() {
        if (isinitialized) {
            gc.setFill(backgroundColor);
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.setFill(cellColor);
            for (int i = 1; i < activeBoard.getArrayLength(); i++) {
                if (canvas.getHeight() < i * activeBoard.getCellSize() + i * activeBoard.getGridSpacing()) {
                    //TODO Så den ikke tegner det som er utenfor
                }
                for (int j = 1; j < activeBoard.getArrayLength(i); j++) {
                    if (activeBoard.getCellState(i, j)) {
                        if (canvas.getWidth() < j * activeBoard.getCellSize() + j * activeBoard.getGridSpacing()) {
                            //TODO Så den ikke tegner det som er utenfor
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

    //Over complicated for the sake of smoothness, this code may have huge potensial for improvement. 
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

    /**
     *
     * @param e
     */
    private void handleMouseClick(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        if (rbRemoveCell.isSelected()) {
            activeBoard.setCellState(y, x, false, moveGridValues[0], moveGridValues[1]);
        } else if (rbMoveGrid.isSelected()) {
        } else {
            activeBoard.setCellState(y, x, true, moveGridValues[0], moveGridValues[1]);

        }
        draw();
    }

    //Does not calc gridspacing yet.
    public void calcNewOffset(double cellSize, double newCellSize) {
        double gridSpace=activeBoard.getGridSpacing();
        if (cellSize != 0) {
            
            double oldx = (canvas.getWidth() / 2 - moveGridValues[0]) / (cellSize );
            double oldy = (canvas.getHeight() / 2 - moveGridValues[1]) / (cellSize);

            moveGridValues[0] = -(oldx * (newCellSize ) - canvas.getWidth() / 2);
            moveGridValues[1] = -(oldy * (newCellSize ) - canvas.getHeight() / 2);

            moveGridValues[0] = (moveGridValues[0] > 0) ? 0 : moveGridValues[0];
            moveGridValues[1] = (moveGridValues[1] > 0) ? 0 : moveGridValues[1];

        }

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

}
