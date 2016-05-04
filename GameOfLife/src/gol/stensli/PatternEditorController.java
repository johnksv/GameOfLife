package gol.stensli;

import gol.model.UsefullMethods;
import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
import gol.model.Board.DynamicBoard;
import gol.other.Configuration;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lieng.GIFWriter;

/**
 * Pattern editor, for creating small and complex patterns. Implements much of
 * the same code as {@link gol.controller.GameController gamController}. Note
 * that the only change from the main game is {@link #drawStrip() the strip},
 * and saving a pattern to GIF or RLE.
 *
 * @author S305084
 */
public class PatternEditorController implements Initializable {

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
    private Color bgColor = Color.GRAY;
    private Color cellColor = Color.BLACK;

    private final int GIFW;
    private final int GIFH;

    public PatternEditorController() {
        //Should never return -1
        if (Configuration.getPropInt("gifWidth") == -1) {
            GIFW = 200;
        } else {
            GIFW = Configuration.getPropInt("gifWidth");
        }
        if (Configuration.getPropInt("gifHeight") == -1) {
            GIFH = 200;
        } else {
            GIFH = Configuration.getPropInt("gifHeight");
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

    @FXML
    private void handleClear() {
        activeBoard.clearBoard();
        draw();
        drawStrip();
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) canvas.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleGIF() {
        //neagtiv value means that there are no alive cells on the board
        if (activeBoard.getBoundingBox()[1] - activeBoard.getBoundingBox()[0] < 0) {
            UsefullMethods.showErrorAlert("Board is empty.", "Sorry, but you cant make a gif with no living cells.");
        } else {

            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Gif format", "*.gif"),
                    new FileChooser.ExtensionFilter("All Files", "*.*"));
            File selected = fileChooser.showSaveDialog(null);
            if (selected != null) {
                try {
                    java.awt.Color awtCellColor = new java.awt.Color((float) cellColor.getRed(), (float) cellColor.getGreen(), (float) cellColor.getBlue());
                    java.awt.Color awtBgColor = new java.awt.Color((float) bgColor.getRed(), (float) bgColor.getGreen(), (float) bgColor.getBlue());

                    GifMaker.makeGif(activeBoard, new GIFWriter(GIFW, GIFH, selected.toString(),
                            500), GIFW, GIFH, awtBgColor, awtCellColor, 20);

                } catch (IOException ex) {
                    UsefullMethods.showErrorAlert("Oops!", "Something  went wrong during saving");
                }
            }
        }
    }

    @FXML
    private void handlebtnRLE() {
        //neagtiv value means that there are no alive cells on the board
        if (activeBoard.getBoundingBox()[1] - activeBoard.getBoundingBox()[0] < 0) {
            UsefullMethods.showErrorAlert("Board is empty.", "Sorry, but you cant make a gif with no living cells.");
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
                UsefullMethods.showErrorAlert("Sorry!", "Something went wrong during saving \n please try again.");

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

    /**
     * Sets a new bgColor value. <br> <b>Default color:</b> Gray.
     *
     * @param bgColor Background Color.
     */
    public void setBGColor(Color bgColor) {
        this.bgColor = bgColor;

        gc.setFill(bgColor);
        draw();
        drawStrip();
    }

    /**
     * Sets a new cellColor value. <br> <b>Default color:</b> Black.
     *
     * @param cellColor Cell Color.
     */
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
        Board boardStrip = new DynamicBoard();
        boardStrip.insertArray(trimmedBoard);
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
        // resets transform
        xform.setTx(0.0);
        gcStrip.setTransform(xform);

    }

    /**
     * Returns the pattern, null if board is empty.
     *
     * @return byte pattern
     */
    public byte[][] getPattern() {
        if (activeBoard.getBoundingBox()[1] - activeBoard.getBoundingBox()[0] < 0) {
            return null;
        }
        return activeBoard.getBoundingBoxBoard();
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
