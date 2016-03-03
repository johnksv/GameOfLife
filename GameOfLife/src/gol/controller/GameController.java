package gol.controller;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
import gol.model.FileIO.PatternFormatException;
import gol.model.FileIO.ReadFile;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
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

    private Board activeBoard;
    private Color cellColor;
    private Color backgroundColor;
    private GraphicsContext gc;
    private final Timeline timeline = new Timeline();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc = canvas.getGraphicsContext2D();

        activeBoard = new ArrayBoard();
        activeBoard.setCellSize(cellSizeSlider.getValue());
        activeBoard.setGridSpacing(gridSpacingSlider.getValue());

        cellCP.setValue(Color.BLACK);
        backgroundCP.setValue(Color.web("#F4F4F4"));
        handleColor();
        handleAnimationSpeedSlider();
        mouseInit();
        initAnimation();

    }

    //MouseEvent
    public void mouseInit() {

        //Registers clicks on scene
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
                (MouseEvent e) -> {
                    handleMouseClick(e);
                });
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                (MouseEvent e) -> {
                    handleMouseClick(e);
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

        if (timeline.getStatus() == Status.RUNNING) {
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
    public void handleZoom() {
        activeBoard.setCellSize(cellSizeSlider.getValue());
        draw();
    }

    @FXML
    public void handleGridSpacingSlider() {
        activeBoard.setGridSpacing(gridSpacingSlider.getValue());
        draw();
    }

    @FXML
    public void handleColor() {
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
                    new ExtensionFilter("Game of Life Files", "*.rle", "*.lif", "*.cells"),
                    new ExtensionFilter("All Files", "*.*"));

            File selected = fileChooser.showOpenDialog(null);
            if (selected != null) {
                try {
                    ReadFile.readFileFromDisk(selected.toPath());
                } catch (PatternFormatException ex) {
                    Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "There was an error reading the file");
        }
    }

    private void handleMouseClick(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();
        activeBoard.setCellState(x, y, true);
        draw();
    }

    public void draw() {
        gc.setFill(backgroundColor);
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
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

    public void constructRule(byte[] cellsToLive, byte[] cellsToSpawn) {
        //@TODO implement costume rules
    }

    public void setCellColor(Color cellColor) {
        this.cellColor = cellColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Board getActiveBoard() {
        return activeBoard;
    }

    public void setActiveBoard(Board activeBoard) {
        this.activeBoard = activeBoard;
    }

}
