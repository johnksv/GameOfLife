package gol.s305089.controller;

import gol.s305089.model.GifMaker;
import gol.model.Board.Board;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

/**
 * FXML Controller class
 *
 * @author s305089
 */
public class GifMakerController implements Initializable {

    @FXML
    private BorderPane borderpane;
    @FXML
    private Label labelCurrentDest;
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
    private Tooltip tooltipSaveLoc;
    @FXML
    private Label labelGenerateFeedback;
    @FXML
    private ImageView imgViewPreview;
    //TODO Rnd Cell color, infity loop, automatic size of borderPane, OR clean code and GUI of it
    private GifMaker gifmaker;
    private String saveLocation;
    private int iterations;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        gifmaker = new GifMaker();

        saveLocation = System.getProperty("user.home") + "\\golGif.gif";

        cpCellColor.setValue(Color.BLACK);
        initSpinners();
        initListners();
        setGIFSaveLocation();
        setGIFValues();
    }

    private void initSpinners() {
        spinnNumIterations.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 20, 1));
        spinnNumIterations.setEditable(true);

        spinnTimeBetween.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10000, 200, 100));
        spinnTimeBetween.setEditable(true);

        spinnWidth.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10000, 200, 100));
        spinnWidth.setEditable(true);

        spinnHeight.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10000, 200, 100));
        spinnHeight.setEditable(true);

    }

    private void initListners() {
        sliderCellSize.valueProperty().addListener(this::autoUpdatedPreview);
        spinnNumIterations.valueProperty().addListener(this::autoUpdatedPreview);
        spinnTimeBetween.valueProperty().addListener(this::autoUpdatedPreview);
        spinnWidth.valueProperty().addListener(this::autoUpdatedPreview);
        spinnHeight.valueProperty().addListener(this::autoUpdatedPreview);
        cpCellColor.valueProperty().addListener(this::autoUpdatedPreview);
        cpBackColor.valueProperty().addListener(this::autoUpdatedPreview);
        cbCenterPattern.selectedProperty().addListener(this::autoUpdatedPreview);
        cbRndCellColor.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            gifmaker.setRandomColor(newValue);
        });
    }

    @FXML
    private void chooseSaveDest() {
        FileChooser filechooser = new FileChooser();
        filechooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Graphics Interchange Format", ".gif"));
        File fileSaveLocation = filechooser.showSaveDialog(null);

        if (fileSaveLocation != null) {
            saveLocation = fileSaveLocation.toPath().toString();
        } else {
            saveLocation = System.getProperty("user.home") + "\\golGif.gif";
        }
        setGIFSaveLocation();
    }

    @FXML
    private void generateGIF() {
        setGIFSaveLocation();
        setGIFValues();
        Alert alertGenerating = new Alert(Alert.AlertType.NONE, "Generating GIF");
        alertGenerating.getButtonTypes().add(new ButtonType("Please wait..."));
        alertGenerating.show();
        try {
            gifmaker.writePatternToGIF(iterations);
            labelGenerateFeedback.setText("GIF was successfully created");
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Det oppsto en feil...:\n" + ex.getMessage());
            alert.setTitle("Error");
            alert.setHeaderText("Pattern to GIF");
            alert.showAndWait();
        } finally {
            alertGenerating.close();
        }
    }

    @FXML
    private void previewGif() {
        try {
            File previewFile = File.createTempFile("golPreview", ".gif");
            gifmaker.setSaveLocation(previewFile.getAbsolutePath());

            setGIFValues();

            gifmaker.writePatternToGIF(iterations);
            Image previewGif = new Image(previewFile.toURI().toString());
            imgViewPreview.setImage(previewGif);

            previewFile.delete();

        } catch (IOException ex) {
            System.err.println("There was an error previewing the file...\n" + ex);
        }
    }

    public void setByteBoard(Board activeBoard) {
        gifmaker.setPattern(activeBoard.getBoundingBoxBoard());
    }

    private void setGIFSaveLocation() {
        labelCurrentDest.setText("Current: " + saveLocation);
        gifmaker.setSaveLocation(saveLocation);
        tooltipSaveLoc.setText(saveLocation);
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

    private void autoUpdatedPreview(ObservableValue ob, Object oldValue, Object newValue) {
        if (cbAutoPreview.isSelected()) {
            previewGif();
        }
    }
}
