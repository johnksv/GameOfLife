/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.s305089.controller;

import gol.model.Board.Board;
import gol.s305089.sound.Sound;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import gol.s305089.sound.WavFile;
import gol.s305089.sound.WavFileException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * FXML Controller class
 *
 * @author s305089
 */
public class SoundController implements Initializable {

    Board activeBoard;

    Media intens1;
    Media intens2;
    Media intens3;
    Media newGen;
    Media single;
    MediaPlayer mediaplayer;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initMediaFiles();

    }

    public void initMediaFiles() {
        try {
            intens1 = new Media(new File("gol/s305089/sound/files/hexagon.wav").toURI().toURL().toString());
            intens2 = new Media(new File("gol/s305089/sound/files/pentagon.wav").toURI().toURL().toString());
            intens3 = new Media(new File("gol/s305089/sound/files/awesome.wav").toURI().toURL().toString());
            newGen = new Media(new File("gol/s305089/sound/files/sfx_CreateCompound.wav").toURI().toURL().toString());
            single = new Media(new File("gol/s305089/sound/files/sfx_Popup.wav").toURI().toURL().toString());
        } catch (MalformedURLException ex) {
            Logger.getLogger(SoundController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void playSound() {

        char[] currString = activeBoard.toString().toCharArray();
        int countSameChar = 0;
        char lastChar;
        char currentChar = 'a';
        for (int i = 0; i < currString.length; i++) {
            if (i == 0) {
                lastChar = currString[i];
            } else {
                lastChar = currString[i - 1];
                currentChar = currString[i];
            }
            if (lastChar == currentChar) {
                countSameChar++;
            } else if (countSameChar > 3) {
                mediaplayer = new MediaPlayer(intens3);
                mediaplayer.play();
            }
        }

    }

    public void setBoard(Board activeBoard) {
        this.activeBoard = activeBoard;
    }

}
