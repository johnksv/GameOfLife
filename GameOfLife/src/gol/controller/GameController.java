package gol.controller;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
import gol.model.FileIO.PatternFormatException;
import gol.model.FileIO.ReadFile;
import gol.model.Logic.ConwaysRule;
import gol.model.Logic.CustomRule;
import gol.model.Logic.unsupportedRuleException;
import gol.s305089.controller.EditorController;
import gol.s305089.model.GifMaker;
import gol.s305089.controller.GifMakerController;
import gol.s305089.controller.StatsController;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * @author s305054, s305084, s305089
 */
public class GameController implements Initializable {

    @FXML
    private Canvas canvas;

    @FXML
    private Slider cellSizeSlider;
    @FXML
    private Slider gridSpacingSlider;
    @FXML
    private Slider animationSpeedSlider;
    @FXML
    private Label animationSpeedLabel;
    @FXML
    private Button startPauseBtn;
    @FXML
    private ColorPicker cellCP;
    @FXML
    private ColorPicker backgroundCP;
    @FXML
    private RadioButton rbRemoveCell;
    @FXML
    private RadioButton rbMoveGrid;
    @FXML
    private ToggleGroup tgGameRules;
    @FXML
    private RadioButton rbCustomGameRules;
    @FXML
    private TextField tfCellsToSpawn;
    @FXML
    private TextField tfCellsToLive;
    @FXML
    private Button btnUseRule;
    @FXML
    private CheckBox cbShowGrid;
    //TODO Show grid is not working yet. Implement it

    private Board activeBoard;
    private final Timeline timeline = new Timeline();
    private GraphicsContext gc;

    private Color cellColor = Color.BLACK;
    private Color backgroundColor = Color.web("#F4F4F4");
    private byte[][] boardFromFile;
    //Offset x, offset y, old x, old y
    private final double[] moveGridValues = {0, 0, -Double.MAX_VALUE, -Double.MAX_VALUE};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Init");
        gc = canvas.getGraphicsContext2D();

