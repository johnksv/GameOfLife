package gol.s305089.sound;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author s305089
 */
public class Sound {

    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNELS = 2;
    private static boolean CLIPPING = false;
    
    /**
     * Each element represents one step. In the child element
     * (<code>ArrayList type double[]</code>) each element represents one tone.
     * In the <code>double[]</code>, the elements are of the following:
     * Amplitude, Frequency, Channel.
     */
    private static final ArrayList<ArrayList<double[]>> SoundContainer = new ArrayList<>();

    /**
     * Adds a frequency to both channels, at the given step, with amplitude 0.5.
     * This is the same as calling {@link #addToSequence(int, double, double, int)},
     * with the parameter amplitude = 0.5, and channels = 2.
     *
     * @see #addToSequence(int, double, double, int)
     */
    public static void addToSequence(int iteration, double frequency) {
        addToSequence(iteration, frequency, 0.5, CHANNELS);
    }

    /**
     * Adds a frequency with this amplitude to both channels, at the given step.
     * This is the same as calling {@link #addToSequence(int, double, double, int)},
     * with the parameter channels = 2.
     *
     * @see #addToSequence(int, double, double, int)
     */
    public static void addToSequence(int iteration, double frequency, double amplitude) {
        addToSequence(iteration, frequency, amplitude, CHANNELS);
    }

    /**
     * Adds a frequency with this amplitude to the given channel, at the given
     * step. For use with {@link #makeSound(java.io.File, int)}. Frequency of
     * tones can be found at http://www.phy.mtu.edu/~suits/notefreqs.html .
     *
     * @param step The step/iteration the frequency and amplitude should be
     * assigned to. Must be larger than 0. The duration of the step is decided
     * when calling on {@link #makeSound(java.io.File, int)}.
     * @param frequency A frequency between min 16.35 Hz and max 7902.13 hz.
     * Values that is not in this domain will be sat to the closest legal
     * frequency.
     * @param amplitude The amplitude/force of the frequency. 1 is max and
     * loudest. 0 is min and silent. Values over 1 is sat to 1. Values under 0
     * is sat to 0.
     * @param channel Which (stereo)channel the tone should be added to. 0 for
     * left side, 1 for right side, 2 for both sides. Invalid values will be
     * corrected to use both sides.
     */
    public static void addToSequence(int step, double frequency, double amplitude, int channel) {
        if (step < 0) {
            throw new IllegalArgumentException("Iteration must be 0 or larger");
        }
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
        double[] tone = new double[]{amplitude, frequency, channel};
        if (SoundContainer.size() - 1 < step) {
            for (int i = SoundContainer.size() - 1; i < step; i++) {
                ArrayList<double[]> itt = new ArrayList<>();
                SoundContainer.add(itt);
            }
        }

        SoundContainer.get(step).add(tone);

    }

    /**
     * Produce a sound file based on the content that is added with
     * {@link #addToSequence(int, double, double, int)}. Much of the basics of this
     * method is copied from the library example at
     * http://www.labbookpages.co.uk/audio/javaWavFiles.html . I.e. construct
     * wavfile, count frames, remaining frames, loop until frames is filled up.
     * Adjustments have been made, to avoid clipping, and therefor support
     * multiple tones on each frame.
     *
     * @param saveLoc The file representation of the save location.
     * @param stepDuration Duration of each step measured in seconds
     */
    //TODO DurIte to double for sounds under 1 sec
    public static void makeSound(File saveLoc, double stepDuration) {
        try {
            int iterations = SoundContainer.size();
            // Calculate the number of frames required for specified duration
            long numFrames = (long) (stepDuration * iterations * SAMPLE_RATE);
            long framesEachIt = (long) (stepDuration * SAMPLE_RATE);

            //Bit depth of 16.
            WavFile wavFile = WavFile.newWavFile(saveLoc, CHANNELS, numFrames, 16, SAMPLE_RATE);

            double[][] buffer = new double[CHANNELS][100];

            for (int i = 0; i < iterations; i++) {
                long frameCounter = 0;
                while (frameCounter < framesEachIt) {

                    // Determine how many frames to write, up to a maximum of the buffer size
                    long remaining = wavFile.getFramesRemaining() - framesEachIt * (iterations - 1 - i);
                    int toWrite = (remaining > 100) ? 100 : (int) remaining;

                    //TODO: Last iterations will probably cut down?
                    for (int s = 0; s < toWrite; s++, frameCounter++) {
                        for (double[] current : SoundContainer.get(i)) {
                            //TODO: Add e^(-x) for fade out and e^x for fade in
                            switch ((int) current[2]) {
                                case 0:
                                    buffer[0][s] += current[0] * Math.sin(2.0 * Math.PI * current[1] * frameCounter / SAMPLE_RATE);
                                    break;
                                case 1:
                                    buffer[1][s] += current[0] * Math.sin(2.0 * Math.PI * current[1] * frameCounter / SAMPLE_RATE);
                                    break;
                                default:
                                    buffer[0][s] += current[0] * Math.sin(2.0 * Math.PI * current[1] * frameCounter / SAMPLE_RATE);
                                    buffer[1][s] += current[0] * Math.sin(2.0 * Math.PI * current[1] * frameCounter / SAMPLE_RATE);
                                    break;
                            }
                        }
                        checkClipping(buffer, s);
                    }

                    // Write the buffer
                    wavFile.writeFrames(buffer, toWrite);
                    //Clean the buffer 
                    buffer = new double[CHANNELS][100];
                }
            }
            wavFile.close();
            if (CLIPPING) {
                System.err.println("The sound was clipped one or many times..");
            }
        } catch (IOException | WavFileException ex) {
            System.err.println("Error making file:\n" + ex);
        }
        SoundContainer.clear();
        CLIPPING = false;
    }

    private static void checkClipping(double[][] buffer, int s) {
        if (buffer[0][s] > 1) {
            buffer[0][s] = 1;
            CLIPPING = true;
        } else if (buffer[0][s] < -1) {
            buffer[0][s] = -1;
            CLIPPING = true;
        }
        if (buffer[1][s] > 1) {
            buffer[1][s] = 1;
            CLIPPING = true;
        } else if (buffer[1][s] < -1) {
            buffer[1][s] = -1;
            CLIPPING = true;
        }
    }
}
