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
import javafx.stage.Screen;

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

    private Board gameboard;
    private GraphicsContext gc;
    private byte[][] pattern;
    private final List<ImageView> theStrip = new ArrayList<>();
    private GifMaker gifmaker;

    private Color cellColor = Color.BLACK;
    private Color backgroundColor = Color.web("#F4F4F4");

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
                gameboard.nextGen();

                File saveLocation = File.createTempFile("golTheStripPreview", ".gif");

                gifmaker = new GifMaker();
                gifmaker.setSaveLocation(saveLocation.getAbsolutePath());
                gifmaker.setPattern(gameboard.getBoundingBoxBoard());
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
        byte[][] activeByteBoard = gameBoardToCopy.getBoundingBoxBoard();

        int rows = (int) (UsefullMethods.getScreenSize()[0]
                / gameBoardToCopy.getCellSize() + gameBoardToCopy.getGridSpacing());
        int columns = (int) (UsefullMethods.getScreenSize()[1]
                / gameBoardToCopy.getCellSize() + gameBoardToCopy.getGridSpacing());

        gameboard = new ArrayBoard(columns, rows);
        this.pattern = activeByteBoard;
        gameboard.insertArray(pattern, 1, 1);
        this.gameboard.setCellSize(gameBoardToCopy.getCellSize());
        draw();
    }

    private void draw() {

        gc.setFill(backgroundColor);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(cellColor);
        for (int i = 1; i < gameboard.getArrayLength(); i++) {
            if (canvas.getHeight() < i * gameboard.getCellSize() + i * gameboard.getGridSpacing()) {
                //TODO Så den ikke tegner det som er utenfor
            }
            for (int j = 1; j < gameboard.getArrayLength(i); j++) {
                if (gameboard.getCellState(i, j)) {
                    if (canvas.getWidth() < j * gameboard.getCellSize() + j * gameboard.getGridSpacing()) {
                        //TODO Så den ikke tegner det som er utenfor
                    }
                    gc.fillRect(j * gameboard.getCellSize() + j * gameboard.getGridSpacing(),
                            i * gameboard.getCellSize() + i * gameboard.getGridSpacing(),
                            gameboard.getCellSize(),
                            gameboard.getCellSize());
                }
            }
        }
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

    private void handleMouseClick(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        if (rbRemoveCell.isSelected()) {
            gameboard.setCellState(y, x, false, 0, 0);
        } else {
            gameboard.setCellState(y, x, true, 0, 0);

        }
        draw();
    }
}
