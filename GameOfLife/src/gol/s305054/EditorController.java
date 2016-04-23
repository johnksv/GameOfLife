/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.s305054;

import gol.model.Board.Board;
import gol.model.Board.DynamicBoard;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

/**
 * FXML Controller class
 *
 * @author Trygve Vang - s305054
 */
public class EditorController implements Initializable {
    
    @FXML
    private RadioButton addCell;
    @FXML
    private RadioButton removeCell;
    @FXML 
    private RadioButton moveGrid;
    @FXML
    private Slider zoomSlider;
    @FXML
    private Button savePatternBtn;
    @FXML
    private Button closeBtn;
    @FXML
    private TextField authorField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField ruleField;
    @FXML
    private Button updateStripBtn;
    @FXML
    private Canvas editorCanvas;
    
    private GraphicsContext gc;
    private final Color cellColor = Color.BLACK;
    private final Color backgroundColor = Color.web("#F4F4F4");
    private Board activeBoard;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gc = editorCanvas.getGraphicsContext2D();
    }

    @FXML
    private void handleZoom() {
        //TODO Skal jeg i det hele tatt gidde å tillate zoom på pattern editor?
    }
    
    //kopierte draw metode fra GameController, da det er nøyaktig samme greia som skjer.
    private void draw() {
        gc.setGlobalAlpha(1);
        gc.setFill(backgroundColor);
        gc.fillRect(0, 0, editorCanvas.getWidth(), editorCanvas.getHeight());
        gc.setFill(cellColor);
        for (int i = 1; i < activeBoard.getArrayLength(); i++) {
            if (editorCanvas.getHeight() < i * activeBoard.getCellSize() + i * activeBoard.getGridSpacing()) {
                //TODO Så den ikke tegner det som er utenfor
            }
            for (int j = 1; j < activeBoard.getArrayLength(i); j++) {
                if (activeBoard.getCellState(i, j)) {
                    if (editorCanvas.getWidth() < j * activeBoard.getCellSize() + j * activeBoard.getGridSpacing()) {
                        //TODO Så den ikke tegner det som er utenfor
                    }
                    gc.fillRect(j * activeBoard.getCellSize() + j * activeBoard.getGridSpacing(), //moveGridvalues[0]?
                            i * activeBoard.getCellSize() + i * activeBoard.getGridSpacing(), //moveGridValues[1]?
                            activeBoard.getCellSize(),
                            activeBoard.getCellSize());
                }
            }
        }

    }
    
}
