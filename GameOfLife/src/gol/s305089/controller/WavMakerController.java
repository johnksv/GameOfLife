package gol.s305089.controller;

import gol.model.Board.Board;
import gol.model.Board.DynamicBoard;
import gol.s305089.Util;
import gol.s305089.model.GifMaker;
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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Duration;

/**
 * @author s305089 - John Kasper Svergja
 */
public class WavMakerController implements Initializable {

    @FXML
    private VBox vboxBaseTone;
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
    @FXML
    private Button btnPlayPreview;
    @FXML
    private Label labelInfo;
    @FXML
    private Label labelCurrentPattern;

    private byte[][] originalPattern;
    //The board that that was the parameter in setBoard
    private Board referenceBoard;
    private Board activeBoard;
    private int iterationsToCalc;
    private double durEachIt;
    private File previewFile;
    private MediaPlayer previewPlayer;

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
        rbCountRow.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            vboxBaseTone.setDisable(newValue);
        });
    }

    public File getPreviewFile() {
        return previewFile;
    }

    public void setBoard(Board activeBoard) {
        referenceBoard = activeBoard;
        originalPattern = activeBoard.getBoundingBoxBoard();
        this.activeBoard = new DynamicBoard();
        this.activeBoard.insertArray(originalPattern, 3, 3);

        generateBoardGIFPreview();
    }

    @FXML
    private void saveWav() {
        setPattern(originalPattern);
        getValuesFromView();

        FileChooser filechooser = new FileChooser();
        filechooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Waveform Audio", ".wav"),
                new FileChooser.ExtensionFilter(" ", ".*"));

        File result = filechooser.showSaveDialog(null);
        if (result != null) {
            handleWriteMode();
            Sound.makeSound(previewFile, durEachIt);
            labelInfo.setText("Wav file successfully generated.");
        }
    }

    @FXML
    private void previewWav() {
        try {
            setPattern(originalPattern);
            getValuesFromView();

            previewFile = File.createTempFile("golSoundPreview", ".wav");

            handleWriteMode();
            Sound.makeSound(previewFile, durEachIt);
            labelInfo.setText("Preview file successfully generated.");
            if (previewPlayer != null) {
                previewPlayer.dispose();
            }
            previewPlayer = new MediaPlayer(new Media(previewFile.toURI().toString()));
            previewPlayer.setOnEndOfMedia(() -> {
                btnPlayPreview.setText("Play preview");
                labelInfo.setText("");
                previewPlayer.seek(Duration.ZERO);
                previewPlayer.pause();
            });

        } catch (IOException ex) {
            System.out.println("There was an error creating the preview...\n" + ex);
        }
    }

    @FXML
    private void playPreview() {
        if (previewPlayer.statusProperty().get() == MediaPlayer.Status.STALLED
                || previewPlayer.statusProperty().get() == MediaPlayer.Status.PLAYING) {
            previewPlayer.pause();
            btnPlayPreview.setText("Play preview");
        } else {
            previewPlayer.play();
            Util.setTimeLabel(previewPlayer, labelInfo);
            btnPlayPreview.setText("Pause preview");
        }
    }

    @FXML
    private void updateBoard() {
        setBoard(referenceBoard);
    }

    private void livingDeadRatio() {
        double rootToneFreq = ((Tone) comBxRootTone.getValue()).getFreq();
        for (int i = 0; i < iterationsToCalc; i++) {
            String board = activeBoard.toString();
            char[] letters = board.toCharArray();
            int countLiving = 0;
            for (char letter : letters) {
                if (letter == '1') {
                    countLiving++;
                }
            }
            double frequency = (double) 10 * rootToneFreq * countLiving / board.length();
            Sound.addToSequence(i, frequency);

            activeBoard.nextGen();
        }
    }

    private void countRowCoherent() {
        for (int ite = 0; ite < iterationsToCalc; ite++) {
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

            for (Integer cntRow : countOnRowSet) {
                if (cntRow == 1) {
                    Sound.addToSequence(ite, Tone.D4);
                } else if (cntRow == 2) {
                    Sound.addToSequence(ite, Tone.E4);
                } else if (cntRow == 3) {
                    Sound.addToSequence(ite, Tone.G4);
                } else if (cntRow == 4) {
                    Sound.addToSequence(ite, Tone.C4);
                } else if (cntRow == 5) {
                    Sound.addToSequence(ite, Tone.E3);
                } else if (cntRow <= 7) {
                    Sound.addToSequence(ite, Tone.C3);
                } else {
                    Sound.addToSequence(ite, Tone.G2);
                }
            }

            activeBoard.nextGen();
        }
    }

    private void neighCount() {
        double rootToneFreq = ((Tone) comBxRootTone.getValue()).getFreq();

        for (int ite = 0; ite < iterationsToCalc; ite++) {
            Set<Integer> frequencySet = new HashSet<>();
            for (int i = 0; i < activeBoard.getArrayLength(); i++) {
                for (int j = 0; j < activeBoard.getArrayLength(i); j++) {
                    double neigh = 0.2;
                    if (activeBoard.getCellState(i, j)) {
                        //Goes through surrounding neighbours
                        for (int k = -1; k <= 1; k++) {
                            for (int l = -1; l <= 1; l++) {
                                if (!(k == 0 && l == 0)) {
                                    if (activeBoard.getCellState(i + k, j + l)) {
                                        neigh++;
                                    }
                                }
                            }
                        }
                        frequencySet.add((int) (rootToneFreq * neigh));
                    }
                }
            } //End of counting.

            for (Integer frequency : frequencySet) {
                Sound.addToSequence(ite, frequency);
            }
            activeBoard.nextGen();
        }
    }

    private void getValuesFromView() {
        iterationsToCalc = (int) spinnIte.getValue();
        durEachIt = (double) spinnDur.getValue();
    }

    private void handleWriteMode() {
        if (rbLivingDead.isSelected()) {
            livingDeadRatio();
        } else if (rbCountRow.isSelected()) {
            countRowCoherent();
        } else if (rbCountNeigh.isSelected()) {
            neighCount();
        }
    }

    private void setPattern(byte[][] patternToSet) {
        activeBoard.clearBoard();
        activeBoard.insertArray(patternToSet, 5, 5);
    }

    private void generateBoardGIFPreview() {
        try {
            Tooltip tooltip = new Tooltip();
            GifMaker gifmaker = new GifMaker();

            ImageView imgViewGIFPreview = new ImageView();
            gifmaker.setPattern(originalPattern);
            File tempFileToolTip = File.createTempFile("golStats", ".gif");

            gifmaker.writePatternToGIF(1, tempFileToolTip.getAbsolutePath());

            Image current = new Image(tempFileToolTip.toURI().toString());
            imgViewGIFPreview.setImage(current);

            tempFileToolTip.delete();

            VBox container = new VBox();
            container.getChildren().add(imgViewGIFPreview);

            tooltip.setGraphic(container);
            labelCurrentPattern.setOnMouseEntered((event) -> Util.showTooltip(event, labelCurrentPattern, tooltip));
            labelCurrentPattern.setOnMouseExited(event -> tooltip.hide());

        } catch (IOException ex) {
            System.err.println("Unable to create GIF preview.\n" + ex);
        }
    }
}
