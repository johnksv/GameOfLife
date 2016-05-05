package gol.stensli;

import gol.stensli.WriteRLE;
import gol.model.Board.Board;
import gol.model.Board.DynamicBoard;
import gol.model.FileIO.PatternFormatException;
import gol.model.FileIO.ReadFile;
import gol.other.Configuration;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author s305084 - Stian Stensli
 */
public class WriteRLETest {

    public WriteRLETest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        Configuration.loadConfig();
    }

    @After
    public void tearDown() {
    }

    /**
     * <p>
     * Test for  
     * </p>
     * Important note: This test relay upon readRLE to work.
     */
    @Test
    public void testToRLE() {
        try {
            System.out.println("toRLE");
            Path path = Paths.get("test.rle");
            Board board = new DynamicBoard(100, 100);
            board.setCellState(5, 6, true);
            board.setCellState(8, 13, true);
            board.setCellState(5, 7, true);
            board.setCellState(31, 2, true);

            WriteRLE.toRLE(path, board, "", "", "");

            assertArrayEquals(board.getBoundingBoxBoard(), ReadFile.readFileFromDisk(path));

            path.toFile().delete();
        } catch (PatternFormatException ex) {
            fail("A PatternFormatException occurred...");
        } catch (IOException ex) {
            fail("A IOException occurred...");
        }
    }

}
