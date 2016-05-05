package gol.svergja.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author s305089 - John Kasper Svergja
 */
public class WriteFileTest {

    public WriteFileTest() {
    }

    @Before
    public void setUp() {
    }

    /**
     * Difficult to check if the file actually is correct. This is done
     * manually.
     *
     * @throws IOException
     */
    @Test
    public void testWriteToRLE() throws IOException {
        System.out.println("writeToRLE");
        byte[][] boardToWrite = {{}};
        Path saveLocation = File.createTempFile("patternTestGol", ".rle").toPath();
        boolean expResult = false;
        boolean result = WriteFile.writeToRLE(boardToWrite, saveLocation);
        assertEquals(expResult, result);
        saveLocation.toFile().delete();

        boardToWrite = new byte[][]{{0, 0, 0, 64, 64, 64}};
        expResult = true;
        result = WriteFile.writeToRLE(boardToWrite, saveLocation);
        assertEquals(expResult, result);
        saveLocation = File.createTempFile("patternTestGol", ".rle").toPath();
        saveLocation.toFile().delete();
    }

    /* Testes used during development, before methods was sat to private.
    Change method compressedRow to public for these tests to work
    @Test
    public void testCompressedRow() {
        System.out.println("compressedRow");
        StringBuilder row = new StringBuilder("oobbo$");
        String expResult = "2o2bo$";
        StringBuilder result = WriteFile.compressedRow(row);
        assertEquals(expResult, result.toString());
    }

    @Test
    public void testCompressedRow2() {
        System.out.println("compressedRow2");
        StringBuilder row = new StringBuilder("ooo$");
        String expResult = "3o$";
        StringBuilder result = WriteFile.compressedRow(row);
        assertEquals(expResult, result.toString());
    }

    @Test
    public void testCompressedRow3() {
        System.out.println("compressedRow3");
        StringBuilder row = new StringBuilder("oobb$");
        String expResult = "2o2b$";
        StringBuilder result = WriteFile.compressedRow(row);
        assertEquals(expResult, result.toString());
    }

    @Test
    public void testCompressedRow4() {
        System.out.println("compressedRow4");
        StringBuilder row = new StringBuilder("obbboob$");
        String expResult = "o3b2ob$";
        StringBuilder result = WriteFile.compressedRow(row);
        assertEquals(expResult, result.toString());
    }

    @Test
    public void testCompressedRow5() {
        System.out.println("compressedRow5");
        StringBuilder row = new StringBuilder("bbb$");
        String expResult = "$";
        StringBuilder result = WriteFile.compressedRow(row);
        assertEquals(expResult, result.toString());
    }

    @Test
    public void testCompressedRow6() {
        System.out.println("compressedRow6");
        StringBuilder row = new StringBuilder("o$");
        String expResult = "o$";
        StringBuilder result = WriteFile.compressedRow(row);
        assertEquals(expResult, result.toString());
    }
    @Test
    public void testCompressedRow7() {
        System.out.println("compressedRow7");
        StringBuilder row = new StringBuilder("b$");
        String expResult = "b$";
        StringBuilder result = WriteFile.compressedRow(row);
        assertEquals(expResult, result.toString());
    }
    
    @Test
    public void testCompressedRow8() {
        System.out.println("compressedRow7");
        StringBuilder row = new StringBuilder("bob!");
        String expResult = "bob!";
        StringBuilder result = WriteFile.compressedRow(row);
        assertEquals(expResult, result.toString());
    }
    
     */
}
