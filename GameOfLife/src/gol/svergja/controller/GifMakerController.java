package gol.svergja.controller;

import gol.svergja.model.GifMaker;
import gol.model.Board.Board;
import gol.model.Board.DynamicBoard;
import gol.svergja.Util;
import gol.svergja.model.Stats;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author s305089 - John Kasper Svergja
 */
public class GifMakerController implements Initializable {

    @FXML
    private BorderPane borderpane;
    @FXML
    private VBox vBoxThreashold;
    @FXML
    private Label labelLoopStatus;
    @FXML
    private Slider sliderCellSize;
    @FXML
    private Spinner spinnTimeBetween;
    @FXML
    private Spinner spinnNumIterations;
    @FXML
    private Spinner spinnWidth;
    @FXML
    private Spinner spinnHeight;
    @FXML
    private Spinner spinnThreshold;
    @FXML
    private ColorPicker cpCellColor;
    @FXML
    private ColorPicker cpBackColor;
    @FXML
    private CheckBox cbCenterPattern;
    @FXML
    private CheckBox cbAutoPreview;
    @FXML
    private CheckBox cbRndCellColor;
    @FXML
    private CheckBox cbInfinityLoop;
    @FXML
    private CheckBox cbCalcCellSize;
    @FXML
    private CheckBox cbCheckPrevGen;
    @FXML
    private Tooltip tooltipSaveLoc;
    @FXML
    private Label labelGenerateFeedback;
    @FXML
    private ImageView imgViewPreview;

    private GifMaker gifmaker;
    private String saveLocation;
    private int iterations;
    //This class uses only the activeBoard to open stats
    private Board activeBoard;
    Tooltip tipInfinity = new Tooltip("Check which iteration the 0-th generation matches best with.");
    Tooltip tipCheckPrev = new Tooltip("Check if some other generation matches with the 0-th generation.");

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        gifmaker = new GifMaker();

        saveLocation = System.getProperty("user.home") + "\\golGif.gif";

