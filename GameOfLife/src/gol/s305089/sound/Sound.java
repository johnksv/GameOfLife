/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.s305089.sound;

/**
 *
 * @author John Kasper
 */
public class Sound {

    public enum Tone {
        //http://copyright.lenardaudio.com/laidesign/images/a03/a03_kb.gif
        A2(55), B2(61.7), C2(65.4), D2(73.4), E2(82.4), F2(87.3), G2(97.9),
        A3(110), B3(123.47), C3(130.8), D3(146.83), E3(164.8), F3(174.6), G3(196),
        A4(220), B4(246.94), C4(261.63), D4(293.66), E4(329.63), F4(349.23), G4(392),
        A5(440), B5(493.88), C5(523.25), D5(587.33), E5(659.26), F5(698.46), G5(783.99);
        private final double frequency;

        private Tone(double frequency) {
            this.frequency = frequency;
        }

        public double getFrequency() {
            return frequency;
        }
    }

    public static double makeTone(double frequency, long frameCounter, int sampleRate) {
        return Math.sin(2.0 * Math.PI * frequency * frameCounter / sampleRate);
    }
}
