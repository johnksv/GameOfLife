package gol.svergja.controller;

import gol.controller.GameController;
import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
import gol.model.Board.DynamicBoard;
import gol.svergja.Util;
import gol.svergja.model.WriteFile;
import java.io.File;
import java.io.IOException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * FXML Controller class for pattern editor.
 *
 * @author s305089 - John Kasper Svergja
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
    private Label labelWriteFileFdBck;
    @FXML
    private TextField tfName;
    @FXML
    private TextField tfAuthor;
    @FXML
    private TextField tfDescription;

    private Board activeBoard;
    private Board theStripBoard;
    private GraphicsContext gc;
    private byte[][] ghostByteBoard;
    private GameController gameController;

    private final Color cellColor = Color.BLACK;
    private final Color backgroundColor;
    private int mousePositionX, mousePositionY;
    private final List<GraphicsContext> theStripGC = new ArrayList<>();
    private final double theStripOffset = 20;

    /**
     *
     */
    public PatternEditorController() {
        this.backgroundColor = Color.web("#F4F4F4");
    }

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

            //The canvas/board you click, should become the active on screen.
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

    //Copied from GameController, with modifications, for autoupdate of the strip
    private void mouseInit() {
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
                (MouseEvent e) -> {
                    if (ghostByteBoard != null) {
                        activeBoard.insertArray(ghostByteBoard, (int) (mousePositionY / (activeBoard.getGridSpacing() + activeBoard.getCellSize())),
                                (int) (mousePositionX / (activeBoard.getGridSpacing() + activeBoard.getCellSize())));
                        ghostByteBoard = null;
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
                    if (ghostByteBoard != null) {
                        mousePositionX = (int) e.getX();
                        mousePositionY = (int) e.getY();
                        draw();
                        drawGhostTiles();
                    }
                });
    }

    /**
     * Draws one generation of the strip on the given GC
     */
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

    //Copy from gameController
    private void draw() {
        gc.setGlobalAlpha(1);
        gc.setFill(backgroundColor);
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

    //Copy from gameController
    private void drawGhostTiles() {
        if (ghostByteBoard != null) {
            gc.setFill(cellColor);
            for (int j = 0; j < ghostByteBoard.length; j++) {
                for (int i = 0; i < ghostByteBoard[j].length; i++) {
                    if (ghostByteBoard[j][i] == 64) {
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

    //Copied from gamecontroller, with modifications.
    private void handleMouseClick(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        if (e.isSecondaryButtonDown()) {
            activeBoard.setCellState(y, x, false, 0, 0);
        } else {
            activeBoard.setCellState(y, x, true, 0, 0);
        }
        draw();
    }

    @FXML
    private void handleClearBtn() {
        activeBoard.clearBoard();
        draw();
    }

    @FXML
    private void handleMakeWav() {
        try {

            Stage golAudio = new Stage();
            FXMLLoader root = new FXMLLoader(getClass().getResource("/gol/svergja/view/WavMaker.fxml"));
            Scene scene = new Scene((Parent) root.load());

            WavMakerController soundController = root.<WavMakerController>getController();
            soundController.setBoard(activeBoard);

            golAudio.setScene(scene);
            golAudio.setTitle("Wav maker - Game of Life");
            golAudio.initOwner(rootBorderPane.getScene().getWindow());
            golAudio.showAndWait();
        } catch (IOException ex) {
            System.err.println("An error occurred...:\n" + ex);
        }

    }

    @FXML
    private void handleShowStats() {
        try {
            Stage golStats = new Stage();
            FXMLLoader root = new FXMLLoader(getClass().getResource("/gol/svergja/view/Stats.fxml"));

            Scene scene = new Scene((Parent) root.load());

            StatsController statsController = root.<StatsController>getController();
            statsController.setBoard(activeBoard);

            golStats.setScene(scene);
            golStats.setTitle("Stats - Game of Life");
            golStats.initOwner(rootBorderPane.getScene().getWindow());
            golStats.show();
        } catch (IOException ex) {
            System.err.println("An error occurred...:\n" + ex);
        }
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
            filechooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("RLE-format", "*.rle"));
            filechooser.setInitialFileName(tfName.getText());
            File file = filechooser.showSaveDialog(null);
            if (file != null) {
                fileSucsessCreated = WriteFile.writeToRLE(activeBoard.getBoundingBoxBoard(), file.toPath());
            }
        }
        if (fileSucsessCreated) {

            labelWriteFileFdBck.setText("Success");
            labelWriteFileFdBck.setStyle("-fx-text-fill: #8BA870;");
        } else {

            labelWriteFileFdBck.setText("Failed.. Try again");
            labelWriteFileFdBck.setStyle("-fx-text-fill: #D8000C;");
        }
    }

    @FXML
    private void sendCurrentBoard() {
        if (gameController != null) {
            Board board = new DynamicBoard();
            board.insertArray(activeBoard.getBoundingBoxBoard());
            gameController.setActiveBoard(board);
        }
    }

    @FXML
    private void saveAsGIF() throws IOException {
        Stage gifMaker = new Stage();
        FXMLLoader root = new FXMLLoader(getClass().getResource("/gol/svergja/view/GifMaker.fxml"));

        Scene scene = new Scene((Parent) root.load());

        GifMakerController gifcontroller = root.<GifMakerController>getController();
        gifcontroller.setBoard(activeBoard);

        gifMaker.setScene(scene);
        gifMaker.setTitle("Generate GIF - Game of Life");
        gifMaker.initModality(Modality.APPLICATION_MODAL);
        gifMaker.setWidth(215);
        gifMaker.setHeight(535);
        gifMaker.show();

    }

    @FXML
    private void updateTheStrip() {
        byte[][] patternToDraw = activeBoard.getBoundingBoxBoard();

        //TODO for strip to be dynamic after size
        theStripBoard = new ArrayBoard(100, 100);
        theStripBoard.setRule(activeBoard.getRule());
        theStripBoard.insertArray(patternToDraw, 10, 10);
        initTheStrip();
        theStripBoard.clearBoard();
        theStripBoard.insertArray(patternToDraw, 10, 10);

        for (int iteration = 0; iteration <= 20; iteration++) {
            drawTheStrip(theStripGC.get(iteration));
            theStripBoard.nextGen();
        }
    }

    /**
     * Constructs an new Board instance, and inserts the given board bounding
     * box pattern.
     * <b>Technical info:</b> The board constructed is of type array board. This
     * is due to the early implementation of the pattern editor.
     *
     * @param boardToSet The board that should be copied. Copies the pattern and
     * rule
     */
    public void setBoard(Board boardToSet) {
        ghostByteBoard = boardToSet.getBoundingBoxBoard();

        int rows = (int) (Util.getScreenSize()[1]
                / boardToSet.getCellSize());
        int columns = (int) (Util.getScreenSize()[0]
                / boardToSet.getCellSize());

        activeBoard = new ArrayBoard(rows, columns);
        activeBoard.setRule(boardToSet.getRule());
        this.activeBoard.setCellSize(boardToSet.getCellSize());
    }

    /**
     *
     * @param gameController
     */
    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }
}