        cpCellColor.setValue(Color.BLACK);
        initSpinners();
        initListners();
        setGIFValues();
    }

    private void initSpinners() {
        spinnNumIterations.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 20, 5));
        spinnNumIterations.setEditable(true);
        spinnNumIterations.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            //Should only happen when it loses focus
            if (!newValue) {
                Util.commitEditorText(spinnNumIterations);
            }
        });

        spinnTimeBetween.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100000, 200, 100));
        spinnTimeBetween.setEditable(true);
        spinnTimeBetween.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            //Should only happen when it loses focus
            if (!newValue) {
                Util.commitEditorText(spinnTimeBetween);
            }
        });

        spinnWidth.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5000, 200, 100));
        spinnWidth.setEditable(true);
        spinnWidth.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            //Should only happen when it loses focus
            if (!newValue) {
                Util.commitEditorText(spinnWidth);
            }
        });

        spinnHeight.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5000, 200, 100));
        spinnHeight.setEditable(true);
        spinnHeight.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            //Should only happen when it loses focus
            if (!newValue) {
                Util.commitEditorText(spinnHeight);
            }
        });

        spinnThreshold.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 90, 1));
        spinnThreshold.setEditable(true);
        spinnThreshold.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            //Should only happen when it loses focus
            if (!newValue) {
                Util.commitEditorText(spinnThreshold);
            }
        });

        cbInfinityLoop.setOnMouseEntered((event) -> {
            Util.showTooltip(event, cbInfinityLoop, tipInfinity);
        });
        cbCheckPrevGen.setOnMouseEntered((event) -> {
            Util.showTooltip(event, cbCheckPrevGen, tipCheckPrev);
        });

        cbInfinityLoop.setOnMouseExited(event -> tipInfinity.hide());
        cbCheckPrevGen.setOnMouseExited(event -> tipCheckPrev.hide());

    }

    private void initListners() {
        sliderCellSize.valueProperty().addListener(this::autoUpdatedPreview);
        spinnNumIterations.valueProperty().addListener(this::autoUpdatedPreview);
        spinnTimeBetween.valueProperty().addListener(this::autoUpdatedPreview);
        spinnWidth.valueProperty().addListener(this::autoUpdatedPreview);
        spinnHeight.valueProperty().addListener(this::autoUpdatedPreview);
        cpCellColor.valueProperty().addListener(this::autoUpdatedPreview);
        cpBackColor.valueProperty().addListener(this::autoUpdatedPreview);
        cbCenterPattern.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            autoUpdatedPreview(observable, oldValue, newValue);
            cbCalcCellSize.setDisable(!newValue);
            if (!newValue) {
                cbCalcCellSize.setSelected(false);
            }
        });
        cbRndCellColor.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            gifmaker.setRandomColor(newValue);
            autoUpdatedPreview(observable, oldValue, newValue);
        });
        cbInfinityLoop.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            vBoxThreashold.setVisible(newValue);
            autoUpdatedPreview(observable, oldValue, newValue);
        });
        cbCalcCellSize.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            gifmaker.setAutoCalcCellSize(newValue);
            sliderCellSize.setDisable(newValue);
            autoUpdatedPreview(observable, oldValue, newValue);
        });

    }

    private void autoUpdatedPreview(ObservableValue ob, Object oldValue, Object newValue) {
        if (cbAutoPreview.isSelected()) {
            previewGif();
        }
    }

    private void calculateInfinity() {
        if (cbInfinityLoop.isSelected()) {

            int threshold = (int) spinnThreshold.getValue();
            int closestIteration = -1;

            Stats stats = new Stats();
            stats.setBoard(activeBoard);

            stats.setCheckSimilarityPrevGen(cbCheckPrevGen.isSelected());
            stats.getStatistics(iterations, true, true);
            //Checks which iteration the 0-th generation matches best with.
            if (stats.getSimilarityMeasure()[0][0] > threshold) {
                closestIteration = stats.getSimilarityMeasure()[0][1];
            }
            //Checks if some other generation matches with the 0-th generation
            for (int[] it : stats.getSimilarityMeasure()) {
                if (it[0] > threshold && it[1] == 0) {
                    closestIteration = it[1];
                }
            }
            if (closestIteration != -1) {
                iterations = closestIteration + 1;
                labelLoopStatus.setText("Match found at iteration: " + iterations + "\n looping..");
            } else {
                labelLoopStatus.setText("Could not find an good enough match..");
            }
        }
    }

    private boolean chooseSaveDest() {
        FileChooser filechooser = new FileChooser();
        filechooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Graphics Interchange Format", "*.gif"));
        File fileSaveLocation = filechooser.showSaveDialog(null);

        if (fileSaveLocation != null) {
            saveLocation = fileSaveLocation.toPath().toString();
            return true;
        } else {
            return false;
        }
    }

    @FXML
    private void openStats() {
        try {
            Stage golStats = new Stage();
            FXMLLoader root = new FXMLLoader(getClass().getResource("/gol/svergja/view/Stats.fxml"));

            Scene scene = new Scene((Parent) root.load());

            StatsController statsController = root.<StatsController>getController();
            statsController.setBoard(activeBoard);

            golStats.setScene(scene);
            golStats.setTitle("Stats - Game of Life");

            golStats.show();
        } catch (IOException ex) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Sorry, an error occured...\n" + ex);
            errorAlert.showAndWait();
        }
    }

    @FXML
    private void generateGIF() {
        setGIFValues();
        calculateInfinity();
        if (chooseSaveDest()) {
            labelGenerateFeedback.setText("Generating gif.");
            Task<Void> drawGif = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    gifmaker.writePatternToGIF(iterations, saveLocation);
                    Platform.runLater(() -> labelGenerateFeedback.setText("GIF was successfully created"));
                    return null;
                }
            };
            Thread th = new Thread(drawGif);
            th.start();
        }
    }

    @FXML
    private void previewGif() {
        borderpane.getScene().getWindow().setWidth(570);
        borderpane.getScene().getWindow().setHeight(540);
        ((Stage) borderpane.getScene().getWindow()).setResizable(false);

        try {
            File previewFile = File.createTempFile("golPreview", ".gif");
            labelGenerateFeedback.setText("Generating preview.");
            setGIFValues();
            calculateInfinity();
            Task<Void> drawGif = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    gifmaker.writePatternToGIF(iterations, previewFile.getAbsolutePath());
                    Platform.runLater(() -> {
                        Image previewGif = new Image(previewFile.toURI().toString());
                        imgViewPreview.setImage(previewGif);
                        labelGenerateFeedback.setText("");
                        previewFile.delete();
                    });
                    return null;
                }
            };
            Thread th = new Thread(drawGif);
            th.start();

        } catch (IOException ex) {
            System.err.println("There was an error previewing the file...\n" + ex);
        }
    }

    /**
     * Saves the current board to be used with live sound. The board is also
     * sent the controller for making wav files.
     *
     * @param boardToSet The board that should be used with live sound
     */
    public void setBoard(Board boardToSet) {
        activeBoard = new DynamicBoard();
        activeBoard.insertArray(boardToSet.getBoundingBoxBoard());
        gifmaker.setBoard(boardToSet);
    }

    private void setGIFValues() {
        iterations = (int) spinnNumIterations.getValue();
        gifmaker.setCellSize(sliderCellSize.getValue());
        gifmaker.setDurationBetweenFrames((int) spinnTimeBetween.getValue());

        gifmaker.setCellColor(cpCellColor.getValue());
        gifmaker.setBackgroundColor(cpBackColor.getValue());

        gifmaker.setGifHeight((int) spinnHeight.getValue());
        gifmaker.setGifWidth((int) spinnWidth.getValue());
        gifmaker.setCenterPattern(cbCenterPattern.isSelected());
    }

}
