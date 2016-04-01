/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.model.FileIO;

import java.io.File;
import java.net.URI;
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
 * @author s305054, s305084, s305089
 */
public class ReadFileTest {

    public ReadFileTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testReadFileFromDisk() throws Exception {
        System.out.println("readFileFromDisk");
        File url;
        Path file;
        byte[][] expResult;
        byte[][] result;

        
        url = new File("test/patternTestFiles/glider.cells");
        file = Paths.get(url.toURI());
        expResult = new byte[][]{{0, 64, 0}, {0, 0, 64}, {64, 64, 64}};
        result = ReadFile.readFileFromDisk(file);
        assertArrayEquals(expResult, result);

        url = new File("test/patternTestFiles/glider.rle");
        file = Paths.get(url.toURI());
        result = ReadFile.readFileFromDisk(file);
        assertArrayEquals(expResult, result);

        url = new File("test/patternTestFiles/custom.cells");
        file = Paths.get(url.toURI());
        expResult = new byte[][]{{0, 0, 0}, {0, 64, 64}, {0, 64, 64}};
        result = ReadFile.readFileFromDisk(file);
        assertArrayEquals(expResult, result);
        
        url = new File("test/patternTestFiles/custom.rle");
        file = Paths.get(url.toURI());
        result = ReadFile.readFileFromDisk(file);
        assertArrayEquals(expResult, result);

    }
}
