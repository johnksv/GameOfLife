/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
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

    private Board activeBoard;
    private final List<MediaPlayer> mediaPlayerQueue = new ArrayList<>();
    private MediaView mediaview = new MediaView();
    Stats stats = new Stats();

    private Media one;
    private Media five;
    private Media ten;
    private Media twenty;
    private Media intens1;
    private Media intens2;
    private Media intens3;
    private Media newGen;
    private Media single;
    private boolean playing = false;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initMediaFiles();
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
        one = new Media(new File("src/gol/s305089/sound/files/1.wav").toURI().toString());
        five = new Media(new File("src/gol/s305089/sound/files/5.wav").toURI().toString());
        ten = new Media(new File("src/gol/s305089/sound/files/10.wav").toURI().toString());
        twenty = new Media(new File("src/gol/s305089/sound/files/20.wav").toURI().toString());
        intens1 = new Media(new File("src/gol/s305089/sound/files/hexagon.wav").toURI().toString());
        intens2 = new Media(new File("src/gol/s305089/sound/files/pentagon.wav").toURI().toString());
        intens3 = new Media(new File("src/gol/s305089/sound/files/awesome.wav").toURI().toString());
        newGen = new Media(new File("src/gol/s305089/sound/files/sfx_CreateCompound.wav").toURI().toString());
        single = new Media(new File("src/gol/s305089/sound/files/sfx_Popup.wav").toURI().toString());
    }

    public void playSound() {
        if (!playing && rbAutoSelect.isSelected()) {
            playing = true;
            parseBoardBB();
            playMediaQueue(false);
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
                playMediaQueue(true);
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
            playMediaQueue(true);
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
        if (countSameChar == 1) {
            mediaPlayerQueue.add(new MediaPlayer(one));
        } else if (countSameChar <= 5) {
            mediaPlayerQueue.add(new MediaPlayer(five));
        } else if (countSameChar <= 10) {
            mediaPlayerQueue.add(new MediaPlayer(ten));
        } else {
            mediaPlayerQueue.add(new MediaPlayer(twenty));
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
        mediaPlayerQueue.add(new MediaPlayer(intens3));
    }

    private void playMediaQueue(boolean updateTimeLabel) {
        for (int i = 0; i < mediaPlayerQueue.size() - 1; i++) {
            MediaPlayer current = mediaPlayerQueue.get(i);
            MediaPlayer next = mediaPlayerQueue.get(i + 1);

            //When current mediaplayer finish, start the next one.
            current.setOnEndOfMedia(() -> {
                next.play();
                mediaview.setMediaPlayer(next);
                setTimeLabel(next, updateTimeLabel);
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
        if (mediaPlayerQueue.size() == 1) {
            onEndLastMP(mediaPlayerQueue.get(0));
        }
        mediaview.setMediaPlayer(mediaPlayerQueue.get(0));
        setTimeLabel(mediaPlayerQueue.get(0), updateTimeLabel);
        mediaPlayerQueue.get(0).play();
        
        //Updates the buttons for audio control
        btnPlayPause.setDisable(false);
        btnPlayPause.setText("Pause");
        
    }

    private void onEndLastMP(MediaPlayer current) {
        current.setOnEndOfMedia(() -> {
            playing = false;
            labelTime.setText("");
            btnPlayPause.setText("Play");
            btnPlayPause.setDisable(true);
            current.dispose();
            mediaPlayerQueue.clear();
        });
    }

    private void setTimeLabel(MediaPlayer current, boolean updateTimeLabel) {
        if (updateTimeLabel) {
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
