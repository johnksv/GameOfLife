/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.s305089.controller;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
import gol.s305089.UsefullMethods;
import gol.s305089.model.WriteFile;
import java.io.File;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.stage.FileChooser;

/**
 * FXML Controller class
 *
 * @author John Kasper
 */
public class PatternEditorController implements Initializable {

    @FXML
    private BorderPane rootBorderPane;
    @FXML
    private Canvas canvas;
    @FXML
    private CheckBox chboxAutoUpdateStrip;
    @FXML
    private Canvas theStripCanvas;
    @FXML
    private RadioButton rbRemoveCell;
    @FXML
    private Label labelWriteFileFdBck;

    private Board activeBoard;
    private GraphicsContext gc;
    private byte[][] byteBoard;
    private GraphicsContext theStripGc;

    private Color cellColor = Color.BLACK;
    private Color backgroundColor = Color.web("#F4F4F4");
    int mousePositionX, mousePositionY;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gc = canvas.getGraphicsContext2D();
        theStripGc = theStripCanvas.getGraphicsContext2D();

        canvas.widthProperty().bind(rootBorderPane.widthProperty());
        canvas.heightProperty().bind(rootBorderPane.heightProperty());

        mouseInit();
    }

    private void mouseInit() {
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
                (MouseEvent e) -> {
                    if (byteBoard != null) {
                        activeBoard.insertArray(byteBoard, (int) (mousePositionY / (activeBoard.getGridSpacing() + activeBoard.getCellSize())),
                                (int) (mousePositionX / (activeBoard.getGridSpacing() + activeBoard.getCellSize())));
                        byteBoard = null;
                        draw();
                    } else {
                        handleMouseClick(e);
                    }
                    if (chboxAutoUpdateStrip.isSelected()) {
                        updateTheStrip();
                    }
                });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                (MouseEvent e) -> {
                    handleMouseClick(e);
                });

        canvas.addEventFilter(MouseEvent.MOUSE_RELEASED, (MouseEvent e) -> {
            if (chboxAutoUpdateStrip.isSelected()) {
                updateTheStrip();
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_MOVED,
                (MouseEvent e) -> {
                    if (byteBoard != null) {
                        mousePositionX = (int) e.getX();
                        mousePositionY = (int) e.getY();
                        draw();
                        drawGhostTiles();
                    }
                });
    }

    @FXML
    private void handleClearBtn() {
        activeBoard.clearBoard();
    }

    @FXML
    private void savePatternRLE() {
        boolean fileSucsessCreated = false;
        byte[][] boardToWrite = activeBoard.getBoundingBoxBoard();
        if (boardToWrite[0].length != 0) {
            FileChooser filechooser = new FileChooser();
            File file = filechooser.showSaveDialog(null);
            if (file != null) {
                fileSucsessCreated = WriteFile.writeToRLE(activeBoard.getBoundingBoxBoard(), file.toPath());
            }
        }
        labelWriteFileFdBck.setText(fileSucsessCreated ? "Success" : "Failed.. Try again");
    }

    @FXML
    private void updateTheStrip() {
        Board tempBoard = activeBoard;
        theStripCanvas.setWidth(5000);
        theStripGc.clearRect(0, 0, theStripCanvas.widthProperty().get(), theStripCanvas.heightProperty().get());
        Affine xform = new Affine();
        double tx = 10;
        double lastTx = 0;

        //TODO FIX BUG: First two is drawn on top of each other
        for (int iteration = 0; iteration < 20; iteration++) {

            byte[][] tempByteBoard = tempBoard.getBoundingBoxBoard();
            int longestRow = 0;

            for (byte[] row : tempByteBoard) {
                if (row.length > longestRow) {
                    longestRow = row.length;
                }
            }
            tempBoard = new ArrayBoard(tempByteBoard.length + 4, longestRow + 4);
            tempBoard.insertArray(tempByteBoard, 2, 2);
            tempBoard.setCellSize(10);
            tempBoard.nextGen();

            drawTheStrip(tempBoard);

            theStripGc.setTransform(xform);
            lastTx = longestRow * tempBoard.getCellSize() + 50;
            tx += lastTx;
            xform.setTx(tx);
        }
        theStripCanvas.setWidth(tx - lastTx);
        xform.setTx(0.0);
        theStripGc.setTransform(xform);

    }

    public void setActiveBoard(Board gameBoardToCopy) {
        byteBoard = gameBoardToCopy.getBoundingBoxBoard();

        int rows = (int) (UsefullMethods.getScreenSize()[1]
                / gameBoardToCopy.getCellSize());
        int columns = (int) (UsefullMethods.getScreenSize()[0]
                / gameBoardToCopy.getCellSize());

        activeBoard = new ArrayBoard(rows, columns);
        this.activeBoard.setCellSize(gameBoardToCopy.getCellSize());

    }

    private void drawTheStrip(Board boardToDraw) {
        theStripGc.setFill(cellColor);
        for (int i = 1; i < boardToDraw.getArrayLength(); i++) {
            for (int j = 1; j < boardToDraw.getArrayLength(i); j++) {
                if (boardToDraw.getCellState(i, j)) {
                    theStripGc.fillRect(j * boardToDraw.getCellSize(),
                            i * boardToDraw.getCellSize(),
                            boardToDraw.getCellSize(),
                            boardToDraw.getCellSize());
                }
            }
        }
    }

    private void draw() {
        gc.setGlobalAlpha(1);
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
                    gc.fillRect(j * activeBoard.getCellSize() + j * activeBoard.getGridSpacing(),
                            i * activeBoard.getCellSize() + i * activeBoard.getGridSpacing(),
                            activeBoard.getCellSize(),
                            activeBoard.getCellSize());
                }
            }
        }
    }

    private void drawGhostTiles() {
        if (byteBoard != null) {
            gc.setFill(cellColor);
            for (int j = 0; j < byteBoard.length; j++) {
                for (int i = 0; i < byteBoard[j].length; i++) {
                    if (byteBoard[j][i] == 64) {
                        gc.setGlobalAlpha(1);
                        gc.setFill(backgroundColor);
                        gc.fillRect(mousePositionX + i * activeBoard.getCellSize() + i * activeBoard.getGridSpacing(),
                                mousePositionY + j * activeBoard.getCellSize() + j * activeBoard.getGridSpacing(),
                                activeBoard.getCellSize(),
                                activeBoard.getCellSize());
                        gc.setFill(cellColor);

                        gc.setGlobalAlpha(0.5);
                        gc.fillRect(mousePositionX + i * activeBoard.getCellSize() + i * activeBoard.getGridSpacing(),
                                mousePositionY + j * activeBoard.getCellSize() + j * activeBoard.getGridSpacing(),
                                activeBoard.getCellSize(),
                                activeBoard.getCellSize());
                    }
                }
            }
        }
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
}
