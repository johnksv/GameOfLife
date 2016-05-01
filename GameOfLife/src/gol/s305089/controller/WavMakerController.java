package gol.s305089.controller;

import gol.model.Board.Board;
import gol.model.Board.DynamicBoard;
import gol.s305089.sound.Sound;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

/**
 * @author John Kasper
 */
public class WavMakerController implements Initializable {

    @FXML
    private Spinner spinnDur;
    @FXML
    private Spinner spinnIte;
    private byte[][] originalPattern;
    private Board activeSoundBoard;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initSpinner();
    }

    private void initSpinner() {
        spinnIte.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 20, 1));
        spinnIte.setEditable(true);
        spinnDur.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.1, 100, 1, 0.2));
        spinnDur.setEditable(true);
    }

    @FXML
    private void saveWav() {

    }

    @FXML
    private void previewWav() {
        try {
            File previewFile = File.createTempFile("golSoundPreview", ".wav");
            double durEachIt = (double) spinnDur.getValue();
            appendSound();
            Sound.makeSound(previewFile, durEachIt);
        } catch (IOException ex) {
            System.out.println("There was an error creating the preview...\n" + ex);
        }

    }

    private void appendSound() {
        int iterations = (int) spinnIte.getValue();
        for (int i = 0; i < iterations; i++) {
            
            
            activeSoundBoard.nextGenConcurrent();
        }
    }

    public void setBoard(Board activeBoard) {
        originalPattern = activeBoard.getBoundingBoxBoard();
        activeSoundBoard = new DynamicBoard();
        activeSoundBoard.insertArray(originalPattern, 3, 3);
    }
}
