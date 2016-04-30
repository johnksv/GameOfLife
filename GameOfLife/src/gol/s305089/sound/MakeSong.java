package gol.s305089.sound;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author s305089
 */
public class MakeSong {

    private static final int SAMPLE_RATE = 44100;
    private static final ArrayList<ArrayList<double[]>> song = new ArrayList<>();

    /**
     * Adds a frequency with amplitude to this iteration. For use with
     * {@link #makeSound(java.io.File, int)}. Frequency of tones can be found at
     * http://www.phy.mtu.edu/~suits/notefreqs.html.
     *
     * @param iteration The iteration the frequency and amplitude should be
     * assigned to
     * @param frequency A frequency between min 16.35 Hz and max 7902.13 hz.
     * Values that is not in this domain will be sat to the closest legal
     * frequency.
     * @param amplitude The amplitude/force of the frequency. 1 is max and
     * loudest. 0 is min and silent. Values over 1 is sat to 1. Values under 0
     * is sat to 0.
     */
    public static void addToSong(int iteration, double frequency, double amplitude) {
        if (amplitude > 1) {
            amplitude = 1;
        }
        if (amplitude < 0) {
            amplitude = 0;
        }
        if (frequency < 16.35) {
            frequency = 16.35;
        }
        if (frequency > 7902.13) {
            frequency = 7902.13;
        }
        double[] tone = new double[]{amplitude, frequency};
        if (song.size() - 1 < iteration) {
            for (int i = song.size() - 1; i < iteration; i++) {
                ArrayList<double[]> itt = new ArrayList<>();
                song.add(itt);
            }
        }

        song.get(iteration).add(tone);

    }

    public static void makeSound(File saveLoc, int durIte) {
        try {
            int iterations = song.size();
            // Calculate the number of frames required for specified duration
            long numFrames = (long) (durIte * iterations * SAMPLE_RATE);
            long framesEachIt = (long) (durIte * SAMPLE_RATE);
            // Create a wav file with the name specified as the first argument
            WavFile wavFile = WavFile.newWavFile(saveLoc, 2, numFrames, 8, SAMPLE_RATE);
            // Create a buffer of 100 frames
            double[][] buffer = new double[2][100];

            for (int i = 0; i < iterations; i++) {
                // Initialise a local frame counter
                long frameCounter = 0;
                // Loop until all frames written
                while (frameCounter < framesEachIt) {
                    // Determine how many frames to write, up to a maximum of the buffer size
                    long remaining = wavFile.getFramesRemaining() - framesEachIt * (iterations - 1 - i);
                    int toWrite = (remaining > 100) ? 100 : (int) remaining;

                    // Fill the buffer, one tone per channel
                    //TODO: Last iterations will probably cut down?
                    for (int s = 0; s < toWrite; s++, frameCounter++) {
                        for (double[] current : song.get(i)) {
                            //Something wierd happens with +0....
                            buffer[0][s] = current[0] * Math.sin(2.0 * Math.PI * current[1] * frameCounter / SAMPLE_RATE);
                            buffer[1][s] = current[0] * Math.sin(2.0 * Math.PI * current[1] * frameCounter / SAMPLE_RATE);
                        }
                    }
                    // Write the buffer
                    wavFile.writeFrames(buffer, toWrite);
                }
            }
        } catch (IOException | WavFileException ex) {
            System.err.println("Error making file:\n" + ex);
        }
    }
}
