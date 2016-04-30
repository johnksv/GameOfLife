package gol.s305089.sound;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author s305089
 */
public class MakeSong {

    private ArrayList<ArrayList<int[]>> song = new ArrayList<>();

    public void addToSong(int iteration, int frequency, int amplitude) {
        ArrayList<int[]> tone = new ArrayList<>();
        tone.add(new int[]{frequency, amplitude});
        song.add(iteration, tone);
    }
    
    public void makeSound(File saveLocation, int durationIteration){
        try{
            
        }catch(){
            
        }
    }

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
