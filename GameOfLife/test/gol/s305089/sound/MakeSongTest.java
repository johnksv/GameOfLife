/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.s305089.sound;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.Before;
import org.junit.Test;
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
    public void testMakeSound() throws IOException {
        System.out.println("makeSound");
        MakeSong.addToSong(0, 261.63, 0.9);
        MakeSong.addToSong(1, 293.66, 0.9);
        MakeSong.addToSong(2, 329.63, 0.5);
        MakeSong.addToSong(3, 349.23, 0.2);


        File saveLoc = Files.createTempFile("golSoundTest", ".wav").toFile();
        int durIte = 1;
        MakeSong.makeSound(saveLoc, durIte);
        System.out.println(saveLoc.getAbsoluteFile());
        fail("The test case is a prototype.");
    }

}
