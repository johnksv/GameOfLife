/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.s305089.controller;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
import gol.s305089.UsefullMethods;
import gol.s305089.model.GifMaker;
import java.io.File;
import java.io.IOException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * FXML Controller class
 *
 * @author John Kasper
 */
public class EditorController implements Initializable {

    @FXML
    private BorderPane rootBorderPane;
    @FXML
    private Canvas canvas;
    @FXML
    private CheckBox chboxAutoUpdateStrip;
    @FXML
    private HBox canvasPreviewContainer;
    @FXML
    private RadioButton rbRemoveCell;

    private Board activeBoard;
    private GraphicsContext gc;
    private byte[][] byteBoard;
    private final List<ImageView> theStrip = new ArrayList<>();
    private GifMaker gifmaker;

    private Color cellColor = Color.BLACK;
    private Color backgroundColor = Color.web("#F4F4F4");
    int mousePositionX, mousePositionY;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gc = canvas.getGraphicsContext2D();
        canvas.widthProperty().bind(rootBorderPane.widthProperty());
        canvas.heightProperty().bind(rootBorderPane.heightProperty());

        initTheStrip();
        mouseInit();
    }

    private void initTheStrip() {
        for (int i = 0; i < 20; i++) {
            theStrip.add(i, new ImageView());
        }
        canvasPreviewContainer.getChildren().addAll(theStrip);
    }

    @FXML
    private void updateTheStrip() {

        for (int iteration = 0; iteration < 20; iteration++) {
            try {
                activeBoard.nextGen();

                File saveLocation = File.createTempFile("golTheStripPreview", ".gif");

                gifmaker = new GifMaker();
                gifmaker.setSaveLocation(saveLocation.getAbsolutePath());
                gifmaker.setPattern(activeBoard.getBoundingBoxBoard());
                gifmaker.writePatternToGIF(1);
                Image previewGif = new Image(saveLocation.toURI().toString());

                theStrip.get(iteration).setImage(previewGif);

                saveLocation.delete();

            } catch (IOException ex) {
                Logger.getLogger(EditorController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public void setActiveBoard(Board gameBoardToCopy) {
        byteBoard = gameBoardToCopy.getBoundingBoxBoard();

        int rows = (int) (UsefullMethods.getScreenSize()[1]
                / gameBoardToCopy.getCellSize());
        int columns = (int) (UsefullMethods.getScreenSize()[0]
                / gameBoardToCopy.getCellSize());

        activeBoard = new ArrayBoard(rows, columns);
        System.out.println("Størrelse: row: " + rows + ", kol: " + columns);
        this.activeBoard.setCellSize(gameBoardToCopy.getCellSize());

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

    private void mouseInit() {

        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
                (MouseEvent e) -> {
                    if (byteBoard != null) {
                        activeBoard.insertArray(byteBoard, (int) (mousePositionY / (activeBoard.getGridSpacing() + activeBoard.getCellSize())),
                                (int) (mousePositionX / (activeBoard.getGridSpacing() + activeBoard.getCellSize())));
                        byteBoard = null;

                        draw();
                    } else if (chboxAutoUpdateStrip.isSelected()) {
                        updateTheStrip();
                    }
                    handleMouseClick(e);
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
                        //TODO SUPPORT FOR OFFSET++
                        drawGhostTiles();
                    }
                });
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
