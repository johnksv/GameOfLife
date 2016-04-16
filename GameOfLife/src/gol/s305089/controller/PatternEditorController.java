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
import java.io.IOException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
    private HBox theStripCanvasContainer;
    @FXML
    private RadioButton rbRemoveCell;
    @FXML
    private Label labelWriteFileFdBck;
    @FXML
    private TextField tfName;
    @FXML
    private TextField tfAuthor;
    @FXML
    private TextField tfDescription;
    @FXML
    private TextField tfRules;
    

    private Board activeBoard;
    private Board theStripBoard;
    private GraphicsContext gc;
    private byte[][] byteBoard;

    private final Color cellColor = Color.BLACK;
    private final Color backgroundColor;
    private int mousePositionX, mousePositionY;
    private final List<GraphicsContext> theStripGC = new ArrayList<>();
    private double theStripOffset = 20;

    public PatternEditorController() {
        this.backgroundColor = Color.web("#F4F4F4");
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        canvas.widthProperty().bind(rootBorderPane.widthProperty());
        canvas.heightProperty().bind(rootBorderPane.heightProperty());
        gc = canvas.getGraphicsContext2D();

        mouseInit();
    }

    private void initTheStrip() {
        theStripCanvasContainer.getChildren().clear();
        theStripGC.clear();
        double cellSize = theStripBoard.getCellSize();
        for (int i = 0; i <= 20; i++) {
            byte[][] boundingBox = theStripBoard.getBoundingBoxBoard();
            Canvas striptCanvas = new Canvas(cellSize * boundingBox[0].length + theStripOffset, cellSize * boundingBox.length + theStripOffset);
            theStripCanvasContainer.getChildren().add(striptCanvas);
            theStripGC.add(striptCanvas.getGraphicsContext2D());
            
            striptCanvas.setOnMousePressed((MouseEvent e) -> {
                Canvas picked = (Canvas) e.getPickResult().getIntersectedNode();
                if (theStripGC.contains(picked.getGraphicsContext2D())) {
                    int position = theStripGC.indexOf(picked.getGraphicsContext2D());
                    for (int iterations = 0; iterations < position; iterations++) {
                        activeBoard.nextGen();
                    }
                    updateTheStrip();
                    draw();
                }
            });
            
            theStripBoard.nextGen();
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
        draw();
    }

    @FXML
    private void savePatternRLE() {
        boolean fileSucsessCreated = false;
        byte[][] boardToWrite = activeBoard.getBoundingBoxBoard();
        if (boardToWrite[0].length != 0) {
            WriteFile.setPatternName(tfName.getText());
            WriteFile.setAuthor(tfAuthor.getText());
            WriteFile.setComment(tfDescription.getText());
            
            FileChooser filechooser = new FileChooser();
            filechooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("RLE-format", ".rle"));
            filechooser.setInitialFileName(tfName.getText());
            File file = filechooser.showSaveDialog(null);
            if (file != null) {
                fileSucsessCreated = WriteFile.writeToRLE(activeBoard.getBoundingBoxBoard(), file.toPath());
            }
        }
        labelWriteFileFdBck.setText(fileSucsessCreated ? "Success" : "Failed.. Try again");
    }

    @FXML
    private void updateTheStrip() {
        byte[][] patternToDraw = activeBoard.getBoundingBoxBoard();

        //TODO for strip to be dynamic after size
        //int[] largestDimension = UsefullMethods.calculateBiggestDimension(patternToDraw, 20);
        theStripBoard = new ArrayBoard(100, 100);
        theStripBoard.insertArray(patternToDraw, 10, 10);
        initTheStrip();
        theStripBoard.clearBoard();
        theStripBoard.insertArray(patternToDraw, 10, 10);

        for (int iteration = 0; iteration <= 20; iteration++) {
            drawTheStrip(theStripGC.get(iteration));
            theStripBoard.nextGen();
        }
    }
    
    @FXML
    private void saveAsGIF() throws IOException{
        Stage gifMaker = new Stage();
        FXMLLoader root = new FXMLLoader(getClass().getResource("/gol/s305089/view/GifMaker.fxml"));

        Scene scene = new Scene((Parent) root.load());

        GifMakerController gifcontroller = root.<GifMakerController>getController();
        gifcontroller.setByteBoard(activeBoard);

        gifMaker.setScene(scene);
        gifMaker.setTitle("Generate GIF - Game of Life");
        gifMaker.initModality(Modality.APPLICATION_MODAL);
        gifMaker.setMaxHeight(600.00);
        gifMaker.show();
        
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

    private void drawTheStrip(GraphicsContext stripGC) {
        stripGC.setGlobalAlpha(1);
        stripGC.setFill(backgroundColor);
        stripGC.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        stripGC.setFill(cellColor);
        byte[][] boundedPattern = theStripBoard.getBoundingBoxBoard();
        int longestRow = 0;
        double cellSize = theStripBoard.getCellSize();
        for (int i = 0; i < boundedPattern.length; i++) {
            longestRow = boundedPattern[i].length > longestRow ? boundedPattern[i].length : longestRow;

            for (int j = 0; j < boundedPattern[i].length; j++) {
                if (boundedPattern[i][j] == 64) {
                    stripGC.fillRect(j * cellSize + theStripOffset, i * cellSize + theStripOffset, cellSize, cellSize);
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
