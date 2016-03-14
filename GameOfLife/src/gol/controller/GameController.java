package gol.controller;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
import gol.model.FileIO.PatternFormatException;
import gol.model.FileIO.ReadFile;
import gol.model.Logic.ConwaysRule;
import gol.model.Logic.CustomRule;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.Duration;

/**
 * @author s305054, s305084, s305089
 */
public class GameController implements Initializable {

    @FXML
    private CanvasController canvasController;

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

    private Board activeBoard;
    private final Timeline timeline = new Timeline();
    private byte[][] boardFromFile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        activeBoard = new ArrayBoard();
        cellCP.setValue(Color.BLACK);
        backgroundCP.setValue(Color.web("#F4F4F4"));

        initCanvasController();
        //handleZoom();
        activeBoard.setCellSize(0.2 * Math.exp(0.05 *cellSizeSlider.getValue() ));
        handleGridSpacingSlider();
        handleColor();
        handleAnimationSpeedSlider();
        initAnimation();
        initGameRulesListner();
        draw();

    }

    private void initCanvasController() {
        canvasController.setActiveBoard(activeBoard);
        canvasController.setRbMoveGrid(rbMoveGrid);
        canvasController.setRbRemoveCell(rbRemoveCell);
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

    private void draw() {
        canvasController.draw();
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
    public void handleRuleText() {
        //TODO

    }

    @FXML
    public void handleRuleBtn() {

        String[] toLiveString = tfCellsToLive.getText().replaceAll("\\D", "").split("");
        String[] toSpawnString = tfCellsToSpawn.getText().replaceAll("\\D", "").split("");

        byte[] toLive = new byte[toLiveString.length];
        for (int i = 0; i < toLive.length; i++) {
            if (Character.isDigit(toLiveString[i].charAt(0)) && toLiveString[i].length() == 1) {
                toLive[i] = (byte) Integer.parseInt(toLiveString[i]);
            }
        }
        byte[] toSpawn = new byte[toSpawnString.length];
        for (int i = 0; i < toSpawn.length; i++) {
            if (Character.isDigit(toSpawnString[i].charAt(0)) && toSpawnString[i].length() == 1) {
                toSpawn[i] = (byte) Integer.parseInt(toSpawnString[i]);
            }
        }

        constructRule(toLive, toSpawn);
    }

    @FXML
    public void handleZoom() {
        double x = cellSizeSlider.getValue();
        //Har ikke implementert gridspacing
        canvasController.calcNewOffset(activeBoard.getCellSize(),0.2 * Math.exp(0.05 * x));
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
        //TODO
        canvasController.setCellColor(cellCP.getValue());
        canvasController.setBackgroundColor(backgroundCP.getValue());
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

    public void constructRule(byte[] cellsToLive, byte[] cellsToSpawn) {
        activeBoard.setGameRule(new CustomRule(cellsToLive, cellsToSpawn));
    }

    public void setActiveBoard(Board activeBoard) {
        this.activeBoard = activeBoard;
    }

    public Board getActiveBoard() {
        return activeBoard;
    }

}
