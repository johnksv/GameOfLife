package gol.s305089.sound;

import gol.s305089.controller.SoundController;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author John Kasper
 */
public class MakeSoundDEMO {

    public static void main(String... args) {
        try {
            File file = File.createTempFile("GolSound", ".wav");
            System.out.println(file.getAbsolutePath());
            makeSound(file, 8000, 1);
        } catch (IOException ex) {
            Logger.getLogger(SoundController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //http://www.phy.mtu.edu/~suits/notefreqs.html

    public static void makeSound(File file, int sampleRate, double duration) {
        try {
            // Calculate the number of frames required for specified duration
            long numFrames = (long) (duration * sampleRate);
            // Create a wav file with the name specified as the first argument
            WavFile wavFile = WavFile.newWavFile(file, 2, numFrames, 8, sampleRate);
            // Create a buffer of 100 frames
            double[][] buffer = new double[2][100];
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
                    buffer[0][s] = Sound.makeTone(138.59, frameCounter, sampleRate);
                    buffer[1][s] = Sound.makeTone(138.59, frameCounter, sampleRate);
                }
                // Write the buffer
                wavFile.writeFrames(buffer, toWrite);
            }
        } catch (IOException | WavFileException ex) {
            System.err.println("Error making file:\n" + ex);
        }
    }

}