        activeBoard = new ArrayBoard();
        cellCP.setValue(Color.BLACK);
        backgroundCP.setValue(Color.web("#F4F4F4"));
        //TODO bug if used like this with new zoom!
        mouseInit();
        handleZoom();
        handleGridSpacingSlider();
        handleColor();
        handleZoom();
        handleAnimationSpeedSlider();
        initAnimation();
        initGameRulesListner();

    }

    private void initGameRulesListner() {
        tgGameRules.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) -> {
            if (newValue == rbCustomGameRules) {
                tfCellsToLive.setDisable(false);
                tfCellsToSpawn.setDisable(false);
                btnUseRule.setDisable(false);
                handleRuleBtn();
            } else {
                tfCellsToLive.setDisable(true);
                tfCellsToSpawn.setDisable(true);
                btnUseRule.setDisable(true);
                activeBoard.setGameRule(new ConwaysRule());
            }
        });
    }

    private void initAnimation() {
        Duration duration = Duration.millis(1000);
        KeyFrame keyframe = new KeyFrame(duration, (ActionEvent e) -> {
            activeBoard.nextGen();
            draw();
        });
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.getKeyFrames().add(keyframe);

    }

    @FXML
    public void handleAnimation() {

        if (timeline.getStatus() == Animation.Status.RUNNING) {
            timeline.pause();
            startPauseBtn.setText("Start game");
        } else {
            timeline.play();
            startPauseBtn.setText("Pause game");
        }
    }

    @FXML
    public void handleAnimationSpeedSlider() {
        double animationSpeed = animationSpeedSlider.getValue();
        timeline.setRate(animationSpeed);
        animationSpeedLabel.setText(String.format("%.2f %s", animationSpeed, " "));
    }

    @FXML
    public void handleRuleText() {
        //TODO

    }

    @FXML
    public void handleRuleBtn() {
        byte[] toSpawn;
        byte[] toLive;
        System.out.println(tfCellsToLive.getText());
        if (tfCellsToLive.getText().replaceAll("\\D", "").equals("")) {
            tfCellsToLive.setText("");
            toLive = new byte[]{-1};
        } else {
            String[] toLiveString = tfCellsToLive.getText().replaceAll("\\D", "").split("");

            toLive = new byte[toLiveString.length];
            for (int i = 0; i < toLive.length; i++) {
                if (Character.isDigit(toLiveString[i].charAt(0)) && toLiveString[i].length() == 1) {
                    toLive[i] = (byte) Integer.parseInt(toLiveString[i]);
                }
            }

        }
        if (tfCellsToSpawn.getText().replaceAll("\\D", "").equals("")) {
            tfCellsToSpawn.setText("");
            toSpawn = new byte[]{-1};
        } else {
            String[] toSpawnString = tfCellsToSpawn.getText().replaceAll("\\D", "").split("");

            toSpawn = new byte[toSpawnString.length];
            for (int i = 0; i < toSpawn.length; i++) {
                if (Character.isDigit(toSpawnString[i].charAt(0)) && toSpawnString[i].length() == 1) {
                    toSpawn[i] = (byte) Integer.parseInt(toSpawnString[i]);
                }
            }
        }

        constructRule(toLive, toSpawn);
    }

    /**
     * //TODO bug?
     *
     * @Bug You can cheat this method if you zoom out max with max spacing, then
     * remove the spacing, but this is the only issue.
     */
    @FXML
    public void handleZoom() {
        double x = cellSizeSlider.getValue();
        double newValue = 0.2 * Math.exp(0.05 * x);
        if ((newValue + activeBoard.getGridSpacing()) * activeBoard.getArrayLength() > canvas.getHeight()
                && (newValue + activeBoard.getGridSpacing()) * activeBoard.getArrayLength(0) > canvas.getWidth()) {

            handleGridSpacingSlider();

            calcNewOffset(activeBoard.getCellSize(), newValue);
            activeBoard.setCellSize(newValue);
        } else {
            cellSizeSlider.setValue(20 * Math.log(5 * activeBoard.getCellSize()));
        }

        draw();
    }

    //TODO ZOOM!
    @FXML
    public void handleGridSpacingSlider() {
        double x = cellSizeSlider.getValue();
        activeBoard.setGridSpacing(0.2 * Math.exp(0.05 * x) * gridSpacingSlider.getValue() / 100);
        draw();
    }

    @FXML
    public void handleColor() {
        //TODO
        cellColor = cellCP.getValue();
        backgroundColor = backgroundCP.getValue();
        draw();
    }

    @FXML
    public void handleClearBtn() {
        activeBoard.clearBoard();
        timeline.pause();
        startPauseBtn.setText("Start game");
        draw();
    }

    @FXML
    public void handleImportFileBtn() {
        try {
            FileChooser fileChooser = new FileChooser();

            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Game of Life Files", "*.rle", "*.lif", "*.cells"),
                    new FileChooser.ExtensionFilter("All Files", "*.*"));

            File selected = fileChooser.showOpenDialog(null);
            if (selected != null) {
                boardFromFile = ReadFile.readFileFromDisk(selected.toPath());

                //TODO no ghosttiles yet
                activeBoard.insertArray(boardFromFile, 1, 1);
                draw();
            }

        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "There was an error reading the file");
            alert.setTitle("Error");
            alert.setHeaderText("Reading File Error");
            alert.showAndWait();
        } catch (PatternFormatException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.setTitle("Error");
            alert.setHeaderText("Pattern Error");
            alert.showAndWait();

        }
    }

    @FXML
    public void openPatternEditor() throws IOException {
        timeline.pause();

        Stage editor = new Stage();
        FXMLLoader root = new FXMLLoader(getClass().getResource("/gol/s305089/view/PatternEditor.fxml"));

        Scene scene = new Scene((Parent) root.load());
        EditorController editorController = root.<EditorController>getController();
        editorController.setActiveBoard(activeBoard);

        editor.setScene(scene);
        editor.setTitle("Pattern Editor");
        editor.initModality(Modality.APPLICATION_MODAL);
        editor.show();
    }
    
    public void currentBoardToGIF() throws IOException {
        timeline.pause();

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

    @FXML
    public void showStats() throws IOException {
        timeline.pause();

        Stage golStats = new Stage();
        FXMLLoader root = new FXMLLoader(getClass().getResource("/gol/s305089/view/Stats.fxml"));

        Scene scene = new Scene((Parent) root.load());

        StatsController statsController = root.<StatsController>getController();
        statsController.setByteBoard(activeBoard);

        golStats.setScene(scene);
        golStats.setTitle("Stats - Game of Life");
        
        
        golStats.show();
    }

    public void constructRule(byte[] cellsToLive, byte[] cellsToSpawn) {
        try {
            activeBoard.setGameRule(new CustomRule(cellsToLive, cellsToSpawn));
        } catch (unsupportedRuleException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.setTitle("Error");
            alert.setHeaderText("Your given rule is not supported. \n Try again.");
            alert.showAndWait();
        }
    }

    public void setActiveBoard(Board activeBoard) {
        this.activeBoard = activeBoard;
    }

    public Board getActiveBoard() {
        return activeBoard;
    }

    /**
     * Initiates all relevant MouseEvents.
     */
    private void mouseInit() {

        //Registers clicks on scene
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
                (MouseEvent e) -> {
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
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED,
                (MouseEvent e) -> {
                    if (rbMoveGrid.isSelected()) {
                        moveGridValues[2] = -Double.MAX_VALUE;
                        moveGridValues[3] = -Double.MAX_VALUE;
                    }
                });
    }

    /**
     * //TODO Fix Comments QUICK NOTE: Draws the grid. First decide where to
     * draw based on size and gridspacing, then calculates to draw in the middle
     * of gridspcaing (see - halfGridSpace) after this is done it adds the
     * offset
     */
    public void drawGrid() {
        gc.setFill(Color.BLUE);
        //TODO Så den ikke tegner det som er utenfor det vi ser
        double sizeAndSpacing = activeBoard.getCellSize() + activeBoard.getGridSpacing();
        double halfGridSpace = activeBoard.getGridSpacing() / 2;
        for (int i = 0; i <= activeBoard.getArrayLength(); i++) {
            gc.strokeLine((i * sizeAndSpacing - halfGridSpace) + moveGridValues[0], 0,
                    (i * sizeAndSpacing - halfGridSpace) + moveGridValues[0], canvas.getHeight());

            for (int j = 0; j <= activeBoard.getArrayLength(i); j++) {

                gc.strokeLine(0, (j * sizeAndSpacing - halfGridSpace) + moveGridValues[1],
                        canvas.getWidth(), (j * sizeAndSpacing - halfGridSpace) + moveGridValues[1]);

            }
        }

    }

    public void draw() {
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
                    gc.fillRect(j * activeBoard.getCellSize() + j * activeBoard.getGridSpacing() + moveGridValues[0],
                            i * activeBoard.getCellSize() + i * activeBoard.getGridSpacing() + moveGridValues[1],
                            activeBoard.getCellSize(),
                            activeBoard.getCellSize());
                }
            }
        }

    }

    //Over complicated for the sake of smoothness, this code may have huge potensial for improvement. 
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

    /**
     *
     * @param e
     */
    private void handleMouseClick(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        if (rbRemoveCell.isSelected()) {
            activeBoard.setCellState(y, x, false, moveGridValues[0], moveGridValues[1]);
        } else if (rbMoveGrid.isSelected()) {
        } else {
            activeBoard.setCellState(y, x, true, moveGridValues[0], moveGridValues[1]);

        }
        draw();
    }

    //Does not calc gridspacing yet.
    public void calcNewOffset(double cellSize, double newCellSize) {
        double gridSpace = activeBoard.getGridSpacing();
        if (cellSize != 0) {

            double oldx = (canvas.getWidth() / 2 - moveGridValues[0]) / (cellSize);
            double oldy = (canvas.getHeight() / 2 - moveGridValues[1]) / (cellSize);

            moveGridValues[0] = -(oldx * (newCellSize) - canvas.getWidth() / 2);
            moveGridValues[1] = -(oldy * (newCellSize) - canvas.getHeight() / 2);

            moveGridValues[0] = (moveGridValues[0] > 0) ? 0 : moveGridValues[0];
            moveGridValues[1] = (moveGridValues[1] > 0) ? 0 : moveGridValues[1];

        }

    }

}
