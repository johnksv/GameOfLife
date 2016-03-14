package gol.controller;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
import gol.model.FileIO.PatternFormatException;
import gol.model.FileIO.ReadFile;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
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
    @FXML
    private ToggleGroup mouseActions;
    @FXML
    private RadioButton rbAddCell;
    @FXML
    private RadioButton rbRemoveCell;
    @FXML
    private RadioButton rbMoveGrid;

    private Board activeBoard;
    private Color cellColor;
    private Color backgroundColor;
    private GraphicsContext gc;
    private final Timeline timeline = new Timeline();
    private byte[][] boardFromFile;
    private double[] moveGridValues = new double[4]; //Offset x, offset y, old x, old y

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc = canvas.getGraphicsContext2D();

        activeBoard = new ArrayBoard();
        cellCP.setValue(Color.BLACK);
        backgroundCP.setValue(Color.web("#F4F4F4"));

        handleZoom();
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
                    if (rbMoveGrid.isSelected()) {
                        moveGrid(e);
                    } else {
                        handleMouseClick(e);
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

    private void handleMouseClick(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        if (rbRemoveCell.isSelected()) {
            activeBoard.setCellState(y, x, false);
        } else if (rbMoveGrid.isSelected()) {
            System.out.println("Moving gird ");
        } else {
            activeBoard.setCellState(y, x, true);
        }
        draw();
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
        double x = cellSizeSlider.getValue();
        activeBoard.setCellSize(0.2 * Math.exp(0.05 * x));
        handleGridSpacingSlider();
        draw();
    }

    @FXML
    public void handleGridSpacingSlider() {
        activeBoard.setGridSpacing(activeBoard.getCellSize() * gridSpacingSlider.getValue() / 100);
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
                boardFromFile = ReadFile.readFileFromDisk(selected.toPath());

                //no ghosttiles yet
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

    public void draw() {
        gc.setFill(backgroundColor);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(cellColor);
        for (int i = 1; i < activeBoard.getArrayLength(); i++) {
            if (canvas.getHeight() < i * activeBoard.getCellSize() + i * activeBoard.getGridSpacing()) {
                break;
            }
            for (int j = 1; j < activeBoard.getArrayLength(i); j++) {
                if (activeBoard.getCellState(i, j)) {
                    if (canvas.getWidth() < j * activeBoard.getCellSize() + j * activeBoard.getGridSpacing()) {
                        break;
                    }
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

    public void setActiveBoard(Board activeBoard) {
        this.activeBoard = activeBoard;
    }

    public Board getActiveBoard() {
        return activeBoard;
    }

    private void moveGrid(MouseEvent e) {
        moveGridValues[0] += e.getX() - moveGridValues[2]; //Offset x = x position - old x
        moveGridValues[1] += e.getY() - moveGridValues[3];
        moveGridValues[2] = e.getX();
        moveGridValues[3] = e.getY();
        draw();

    }
}
