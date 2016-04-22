/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.s305089.controller;

import gol.model.Board.Board;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * FXML Controller class
 *
 * @author s305089
 */
public class SoundController implements Initializable {

    private Board activeBoard;
    private final List<MediaPlayer> mediaPlayerQueue = new ArrayList<>();

    private Media one;
    private Media five;
    private Media ten;
    private Media twenty;
    private Media intens1;
    private Media intens2;
    private Media intens3;
    private Media newGen;
    private Media single;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initMediaFiles();

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

    public void parseBoardBB() {
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
        mediaPlayerQueue.add(new MediaPlayer(newGen));
    }

    public void playSound() {
        parseBoardBB();
        playMediaQueue();
        mediaPlayerQueue.clear();

    }

    private void playMediaQueue() {
        mediaPlayerQueue.get(0).play();
        for (int i = 0; i < mediaPlayerQueue.size() - 1; i++) {
            final MediaPlayer next = mediaPlayerQueue.get(i + 1);
            mediaPlayerQueue.get(i).setOnEndOfMedia(() -> {
                next.play();
            });
        }
    }

    private void parseBoard() {
        char[] currString = activeBoard.toString().toCharArray();
        int countSameChar = 0;
        char lastChar = '0';
        char currentChar = '0';
        for (int i = 0; i < currString.length; i++) {
            if (i > 1) {
                lastChar = currString[i - 1];
                currentChar = currString[i];
            }

            if (lastChar == currentChar && currentChar == '1') {
                countSameChar++;
            } else {
                if (currentChar == '1') {
                    countSameChar++;
                }
                if (countSameChar > 0) {
                    assignSound(countSameChar);
                    countSameChar = 0;
                }
            }
        }
        mediaPlayerQueue.add(new MediaPlayer(newGen));
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

    public void setBoard(Board activeBoard) {
        this.activeBoard = activeBoard;
    }

}
