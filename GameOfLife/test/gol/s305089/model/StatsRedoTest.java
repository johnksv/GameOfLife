package gol.s305089.model;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
import gol.model.FileIO.PatternFormatException;
import gol.model.FileIO.ReadFile;
import gol.other.Configuration;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 *
 * @author s305089 - John Kasper Svergja
 */
public class StatsRedoTest {

    public StatsRedoTest() {
    }

    @Before
    public void setUp() {
        Configuration.loadConfig();
        instance = new StatsRedo();
        patternBoard = new ArrayBoard(6, 6);
        patternBoard.insertArray(new byte[][]{{0, 64, 0}, {0, 64, 0}, {0, 64, 0}});
        pattern2Board = new ArrayBoard(6, 6);
        pattern2Board.insertArray(new byte[][]{{64, 64, 0}, {0, 64, 0}, {0, 64, 0}});
        instance.setBoard(patternBoard);
    }
    StatsRedo instance;
    Board patternBoard;
    Board pattern2Board;

    @Rule
    public ExpectedException expectedExeption = ExpectedException.none();

    @Test
    public void testGetStatistics() {
        System.out.println("getStatistics");

        instance.setBoard(patternBoard);
        int[][] result = instance.getStatistics(20, true, true);

        for (int iteration = 0; iteration < result.length - 1; iteration++) {

            assertEquals(3, result[iteration][0]);
            assertEquals(0, result[iteration][1]);

            //For each generation, there should be a 100% match with another generation
            //unless for the last. Only checking for generation in future.
            if (iteration < result.length - 3) {
                assertEquals(100, result[iteration][2], 0.0005);
            } else if (iteration == result.length - 1) {
                assertEquals(0, result[iteration][2], 0.0005);
            }
        }
    }

    @Test
    public void testTuringMachineStats() throws IOException, PatternFormatException {
        //Test the speed
        System.out.println("Testing executing time of TuringMachine (check test result for result)");
        File url = new File("test/patternTestFiles/turingmachine.rle");
        Path file = Paths.get(url.toURI());
        byte[][] byteBoard = ReadFile.readFileFromDisk(file);
        Board arrayBoard = new ArrayBoard(2000, 2000);
        arrayBoard.insertArray(byteBoard);
        instance.setBoard(arrayBoard);
        instance.getStatistics(60, true, true);
    }

}
