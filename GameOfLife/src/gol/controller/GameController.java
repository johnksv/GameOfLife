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
import javafx.scene.control.ComboBox;
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
    private RadioButton rbStandardGameRules;
    @FXML
    private RadioButton rbCustomGameRules;
    @FXML
    private TextField tfCellsToSpawn;
    @FXML
    private TextField tfCellsToLive;

    private Board activeBoard;
    private final Timeline timeline = new Timeline();
    private byte[][] boardFromFile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        activeBoard = new ArrayBoard();
        cellCP.setValue(Color.BLACK);
        backgroundCP.setValue(Color.web("#F4F4F4"));
        System.out.println(canvasController);
        canvasController.setActiveBoard(activeBoard);
        canvasController.setRbMoveGrid(rbMoveGrid);
        canvasController.setRbRemoveCell(rbRemoveCell);
        
        
        handleZoom();
        handleColor();
        handleAnimationSpeedSlider();
        initAnimation();
        initGameRulesListner();

    }
    
    private void initGameRulesListner(){
        tgGameRules.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) -> {
            if(newValue == rbCustomGameRules){
                tfCellsToLive.setDisable(false);
                tfCellsToSpawn.setDisable(false);
            }else{
                tfCellsToLive.setDisable(true);
                tfCellsToSpawn.setDisable(true);
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
        //@TODO implement costume rules
    }


    public void setActiveBoard(Board activeBoard) {
        this.activeBoard = activeBoard;
    }

    public Board getActiveBoard() {
        return activeBoard;
    }

}
