package gol.s305089.controller;

import gol.model.Board.Board;
import gol.s305089.model.Stats;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.HBox;
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
 * @author s305089
 */
public class SoundController implements Initializable {

    @FXML
    private HBox hBoxAuto;
    @FXML
    private VBox vBoxUser;
    @FXML
    private Button btnPlayPause;
    @FXML
    private Label labelTime;
    @FXML
    private Label labelLocation;
    @FXML
    private RadioButton rbAutoSelect;

    //The board that is actually being displayed in main window.
    private Board activeBoard;

    private final List<AudioClip> audioClipQueue = new ArrayList<>();
    //Use mediaplayer for selected files, beacuse this is more realiable on larger files.
    private final List<MediaPlayer> mediaPlayerQueue = new ArrayList<>();
    private final MediaView mediaview = new MediaView();
    Stats stats = new Stats();

    private AudioClip one;
    private AudioClip five;
    private AudioClip ten;
    private AudioClip twenty;
    private AudioClip nextGen;
    private AudioClip Db3;
    private AudioClip F3Sharp;
    private AudioClip E3;
    private AudioClip E4;
    private boolean playing = false;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initMediaFiles();
        initFXMLControls();
    }

    private void initFXMLControls() {
        rbAutoSelect.selectedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            disposeMediaPlayers();
            if (newValue) {
                hBoxAuto.setVisible(true);
                vBoxUser.setVisible(false);
            } else {
                hBoxAuto.setVisible(false);
                vBoxUser.setVisible(true);
            }
        });
    }

    public void initMediaFiles() {
        one = new AudioClip(new File("src/gol/s305089/sound/files/1.wav").toURI().toString());
        five = new AudioClip(new File("src/gol/s305089/sound/files/5.wav").toURI().toString());
        ten = new AudioClip(new File("src/gol/s305089/sound/files/10.wav").toURI().toString());
        twenty = new AudioClip(new File("src/gol/s305089/sound/files/20.wav").toURI().toString());
        nextGen = new AudioClip(new File("src/gol/s305089/sound/files/nextGen.wav").toURI().toString());
        Db3 = new AudioClip(new File("src/gol/s305089/sound/files/Db3.wav").toURI().toString());
        F3Sharp = new AudioClip(new File("src/gol/s305089/sound/files/F3#.wav").toURI().toString());
        E3 = new AudioClip(new File("src/gol/s305089/sound/files/E3.wav").toURI().toString());
        E4 = new AudioClip(new File("src/gol/s305089/sound/files/E4.wav").toURI().toString());
    }

    public void playSound() {
        audioClipQueue.clear();
        if (!playing && rbAutoSelect.isSelected()) {
            playing = true;
            parseBoardBB();
            playAudioQueue();
            playing = false;
        }

    }

    @FXML
    private void handleSelectFolder() {
        disposeMediaPlayers();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File result = directoryChooser.showDialog(null);
        if (result != null && result.isDirectory()) {
            File[] musicFiles = result.listFiles((File dir, String name) -> {
                boolean wav = name.toLowerCase().endsWith(".wav");
                boolean mp3 = name.toLowerCase().endsWith(".mp3");
                return wav || mp3;
            });
            if (musicFiles.length == 0) {
                labelLocation.setText("No music files found. Pleas choose an directory that contains at least one music file.");
            } else {
                labelLocation.setText("Playing from: " + result.getAbsolutePath());
                for (File musicFile : musicFiles) {
                    System.out.println(musicFile);
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
        //Current generation
        int gen = audioClipQueue.size() - 1;

        if (countSameChar == -1) {
            audioClipQueue.add(nextGen);
        } else if (countSameChar > 0 && countSameChar <= 3) {
            System.out.println("E4: count: " + countSameChar);
            audioClipQueue.add(E4);
        } else if (countSameChar <= 5) {
            System.out.println("E3: count: " + countSameChar);
            audioClipQueue.add(E3);
        } else if (countSameChar <= 10) {
            System.out.println("B3: count: " + countSameChar);
            audioClipQueue.add(F3Sharp);
        } else {
            System.out.println("Db3: count: " + countSameChar);
            audioClipQueue.add(Db3);
        }
    }

    private void parseBoardBB() {
        byte[][] current = activeBoard.getBoundingBoxBoard();

        int countOnRow = 0;
        for (int i = 0; i < current.length; i++) {
            for (int j = 0; j < current[i].length; j++) {
                if (current[i][j] == 64 && j == current[i].length - 1) {
                    //Last row
                    countOnRow++;
                    assignSound(countOnRow);
                } else if (current[i][j] == 64) {
                    countOnRow++;
                } else if (countOnRow > 0) {
                    assignSound(countOnRow);
                    countOnRow = 0;
                }
            }
            countOnRow = 0;
        }

        //Next gen sound should be played alone
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
                setTimeLabel(next);
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
        setTimeLabel(mediaPlayerQueue.get(0));
        mediaPlayerQueue.get(0).play();
        if (mediaPlayerQueue.size() == 1) {
            onEndLastMP(mediaPlayerQueue.get(0));
        }

        //Updates the buttons for audio control
        btnPlayPause.setDisable(false);
        btnPlayPause.setText("Pause");

    }

    private void onEndLastMP(MediaPlayer current) {
        current.setOnEndOfMedia(() -> {
            labelTime.setText("");
            btnPlayPause.setText("Play");
            btnPlayPause.setDisable(true);
            disposeMediaPlayers();
        });
    }

    private void setTimeLabel(MediaPlayer current) {
        current.currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
            //Current time elapsed
            int secondsElapsed = (int) newValue.toSeconds();
            int minuts = secondsElapsed / 60;
            int seconds = secondsElapsed - minuts * 60;
            String elapsedTime = minuts + ":";
            if (seconds < 10) {
                elapsedTime += "0" + seconds;
            } else {
                elapsedTime += seconds;
            }

            //Total time of whole mediafile
            int totalDur = (int) current.getTotalDuration().toSeconds();
            int totalMin = totalDur / 60;
            int totalSec = totalDur - totalMin * 60;
            String totalTime = totalMin + ":";
            if (totalSec < 10) {
                totalTime += "0" + totalSec;
            } else {
                totalTime += totalSec;
            }

            labelTime.setText(elapsedTime + "/" + totalTime);
        });
    }

    public void setBoard(Board activeBoard) {
        this.activeBoard = activeBoard;
    }

    public void disposeMediaPlayers() {
        playing = false;
        for (MediaPlayer mediaPlayer : mediaPlayerQueue) {
            mediaPlayer.dispose();
        }
        mediaPlayerQueue.clear();
    }

}
