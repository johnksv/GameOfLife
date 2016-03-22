/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.s305089.controller.gif;

import gol.model.Board.Board;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

/**
 * FXML Controller class
 *
 * @author s305089
 */
public class GifMakerController implements Initializable {

    @FXML
    private Label labelCurrentDest;
    @FXML
    private Slider sliderCellSize;
    @FXML
    private Spinner spinnNumIterations;
    @FXML
    private Spinner spinnTimeBetween;
    @FXML
    private Tooltip tooltipSaveLoc;
    @FXML
    private Label labelGenerateFeedback;
    @FXML
    private ImageView imgViewPreview;

    private byte[][] activeByteBoard;
    private GifMaker gifmaker;
    private String saveLocation;
    private int iterations;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            gifmaker = new GifMaker();
        } catch (IOException ex) {
            System.err.println("File stream could not be established.. Try again");
        }
        saveLocation = System.getProperty("user.home") + "/golGif.gif";
        initSpinners();
        setGIFSaveLocation();
        setGIFValuesFromSpinners();
    }

    private void initSpinners() {
        spinnNumIterations.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 20, 1));
        spinnTimeBetween.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10000, 200, 1));
        spinnNumIterations.setEditable(true);
        spinnTimeBetween.setEditable(true);
        spinnNumIterations.valueProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
            setGIFValuesFromSpinners();
        });
        spinnTimeBetween.valueProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
            setGIFValuesFromSpinners();
        });
    }

    @FXML
    private void chooseSaveDest() {
        FileChooser filechooser = new FileChooser();
        //TODO set filter for filechooser to .gif
        File fileSaveLocation = filechooser.showSaveDialog(null);

        if (fileSaveLocation != null) {
            saveLocation = fileSaveLocation.toPath().toString();
        } else {
            saveLocation = System.getProperty("user.home");
        }
        setGIFSaveLocation();
    }

    @FXML
    private void handleCellSizeChange() {
        gifmaker.setCellSize((int) sliderCellSize.getValue());
    }

    @FXML
    private void generateGIF() {
        setGIFSaveLocation();
        setGIFValuesFromSpinners();
        try {
            gifmaker.writePatternToGIF(iterations);
            labelGenerateFeedback.setText("GIF was successfully created");
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Det oppsto en feil...:\n" + ex.getMessage());
            alert.setTitle("Error");
            alert.setHeaderText("Pattern to GIF");
            alert.showAndWait();
        }
    }

    @FXML
    private void previewGif() {
        try {
            File previewFile = File.createTempFile("golPreview", ".gif");
            gifmaker.setSaveLocation(previewFile.getAbsolutePath());

            setGIFValuesFromSpinners();

            gifmaker.writePatternToGIF(iterations);
            Image previewGif = new Image(previewFile.toURI().toString());
            imgViewPreview.setImage(previewGif);
            
            previewFile.delete();

        } catch (IOException ex) {
            System.err.println("There was an error previewing the file...\n" + ex);
        }
    }

    private void setGIFSaveLocation() {
        labelCurrentDest.setText("Current: " + saveLocation);
        gifmaker.setSaveLocation(saveLocation);
        tooltipSaveLoc.setText(saveLocation);
    }

    private void setGIFValuesFromSpinners() {
        iterations = (int) spinnNumIterations.getValue();
        gifmaker.setDurationBetweenFrames((int) spinnTimeBetween.getValue());
    }

    public void setByteBoard(Board activeBoard) {
        this.activeByteBoard = activeBoard.getBoundingBoxBoard();
        gifmaker.setPattern(activeByteBoard);
    }
}
