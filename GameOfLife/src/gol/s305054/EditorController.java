/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.s305054;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
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
        
        mouseInit();
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

    private void mouseInit() {

        //Registers clicks on scene
        editorCanvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
                (MouseEvent e) -> {
                    handleMouseClick(e);
                });
        editorCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                (MouseEvent e) -> {
                    handleMouseClick(e);

                });
        /* Trenger jeg disse?
        editorCanvas.addEventHandler(MouseEvent.MOUSE_RELEASED,
                (MouseEvent e) -> {
                    if (rbMoveGrid.isSelected()) {
                        moveGridValues[2] = -Double.MAX_VALUE;
                        moveGridValues[3] = -Double.MAX_VALUE;
                    }
                });

        editorCanvas.addEventHandler(MouseEvent.MOUSE_MOVED,
                (MouseEvent e) -> {
                    mousePositionX = (int) e.getX();
                    mousePositionY = (int) e.getY();
                    if (boardFromFile != null) {

                        draw();
                        drawGhostTiles();
                    }
                }); */

        editorCanvas.setOnScroll((ScrollEvent event) -> {
            editorCanvas.requestFocus();
            if (event.getDeltaY() > 0) {
                zoomSlider.increment();
            } else {
                zoomSlider.decrement();
            }
        });
    }

    private void handleMouseClick(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        if (removeCell.isSelected()) {
            activeBoard.setCellState(y, x, false, 0, 0);
        } else if (moveGrid.isSelected()) {
        } else {
            activeBoard.setCellState(y, x, true, 0, 0);
        }

        draw();
    }
    
    public void setBoard(Board gameBoard) {
        
        //Denne må dobbelsjekkes. Vil jeg virkelig ha et like stort brett som det som spilles? Det er jo tross alt et dynamisk brett.
        int y = gameBoard.getArrayLength();
        int x = gameBoard.getArrayLength(0); //Brettet er et rektangel så jeg kan vel bruke første rad for å finne antall kolonner?
        activeBoard = new ArrayBoard(y,x);
        activeBoard.setCellSize(gameBoard.getCellSize());
        activeBoard.setGridSpacing(gameBoard.getGridSpacing());
    }

}
