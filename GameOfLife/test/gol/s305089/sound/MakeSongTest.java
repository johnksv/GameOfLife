/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.s305089.sound;

import gol.s305089.model.sound.Sound;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.Before;
import org.junit.Test;
import gol.s305089.model.sound.Tone;
import static org.junit.Assert.*;

/**
 *
 * @author John Kasper
 */
public class MakeSongTest {

    public MakeSongTest() {
    }

    @Before
    public void setUp() {
    }

    @Test
    public void testMakeTones() throws IOException {
        System.out.println("makeSound");
        Sound.addToSequence(0, Tone.C4.getFreq(), 0.8);
        Sound.addToSequence(1, Tone.D4.getFreq(), 0.8);
        Sound.addToSequence(2, Tone.E4.getFreq(), 0.8);
        Sound.addToSequence(3, Tone.F4.getFreq(), 0.8);
        Sound.addToSequence(4, Tone.G4.getFreq(), 0.7);
        Sound.addToSequence(5, Tone.G4.getFreq(), 1);
        Sound.addToSequence(6, Tone.G4.getFreq(), 0.6);
        Sound.addToSequence(7, Tone.G4.getFreq(), 0.6);
        Sound.addToSequence(9, Tone.A4.getFreq(), 0.8);
        Sound.addToSequence(10, Tone.A4.getFreq(), 0.7);
        Sound.addToSequence(11, Tone.A4.getFreq(), 0.8);
        Sound.addToSequence(12, Tone.A4.getFreq(), 0.8);
        Sound.addToSequence(13, Tone.G4.getFreq(), 0.3);

        File saveLoc = Files.createTempFile("golSoundTest", ".wav").toFile();
        double durIte = 0.5;
        Sound.makeSound(saveLoc, durIte);
        System.out.println(saveLoc.getAbsoluteFile());

    }

    @Test
    public void testMakeAccord() throws IOException {
        System.out.println("MakeAccord");
        Sound.addToSequence(0, Tone.G3.getFreq(), 0.8);
        Sound.addToSequence(0, Tone.B3.getFreq());
        Sound.addToSequence(0, Tone.D4.getFreq());
        Sound.addToSequence(1, Tone.C4.getFreq(), 0.8);
        Sound.addToSequence(1, Tone.E4.getFreq());
        Sound.addToSequence(1, Tone.G3.getFreq());
        Sound.addToSequence(2, Tone.F3.getFreq(), 0.7);
        Sound.addToSequence(2, Tone.A3.getFreq());
        Sound.addToSequence(2, Tone.A4.getFreq());
        Sound.addToSequence(2, Tone.F5.getFreq());

        File saveLoc = Files.createTempFile("golSoundTest", ".wav").toFile();
        double durIte = 1;
        Sound.makeSound(saveLoc, durIte);
        System.out.println(saveLoc.getAbsoluteFile());

    }

}
