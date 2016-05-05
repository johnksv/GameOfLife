package gol.vang.controller;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
import gol.other.Configuration;
import gol.vang.model.WriteFileS54;
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
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Controlls the Pattern Editor, like editing a pattern, showing its 20 next
 * generations, and saving that pattern to a .rle file. EditorController imports
 * the pattern from {@link gol.controller.GameController GameController}, and
 * put it into this main Canvas.
 *
 * @author s305054 - Trygve Vang
 */
public class EditorController implements Initializable {

    @FXML
    private RadioButton addCell;
    @FXML
    private RadioButton removeCell;
    @FXML
    private RadioButton moveGrid;
    @FXML
    private Button savePatternBtn;
    @FXML
    private Button btnClose;
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

    private Board activeBoard = new ArrayBoard(Configuration.getPropInt("arrayLength"), Configuration.getPropInt("arrayLength"));
    private Board stripBoard;

    byte[][] patternToInsert;
    byte[][] stripBoundingBox;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        activeBoard.setCellSize(Configuration.getPropInt("startSize"));
        activeBoard.setGridSpacing(activeBoard.getCellSize() * 0.03);
        gc = editorCanvas.getGraphicsContext2D();
        stripGc = stripCanvas.getGraphicsContext2D();

        mouseInit();

    }

    /**
     * Finished pattern from PatternEditor.
     *
     * @return boundingbox of the finished pattern
     */
    public byte[][] sendPattern() {
        if (activeBoard.getArrayLength() == 0) {
            return null;
        } else {
            return activeBoard.getBoundingBoxBoard();
        }
    }

    //Copied from GameController
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
     *
     * @see #drawStrip()
     */
    public void updateStrip() {
        stripGc.clearRect(0, 0, stripCanvas.widthProperty().doubleValue(), stripCanvas.heightProperty().doubleValue());
        stripBoundingBox = activeBoard.getBoundingBoxBoard();

        stripBoard = new ArrayBoard();
        stripBoard.insertArray(stripBoundingBox, 1, 1);

        if (stripBoundingBox.length <= stripBoundingBox[0].length) {
            if (stripBoundingBox.length < 4) {
                stripBoard.setCellSize(12);
            } else {
                stripBoard.setCellSize(((stripCanvas.getWidth() / 20) / stripBoundingBox.length) / 2);
            }
        } else if (stripBoundingBox[0].length < 4) {
            stripBoard.setCellSize(12);
        } else {
            stripBoard.setCellSize(((stripCanvas.getWidth() / 20) / stripBoundingBox[0].length) / 2);
        }
        stripBoard.setGridSpacing(0.05);

        Affine xForm = new Affine();
        double tx = 0;
        //TODO sjekk hvorfor den ikke itererer riktig
        for (int iteration = 0; iteration < 20; iteration++) {
            tx = stripBoundingBox.length + iteration * (stripCanvas.getWidth() / 20);
            xForm.setTx(tx);

            stripGc.strokeLine(stripCanvas.getWidth() / 20, 0, stripCanvas.getWidth() / 20, stripCanvas.getHeight());
            stripGc.setTransform(xForm);

            stripBoard.nextGen();
            drawStrip();

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

    }

    //Copied from GameController
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

    /**
     * Closes the pattern editor.
     */
    @FXML
    public void handleClose() {
            Stage stage = (Stage) btnClose.getScene().getWindow();
            stage.close();
    }

    /**
     * Sets all elements in this active board to 0. See the
     * {@link gol.model.Board.Board#clearBoard clearBoard} method.
     */
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
                WriteFileS54 writer = new WriteFileS54();
                writer.writeRLE(activeBoard, titleField, authorField, descriptionField, file.toPath());
            } catch (IOException ex) {
                Logger.getLogger(EditorController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Sends this gameboard to the pattern editor, and set the cell size
     *
     * @param gameBoard Board to send
     */
    public void setBoard(Board gameBoard) {
        patternToInsert = gameBoard.getBoundingBoxBoard();
        activeBoard.insertArray(patternToInsert, 5, 5);

        draw();
    }

}
