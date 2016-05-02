package gol.s305089.controller;

import gol.model.Board.Board;
import gol.model.Board.DynamicBoard;
import gol.s305089.sound.Sound;
import gol.s305089.sound.Tone;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * @author s305089 - John Kasper Svergja
 */
public class WavMakerController implements Initializable {

    @FXML
    private Spinner spinnDur;
    @FXML
    private Spinner spinnIte;
    @FXML
    private RadioButton rbLivingDead;
    @FXML
    private RadioButton rbCountRow;
    @FXML
    private RadioButton rbCountNeigh;
    @FXML
    private ComboBox comBxRootTone;

    private byte[][] originalPattern;
    private Board activeBoard;
    private int iterationsToCalc;
    private double durEachIt;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
    }

    private void initView() {
        spinnIte.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 20, 1));
        spinnIte.setEditable(true);
        spinnDur.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.1, 100, 1, 0.2));
        spinnDur.setEditable(true);
        comBxRootTone.getItems().setAll(Tone.C3, Tone.D3, Tone.E3, Tone.F3, Tone.G3, Tone.A3, Tone.B3);
        comBxRootTone.setValue(Tone.C3);

    }

    private void getValuesFromView() {
        iterationsToCalc = (int) spinnIte.getValue();
        durEachIt = (double) spinnDur.getValue();;
    }

    public void setBoard(Board activeBoard) {
        originalPattern = activeBoard.getBoundingBoxBoard();
        this.activeBoard = new DynamicBoard();
        this.activeBoard.insertArray(originalPattern, 3, 3);
    }

    @FXML
    private void saveWav() {
        getValuesFromView();
    }

    @FXML
    private void previewWav() {
        try {
            setPattern(originalPattern);
            getValuesFromView();

            File previewFile = File.createTempFile("golSoundPreview", ".wav");

            handleMode();
            Sound.makeSound(previewFile, durEachIt);
            System.out.println(previewFile);
            MediaPlayer player = new MediaPlayer(new Media(previewFile.toURI().toString()));
            player.setOnEndOfMedia(() -> player.dispose());
            player.play();

        } catch (IOException ex) {
            System.out.println("There was an error creating the preview...\n" + ex);
        }

    }

    private void append1dLivingDeadRatio() {
        for (int i = 0; i < iterationsToCalc; i++) {
            String board = activeBoard.toString();
            char[] letters = board.toCharArray();
            int countLiving = 0;
            for (char letter : letters) {
                if (letter == '1') {
                    countLiving++;
                }
            }
            double rootToneFreq = ((Tone) comBxRootTone.getValue()).getFreq();
            double frequency = (double) 10 * rootToneFreq * countLiving / board.length();
            //TODO DYNAMIC ADDING!
            Sound.addToSequence(i, frequency);

            activeBoard.nextGen();
        }
    }

    private void append1dRowCoherent() {
        for (int iteration = 0; iteration < iterationsToCalc; iteration++) {
            byte[][] current = activeBoard.getBoundingBoxBoard();
            Set<Integer> countOnRowSet = new HashSet<>();

            int countOnRow = 0;
            for (byte[] row : current) {
                for (int j = 0; j < row.length; j++) {
                    //If last element on row is alive.
                    if (row[j] == 64 && j == row.length - 1) {
                        countOnRow++;
                        countOnRowSet.add(countOnRow);
                    } else if (row[j] == 64) {
                        countOnRow++;
                    } else if (countOnRow > 0) {
                        countOnRowSet.add(countOnRow);
                        countOnRow = 0;
                    }
                }
                countOnRow = 0;
            }
            rowCoherentAddSound(countOnRowSet, iteration);
            activeBoard.nextGen();
        }
    }

    private <T> void rowCoherentAddSound(Set<Integer> values, int ite) {
        for (Integer countOnRow : values) {
            if (countOnRow == 1) {
                Sound.addToSequence(ite, Tone.D4);
            } else if (countOnRow == 2) {
                Sound.addToSequence(ite, Tone.E4);
            } else if (countOnRow == 3) {
                Sound.addToSequence(ite, Tone.G4);
            } else if (countOnRow == 4) {
                Sound.addToSequence(ite, Tone.C4);
            } else if (countOnRow == 5) {
                Sound.addToSequence(ite, Tone.E3);
            } else if (countOnRow <= 7) {
                Sound.addToSequence(ite, Tone.C3);
            } else {
                Sound.addToSequence(ite, Tone.G2);
            }
        }
    }

    private void setPattern(byte[][] patternToSet) {
        activeBoard.clearBoard();
        activeBoard.insertArray(patternToSet, 5, 5);
    }

    private void handleMode() {
        if (rbLivingDead.isSelected()) {
            append1dLivingDeadRatio();
        } else if (rbCountRow.isSelected()) {
            append1dRowCoherent();
        } else if (rbCountNeigh.isSelected()) {

        }
    }
}
