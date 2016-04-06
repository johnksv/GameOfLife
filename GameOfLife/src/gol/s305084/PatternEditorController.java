package gol.s305084;

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
 * Controller
 *
 * @author S305084
 */
public class PatternEditorController implements Initializable {
    
    @FXML
    private Canvas canvas;
    @FXML
    private RadioButton rbRemoveCell;
    
    private Board activeBoard;
    private GraphicsContext gc;
    private Color bgColor;
    private Color cellColor;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gc = canvas.getGraphicsContext2D();
        activeBoard = new ArrayBoard(150, 150);
        
        activeBoard.setCellSize(10);
        activeBoard.setGridSpacing(0.5);
        mouseInit();
    }
    
    private void mouseInit() {
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
                (MouseEvent e) -> {
                    handleMouseClick(e);
                    
                });
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                (MouseEvent e) -> {
                    handleMouseClick(e);
                    
                });
    }
    
    public void setBGColor(Color bgColor) {
        this.bgColor = bgColor;
        
        gc.setFill(bgColor);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
    
    public void setCellColor(Color cellColor) {
        this.cellColor = cellColor;
        
        gc.setFill(bgColor);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
    
    private void handleMouseClick(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();
        
        if (rbRemoveCell.isSelected()) {
            activeBoard.setCellState(y, x, false, 0, 0);
        } else {
            activeBoard.setCellState(y, x, true, 0, 0);
        }
        draw();
    }
    
    private void draw() {
        gc.setGlobalAlpha(1);
        gc.setFill(bgColor);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(cellColor);
        
        for (int i = 1; i < activeBoard.getArrayLength(); i++) {
            for (int j = 1; j < activeBoard.getArrayLength(i); j++) {
                if (activeBoard.getCellState(i, j)) {
                    gc.fillRect(j * activeBoard.getCellSize() + j * activeBoard.getGridSpacing(),
                            i * activeBoard.getCellSize() + i * activeBoard.getGridSpacing(),
                            activeBoard.getCellSize(),
                            activeBoard.getCellSize());
                }
            }
        }
    }
    
}
