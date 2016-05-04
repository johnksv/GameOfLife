package gol.svergja.controller;

import gol.model.Board.Board;
import gol.svergja.Util;
import static gol.svergja.Util.showTooltip;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;

import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author s305089 - John Kasper Svergja
 */
public class SoundController implements Initializable {

    @FXML
    private VBox vboxUser;
    @FXML
    private AnchorPane ancPaneGenerated;
    @FXML
    private VBox wavMaker;
    @FXML
    private WavMakerController wavMakerController;
    @FXML
    private Button btnPlayPause;
    @FXML
    private Button btnRewind;
    @FXML
    private Label labelTime;
    @FXML
    private Label labelLocation;
    @FXML
    private RadioButton rbDynamic;
    @FXML
    private RadioButton rbSavePlayback;
    @FXML
    private RadioButton rbFromClips;

    //The board that is actually being used in main window.
    private Board activeBoard;

    //Allow only one uniqe value
    private final Set<AudioClip> audioClipQueue = new HashSet<>();
    //Use mediaplayer for selected files, beacuse this is more realiable on larger files.
    private final List<MediaPlayer> mediaPlayerQueue = new ArrayList<>();
    private final MediaView mediaview = new MediaView();

    private AudioClip drumBass;
    private AudioClip drumSnare;
    private AudioClip Db3;
    private AudioClip FSharp3;
    private AudioClip E3;
    private AudioClip E4;
    private AudioClip nextGen;
    private boolean playing = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initMediaFiles();
        initFXMLControls();
    }

    /**
     *
     */
    public void playSound() {
        if (!playing) {
            if (rbDynamic.isSelected() && rbFromClips.isSelected()) {
                audioClipQueue.clear();
                playing = true;
                parseBoard1D();
                playAudioQueue();

                //Nasty, but probably best way.Use of timeline would be overkill. 
                Thread th = new Thread(() -> {
                    try {
                        //The duration of the Audioclips are ca 0.8 sec.
                        Thread.sleep(800);
                    } catch (InterruptedException ex) {
                        System.out.println("Not able to sleep..\n " + ex);
                    }
                    playing = false;
                });
                th.start();
            }
        }
    }

    public void setBoard(Board boardToSet) {
        this.activeBoard = boardToSet;
        this.activeBoard.setRule(boardToSet.getRule());
        wavMakerController.setBoard(boardToSet);
    }

    public void disposeMediaPlayers() {
        playing = false;
        for (MediaPlayer mediaPlayer : mediaPlayerQueue) {
            mediaPlayer.dispose();
        }
        mediaPlayerQueue.clear();
    }

    private void initFXMLControls() {
        rbDynamic.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            disposeMediaPlayers();
            ancPaneGenerated.setVisible(newValue);
            vboxUser.setVisible(!newValue);
        });
        rbFromClips.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            wavMaker.setDisable(newValue);
        });

    }

    private void initMediaFiles() {
        drumBass = new AudioClip(new File("src/gol/svergja/sound/files/drumBass.wav").toURI().toString());
        drumSnare = new AudioClip(new File("src/gol/svergja/sound/files/drumSnare.wav").toURI().toString());
        nextGen = new AudioClip(new File("src/gol/svergja/sound/files/nextGen.wav").toURI().toString());
        Db3 = new AudioClip(new File("src/gol/svergja/sound/files/Db3.wav").toURI().toString());
        FSharp3 = new AudioClip(new File("src/gol/svergja/sound/files/F3#.wav").toURI().toString());
        E3 = new AudioClip(new File("src/gol/svergja/sound/files/E3.wav").toURI().toString());
        E4 = new AudioClip(new File("src/gol/svergja/sound/files/E4.wav").toURI().toString());
    }

    @FXML
    private void handleSelectFolder() {
        disposeMediaPlayers();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File result = directoryChooser.showDialog(null);
        if (result != null && result.isDirectory()) {
            File[] musicFiles = result.listFiles((File dir, String name) -> {
                //Check if file ends with wav or mp3
                boolean wav = name.toLowerCase().endsWith(".wav");
                boolean mp3 = name.toLowerCase().endsWith(".mp3");
                return wav || mp3;
            });
            if (musicFiles.length == 0) {
                labelLocation.setText("No music files found. Please choose an directory that contains at least one music file.");
            } else {
                labelLocation.setText("Playing from: " + result.getAbsolutePath());
                for (File musicFile : musicFiles) {
                    mediaPlayerQueue.add(new MediaPlayer(new Media(musicFile.toURI().toString())));
                }
                playMediaQueue();
            }
        }
    }

    @FXML
    private void handleSelectFile() {
        disposeMediaPlayers();

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Music file", "*.mp3", "*.wav"),
                new FileChooser.ExtensionFilter("All files", "*.*"));
        File result = fileChooser.showOpenDialog(null);
        if (result != null) {
            labelLocation.setText("Playing: " + result.getAbsolutePath());
            mediaPlayerQueue.add(new MediaPlayer(new Media(result.toURI().toString())));
            playMediaQueue();
        }

    }

    @FXML
    private void handlePlayPause() {
        Status currentStatus = mediaview.getMediaPlayer().getStatus();
        if (currentStatus == Status.PLAYING || currentStatus == Status.STALLED) {
            btnPlayPause.setText("Play");
            mediaview.getMediaPlayer().pause();
        }
        if (currentStatus == Status.PAUSED) {
            btnPlayPause.setText("Pause");
            mediaview.getMediaPlayer().play();
        }
    }

    @FXML
    private void rewindCurrent() {
        mediaview.getMediaPlayer().seek(Duration.ZERO);
    }

    private void assignSound(int countSameChar) {
        if (countSameChar == -1) {
            audioClipQueue.add(nextGen);
        } else if (countSameChar == 1) {
            audioClipQueue.add(drumSnare);
        } else if (countSameChar == 2) {
            audioClipQueue.add(E3);
        } else if (countSameChar == 3) {
            audioClipQueue.add(drumBass);
        } else if (countSameChar <= 5) {
            audioClipQueue.add(E4);
        } else if (countSameChar <= 7) {
            audioClipQueue.add(FSharp3);
        } else {
            audioClipQueue.add(Db3);
        }
    }

    /**
     * Parses the board in 1d. Row by row. Could have (and should) used
     * toString.
     */
    private void parseBoard1D() {
        byte[][] current = activeBoard.getBoundingBoxBoard();

        int countOnRow = 0;
        for (byte[] row : current) {
            for (int j = 0; j < row.length; j++) {
                //If last element on row is alive.
                if (row[j] == 64 && j == row.length - 1) {
                    countOnRow++;
                    assignSound(countOnRow);
                } else if (row[j] == 64) {
                    countOnRow++;
                } else if (countOnRow > 0) {
                    assignSound(countOnRow);
                    countOnRow = 0;
                }
            }
            countOnRow = 0;
        }

        //Next gen
        assignSound(-1);
    }

    private void playAudioQueue() {
        for (AudioClip audioClip : audioClipQueue) {
            audioClip.play();
        }
    }

    private void playMediaQueue() {
        for (int i = 0; i < mediaPlayerQueue.size() - 1; i++) {
            MediaPlayer current = mediaPlayerQueue.get(i);
            MediaPlayer next = mediaPlayerQueue.get(i + 1);

            //When current mediaplayer finish, start the next one.
            current.setOnEndOfMedia(() -> {
                next.play();
                mediaview.setMediaPlayer(next);
                Util.setTimeLabel(next, labelTime);
                current.dispose();

            });
            current.setOnError(() -> {
                System.err.println("Could not play file: \n" + current.getError());
            });
            //For the last mediaplayer, we want to dispose and clear the list.
            if (i == mediaPlayerQueue.size() - 2) {
                onEndLastMP(current);
            }
        }

        //First mediaplayer is not part of loop. Has to start it explicitly.
        mediaview.setMediaPlayer(mediaPlayerQueue.get(0));
        Util.setTimeLabel(mediaPlayerQueue.get(0), labelTime);
        mediaPlayerQueue.get(0).play();
        if (mediaPlayerQueue.size() == 1) {
            onEndLastMP(mediaPlayerQueue.get(0));
        }

        //Updates the buttons for audio control
        btnRewind.setDisable(false);
        btnPlayPause.setDisable(false);
        btnPlayPause.setText("Pause");

    }

    private void onEndLastMP(MediaPlayer current) {
        current.setOnEndOfMedia(() -> {
            labelTime.setText("");
            btnPlayPause.setText("Play");
            btnPlayPause.setDisable(true);
            btnRewind.setDisable(true);
            disposeMediaPlayers();
        });
    }
}
