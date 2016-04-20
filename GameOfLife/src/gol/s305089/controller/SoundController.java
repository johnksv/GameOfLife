/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.s305089.controller;

import gol.s305089.sound.Sound;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import gol.s305089.sound.WavFile;
import gol.s305089.sound.WavFileException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void makeWav() {

    }

    public static void main(String... args) {
        try {
            File file = File.createTempFile("GolSound", ".wav");
            System.out.println(file.getAbsolutePath());
            makeSound(file, 44100, 5);

        } catch (IOException ex) {
            Logger.getLogger(SoundController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void makeSound(File file, int sampleRate, double duration) {
        try {
            // Calculate the number of frames required for specified duration
            long numFrames = (long) (duration * sampleRate);

            // Create a wav file with the name specified as the first argument
            WavFile wavFile = WavFile.newWavFile(file, 4, numFrames, 8, sampleRate);

            // Create a buffer of 100 frames
            double[][] buffer = new double[4][100];

            // Initialise a local frame counter
            long frameCounter = 0;

            // Loop until all frames written
            while (frameCounter < numFrames) {

                // Determine how many frames to write, up to a maximum of the buffer size
                long remaining = wavFile.getFramesRemaining();
                int toWrite = (remaining > 100) ? 100 : (int) remaining;

                // Fill the buffer, one tone per channel
                    
                for (int s = 0; s < toWrite; s++, frameCounter++) {
                    //Math.sin(2.0 * Math.PI * tone.getFrequency() * frameCounter / sampleRate)
                        buffer[0][s] = Sound.makeTone(Sound.Tone.A2, frameCounter, sampleRate);
                        buffer[1][s] = Sound.makeTone(Sound.Tone.A5, frameCounter, sampleRate);
                        buffer[2][s] = Sound.makeTone(Sound.Tone.G4, frameCounter, sampleRate);
                        buffer[3][s] = Sound.makeTone(Sound.Tone.G4, frameCounter, sampleRate);
                        
                }

                // Write the buffer
                wavFile.writeFrames(buffer, toWrite);

            }
        } catch (IOException | WavFileException ex) {
            System.err.println("Error making file:\n" + ex);
        }
    }

}
