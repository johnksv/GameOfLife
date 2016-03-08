/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.model.FileIO;

import java.lang.reflect.Method;
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
        readRLEMethod = ReadFile.class.getDeclaredMethod("readRLE", String[].class);
        readRLEMethod.setAccessible(true);
        readPlainText = ReadFile.class.getDeclaredMethod("readPlainText", String[].class);
        readPlainText.setAccessible(true);
    }

    @After
    public void tearDown() {
    }

    Method readRLEMethod;
    Method readPlainText;

    @Test
    public void testReadFileFromDisk() throws Exception {
        System.out.println("readFileFromDisk");
        String url = "file:///glider.cells";
        Path file = Paths.get(URI.create(url));
        byte[][] expResult = {{0, 64, 0}, {0, 0, 64}, {64, 64, 64}};
        byte[][] result = ReadFile.readFileFromDisk(file);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testReadRLE() throws Exception {
        System.out.println("testReadRLE");
        String[] a = new String[]{"x = 3, y = 3", "bob$2b1o$3o!"};
        Object[] param = {a};

        byte[][] expected = {{0, 64, 0}, {0, 0, 64}, {64, 64, 64}};

        byte[][] output = (byte[][]) readRLEMethod.invoke(null, new Object[]{a});
        for (byte[] line : output) {
            for (byte value : line) {
                System.out.print(value);
            }
            System.out.println("");
        }
        
        assertArrayEquals(expected, output);

        //  fail("Not implemented yet ");
    }

        @Test
    public void testReadRLE2() throws Exception {
        System.out.println("testReadRLE2");
        String[] a = new String[]{"#Casdjkløsadsdf","x = 3, y = 3", "bob$2b1o$3o!"};
        Object[] param = {a};

        byte[][] expected = {{0, 64, 0}, {0, 0, 64}, {64, 64, 64}};

        byte[][] output = (byte[][]) readRLEMethod.invoke(null, new Object[]{a});
        for (byte[] line : output) {
            for (byte value : line) {
                System.out.print(value);
            }
            System.out.println("");
        }
        
        assertArrayEquals(expected, output);

        //  fail("Not implemented yet ");
    }
    
}
