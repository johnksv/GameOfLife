/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.s305089.controller;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
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
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * FXML Controller class
 *
 * @author John Kasper
 */
public class EditorController implements Initializable {

    @FXML
    private Canvas canvas;
    @FXML
    private CheckBox chboxAutoUpdateStrip;
    @FXML
    private HBox canvasPreviewContainer;
    @FXML
    private RadioButton rbRemoveCell;
    @FXML
    private RadioButton rbMoveGrid;

    private Board gameboard;
    private GraphicsContext gc;
    private byte[][] pattern;
    private final List<ImageView> theStrip = new ArrayList<>();
    private GifMaker gifmaker;
    private final double[] moveGridValues = {0, 0, -Double.MAX_VALUE, -Double.MAX_VALUE};

    private Color cellColor = Color.BLACK;
    private Color backgroundColor = Color.web("#F4F4F4");

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gc = canvas.getGraphicsContext2D();
        System.out.println(gc); 
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

    public void setActiveBoard(Board gameboard) {
        byte[][] activeByteBoard = gameboard.getBoundingBoxBoard();
        setPattern(activeByteBoard);
        gameboard.setCellSize(gameboard.getCellSize());
        draw();
    }

    /**
     * Constructs an new Board instance, and inserts this board
     *
     * @see gol.model.Board.Board#insertArray(byte[][], int, int)
     * @param Pattern the pattern to set
     */
    public void setPattern(byte[][] Pattern) {
        //TODO dynaimc size of board
        gameboard = new ArrayBoard(5, 5);
        this.pattern = Pattern;
        gameboard.insertArray(pattern, 1, 1);
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
                    System.out.println("pressed");
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
    }

    private void handleMouseClick(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        if (rbRemoveCell.isSelected()) {
            gameboard.setCellState(y, x, false, moveGridValues[0], moveGridValues[1]);
        } else if (rbMoveGrid.isSelected()) {
        } else {
            gameboard.setCellState(y, x, true, moveGridValues[0], moveGridValues[1]);

        }
        draw();
    }

    private void moveGrid(MouseEvent e) {
        if (moveGridValues[2] == -Double.MAX_VALUE && moveGridValues[3] == -Double.MAX_VALUE) {
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
}
