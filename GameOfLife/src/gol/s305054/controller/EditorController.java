/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.s305054.controller;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
import gol.s305054.model.WriteRleS305054;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.stage.FileChooser;

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
    private TextField titleField;
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
    @FXML
    private Canvas stripCanvas;

    private GraphicsContext gc;
    private GraphicsContext stripGc;
    private final Color cellColor = Color.BLACK;
    private final Color backgroundColor = Color.web("#F4F4F4");
    private Board activeBoard = new ArrayBoard(150,150);
    private Board stripBoard;
    byte[][] patternToInsert;
    byte[][] patternStrip;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gc = editorCanvas.getGraphicsContext2D();
        stripGc = stripCanvas.getGraphicsContext2D();
        activeBoard.setCellSize(15);
        activeBoard.setGridSpacing(0.8);

        mouseInit();
        stripParts();
    }

    @FXML
    private void handleZoom() {
        double x = zoomSlider.getValue();
        double newValue = 0.2 * Math.exp(0.05 * x);
        if (((newValue) * activeBoard.getArrayLength() > editorCanvas.getHeight()
                && (newValue) * activeBoard.getArrayLength(0) > editorCanvas.getWidth())) {

            activeBoard.setCellSize(newValue);
            activeBoard.setGridSpacing(newValue * 0.053333333);
        } else {
            zoomSlider.setValue(20 * Math.log(5 * activeBoard.getCellSize()));
        }

        draw();
    }

    //kopierte draw metode fra GameController, da det er nøyaktig samme greia som skjer.
    private void draw() {
        gc.setGlobalAlpha(1);
        gc.setFill(backgroundColor);
        gc.fillRect(0, 0, editorCanvas.getWidth(), editorCanvas.getHeight());
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
    
    private void drawStrip() {
        stripGc.setGlobalAlpha(1);
        stripGc.setFill(backgroundColor);
        stripGc.fillRect(0, 0, stripCanvas.getWidth(), stripCanvas.getHeight());
        stripGc.setFill(cellColor);
        for (int i = 1; i < stripBoard.getArrayLength(); i++) {
            for (int j = 1; j < stripBoard.getArrayLength(i); j++) {
                if (stripBoard.getCellState(i, j)) {
                    stripGc.fillRect(j * stripBoard.getCellSize() + j * stripBoard.getGridSpacing(),
                            i * stripBoard.getCellSize() + i * stripBoard.getGridSpacing(),
                            stripBoard.getCellSize(),
                            stripBoard.getCellSize());
                }
            }
        }

    }
    
    private void stripParts() {
        //TODO height, width, sånn shit. Vil ha 20 patterns på en canvas. Affain klasse som er nøkkelordet
        stripBoard = new ArrayBoard();
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
        }  else {
            activeBoard.setCellState(y, x, true, 0, 0);
        }
        draw();
    }

    public void setBoard(Board gameBoard) {
        patternToInsert = gameBoard.getBoundingBoxBoard();
        activeBoard.insertArray(patternToInsert, 5, 5);
        activeBoard.setCellSize(gameBoard.getCellSize());
        activeBoard.setGridSpacing(gameBoard.getGridSpacing());
        draw();
    }
    
    @FXML
    private void handleSave() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Pattern");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("RLE", ".rle"));
        
        File file = fileChooser.showSaveDialog(null); //Stage?
        if(file != null) {
            try {
                WriteRleS305054 writer = new WriteRleS305054();
                writer.writeRLE(activeBoard, titleField, authorField, descriptionField, file.toPath());
            } catch (IOException ex) {
                Logger.getLogger(EditorController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
