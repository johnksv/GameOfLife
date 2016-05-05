/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.vang.controller;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
import gol.model.Board.DynamicBoard;
import gol.other.Configuration;
import gol.vang.model.WriteRleS305054;
import java.io.File;
import java.io.IOException;
import java.net.URL;
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
import javafx.scene.transform.Affine;
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
    
    private Board activeBoard = new ArrayBoard();
    private Board stripBoard;
    
    byte[][] patternToInsert;
    byte[][] stripBoundingBox;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        activeBoard.setCellSize(Configuration.getPropInt("startSize"));
        activeBoard.setGridSpacing(activeBoard.getCellSize()*0.03);
        gc = editorCanvas.getGraphicsContext2D();
        stripGc = stripCanvas.getGraphicsContext2D();

        mouseInit();
        
    }
    
    //Directly copied from GameController
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

    /**
     * Refreshes the strip with an updated version of this board.
     * @see #drawStrip() 
     */
    public void updateStrip() {
        stripGc.clearRect(0, 0, stripCanvas.widthProperty().doubleValue(), stripCanvas.heightProperty().doubleValue());
        stripBoundingBox = activeBoard.getBoundingBoxBoard();
        stripBoard = new ArrayBoard();
        stripBoard.insertArray(stripBoundingBox, 1, 1);
        
        if(stripBoundingBox.length <= stripBoundingBox[0].length) {
            if(stripBoundingBox.length < 4) {
                stripBoard.setCellSize(12);
            } else {
                stripBoard.setCellSize(((stripCanvas.getWidth()/20)/stripBoundingBox.length)/2);
            }
        } else {
            if(stripBoundingBox[0].length < 4) {
                stripBoard.setCellSize(12);
            } else {
                stripBoard.setCellSize(((stripCanvas.getWidth()/20)/stripBoundingBox[0].length)/2);
            }
        }
        stripBoard.setGridSpacing(0.05);
        System.out.println(stripBoard.getCellSize() + "\n" + stripBoard.getGridSpacing());

        Affine xForm = new Affine();
        double tx = 0;
        //TODO sjekk hvorfor den ikke itererer riktig
        for (int iteration = 0; iteration < 20; iteration++) {
            tx = stripBoundingBox.length + iteration*(stripCanvas.getWidth() / 20);
            xForm.setTx(tx);

            stripGc.strokeLine(stripCanvas.getWidth()/20, 0, stripCanvas.getWidth()/20, stripCanvas.getHeight());
            stripGc.setTransform(xForm);
            
            stripBoard.nextGen();
            drawStrip();
            
            System.out.println("gen: "+iteration);
        }
        
        xForm.setTx(0.0);
        stripGc.setTransform(xForm);
    }
    
    //Same methods as in GameController, with support for updating the strip.
    private void mouseInit() {
        editorCanvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
                (MouseEvent e) -> {
                    handleMouseClick(e);
                });
        editorCanvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                (MouseEvent e) -> {
                    handleMouseClick(e);

                });

        editorCanvas.addEventHandler(MouseEvent.MOUSE_RELEASED,
                (MouseEvent) -> {
                    updateStrip();
                });

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
        } else {
            activeBoard.setCellState(y, x, true, 0, 0);
        }
        draw();
    }

    @FXML
    public void handleClearBtn() {
        activeBoard.clearBoard();
        stripBoard.clearBoard();
        draw();
        drawStrip();
    }
    
    @FXML
    private void handleSave() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("RLE", "*.rle"));
        fileChooser.setInitialFileName(titleField.getText());
        fileChooser.setTitle("Save Pattern");

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                WriteRleS305054 writer = new WriteRleS305054();
                writer.writeRLE(activeBoard, titleField, authorField, descriptionField, file.toPath());
            } catch (IOException ex) {
                Logger.getLogger(EditorController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @FXML
    private void handleZoom() {
        double x = zoomSlider.getValue();
        double newValue = 0.2 * Math.exp(0.05 * x);
        if (((newValue) * activeBoard.getArrayLength() > editorCanvas.getHeight()
                && (newValue) * activeBoard.getArrayLength(0) > editorCanvas.getWidth())) {

            activeBoard.setCellSize(newValue);
        } else {
            zoomSlider.setValue(20 * Math.log(5 * activeBoard.getCellSize()));
        }
        draw();
    }
    
    /**
     * Sends this gameboard to the pattern editor, and set the cell size
     * @param gameBoard Board to send
     */
    public void setBoard(Board gameBoard) {
        patternToInsert = gameBoard.getBoundingBoxBoard();
        activeBoard.insertArray(patternToInsert, 5, 5);

        draw();
    }

    

}
