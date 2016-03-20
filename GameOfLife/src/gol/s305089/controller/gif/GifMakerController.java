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
import java.util.function.UnaryOperator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

/**
 * FXML Controller class
 *
 * @author s305089
 */
public class GifMakerController implements Initializable {

    @FXML
    private BorderPane borderPane;
    @FXML
    private Label labelCurrentDest;
    @FXML
    private Button btnChooseSaveDest;
    @FXML
    private Slider sliderCellSize;
    @FXML
    private Spinner spinnNumIterations;
    @FXML
    private Spinner spinnTimeBetween;
    @FXML
    private ImageView imgViewPreview;
    @FXML
    private Tooltip tooltipSaveLoc;
    @FXML
    private Label labelGenerateFeedback;

    protected byte[][] activeByteBoard;
    private String saveLocation;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        saveLocation = System.getProperty("user.home") + "/golGif.gif";
        initSpinners();
        setSaveLocation();
        setValuesFromSpinners();

    }

    private void initSpinners() {
        spinnNumIterations.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 20, 1));
        spinnTimeBetween.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10000, 20, 1));
        spinnNumIterations.setEditable(true);
        spinnTimeBetween.setEditable(true);
        spinnNumIterations.valueProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
            setValuesFromSpinners();
        });
        spinnTimeBetween.valueProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
            setValuesFromSpinners();
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
        setSaveLocation();
    }

    @FXML
    private void handleCellSizeChange() {
        GifWriter.setCellSize((int) sliderCellSize.getValue());
    }

    @FXML
    private void generateGIF() {
        setSaveLocation();
        setValuesFromSpinners();
        try {
            GifWriter.writeBoardtoGIF(activeByteBoard);
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
            GifWriter.setSaveLocation(previewFile.getAbsolutePath());
            
            GifWriter.writeBoardtoGIF(activeByteBoard);
            Image previewGif = new Image(previewFile.toURI().toString());
            
            imgViewPreview.setImage(previewGif);
            
            previewFile.deleteOnExit();
            
        } catch (IOException ex) {
            System.err.println("There was an error previewing the file...\n" + ex );
        }

    }

    private void setSaveLocation() {
        labelCurrentDest.setText("Current: " + saveLocation);
        GifWriter.setSaveLocation(saveLocation);
        tooltipSaveLoc.setText(saveLocation);
    }
    
    private void setValuesFromSpinners(){
        GifWriter.setIterations(((int) spinnNumIterations.getValue()));
        GifWriter.setDurationBetweenFrames((int) spinnTimeBetween.getValue());
    }

    public void setByteBoard(Board activeBoard) {
        this.activeByteBoard = activeBoard.getBoundingBoxBoard();
    }

}
