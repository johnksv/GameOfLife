package gol.s305084;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.stage.FileChooser;
import lieng.GIFWriter;

//TODO make close button
/**
 * Controller
 *
 * @author S305084
 */
public class PatternEditorController implements Initializable {

    //TODO Make junit tester
    @FXML
    private Canvas canvas;
    @FXML
    private Canvas theStrip;
    @FXML
    private RadioButton rbRemoveCell;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtAuthor;
    @FXML
    private TextField txtComment;

    private Board activeBoard;
    private GraphicsContext gc;
    private GraphicsContext gcStrip;
    private Color bgColor;
    private Color cellColor;

    @FXML
    private void handleClear() {
        activeBoard.clearBoard();
        draw();
        drawStrip();
    }

    @FXML
    private void handleGIF() {
        //neagtiv value means that there are no alive cells on the board
        if (activeBoard.getBoundingBox()[1] - activeBoard.getBoundingBox()[0] < 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Sorry, but you cant make a gif with no cells alive.");
            alert.showAndWait();
        } else {
            
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Game of Life Files", "*.gif"),
                    new FileChooser.ExtensionFilter("All Files", "*.*"));
            File selected = fileChooser.showSaveDialog(null);
            if (selected != null) {
                try {
                    java.awt.Color awtCellColor = new java.awt.Color((float) cellColor.getRed(), (float) cellColor.getGreen(), (float) cellColor.getBlue());
                    java.awt.Color awtBgColor = new java.awt.Color((float) bgColor.getRed(), (float) bgColor.getGreen(), (float) bgColor.getBlue());

                    GifMaker.makeGif(activeBoard.getBoundingBoxBoard(), new GIFWriter(140, 140, selected.toString(),
                            500), 140, 140, awtBgColor, awtCellColor, 20);

                } catch (IOException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
                    alert.setTitle("Error");
                    alert.setHeaderText("Sorry, something  went wrong during saving");
                    alert.showAndWait();
                }
            }
        }
    }

    @FXML
    private void handlebtnRLE() {
        //neagtiv value means that there are no alive cells on the board
        if (activeBoard.getBoundingBox()[1] - activeBoard.getBoundingBox()[0] < 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Sorry, but you cant make a file with no cells alive.");
            alert.showAndWait();
        } else {
            try {
                FileChooser fileChooser = new FileChooser();

                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Game of Life Files", "*.rle"),
                        new FileChooser.ExtensionFilter("All Files", "*.*"));

                File selected = fileChooser.showSaveDialog(null);
                if (selected != null) {
                    WriteRLE.toRLE(selected.toPath(), activeBoard, txtName.getText(), txtAuthor.getText(), txtComment.getText());
                }
            } catch (IOException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
                alert.setTitle("Error");
                alert.setHeaderText("Sorry, something  went wrong during saving \n please try again.");
                alert.showAndWait();
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gc = canvas.getGraphicsContext2D();
        gcStrip = theStrip.getGraphicsContext2D();
        activeBoard = new ArrayBoard(100, 100);

        activeBoard.setCellSize(15);
        activeBoard.setGridSpacing(0.6);

        mouseInit();
        drawStrip();
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
        draw();
        drawStrip();
    }

    public void setCellColor(Color cellColor) {
        this.cellColor = cellColor;
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
        drawStrip();
    }

    private void draw() {
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

    private void drawStrip() {
        byte[][] trimmedBoard = activeBoard.getBoundingBoxBoard();
        Board boardStrip = new ArrayBoard(trimmedBoard.length + 40, trimmedBoard[0].length + 40);
        boardStrip.insertArray(trimmedBoard, 20, 20);
        Affine xform = new Affine();

        double tx = 0;
        xform.setTx(tx);
        gcStrip.setTransform(xform);

        gcStrip.clearRect(0, 0, theStrip.getWidth(), theStrip.getHeight());
        gcStrip.setFill(bgColor);
        gcStrip.fillRect(0, 0, theStrip.getWidth(), theStrip.getHeight());
        gcStrip.setLineWidth(1);

        for (int i = 0; i < 20; i++) {

            boardStrip.nextGen();
            drawStripPart(boardStrip.getBoundingBoxBoard());
            tx += theStrip.getWidth() / 20;

            gcStrip.strokeLine(theStrip.getWidth() / 20, 0, theStrip.getWidth() / 20, theStrip.getHeight());

            xform.setTx(tx);
            gcStrip.setTransform(xform);
        }
        xform.setTx(0.0);
        gcStrip.setTransform(xform);

        // reset transform
    }

    private void drawStripPart(byte[][] pattern) {
        double cellSize;
        double xoffset = 0;
        double yoffset = 0;

        double height = theStrip.getHeight();
        double width = theStrip.getWidth() / 20;

        //Finding the rigth cellSize and finding the rigth offset
        //The offset sets the pattern in the middel of the width or heigth
        if (height / pattern.length < width / pattern[0].length) {
            cellSize = height / pattern.length;
            xoffset = width / 2 - (pattern[0].length * cellSize) / 2;
            if (xoffset < 0) {
                xoffset = 0;
            }
        } else {
            cellSize = width / pattern[0].length;
            yoffset = height / 2 - (pattern.length * cellSize) / 2;
            if (yoffset < 0) {
                yoffset = 0;
            }
        }

        //Calculating offset with if above max-cellSize
        if (cellSize > activeBoard.getCellSize()) {
            cellSize = activeBoard.getCellSize();
            yoffset = height / 2 - (pattern.length * cellSize) / 2;
            xoffset = width / 2 - (pattern[0].length * cellSize) / 2;
        }

        //Draw method
        gcStrip.setFill(cellColor);
        for (int i = 0; i < pattern.length; i++) {
            for (int j = 0; j < pattern[i].length; j++) {
                if (pattern[i][j] == 64) {

                    gcStrip.fillRect(j * cellSize + xoffset, i * cellSize + yoffset,
                            cellSize - cellSize * 0.05, cellSize - cellSize * 0.05);

                }
            }
        }
    }
}
