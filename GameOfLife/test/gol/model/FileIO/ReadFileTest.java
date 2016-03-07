/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.model.FileIO;

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
 * @author Stian
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
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testReadFileFromDisk() throws Exception {
        System.out.println("readFileFromDisk");
        String url = "file:///glider.cells";
        Path file= Paths.get(URI.create(url));
        byte[][] expResult = {{0,64,0},{0,0,64},{64,64,64}};
        byte[][] result = ReadFile.readFileFromDisk(file);
        assertArrayEquals(expResult, result);
    }
    
        @Test
    public void testReadFileFromDisk2() throws Exception {
        //Forventer exception
        System.out.println("readFileFromDisk");
        String url = "file:///Users/Stian/Documents/glider.cell";
        Path file= Paths.get(URI.create(url));
        byte[][] expResult = {{0,64,0},{0,0,64},{64,64,64}};
        byte[][] result = ReadFile.readFileFromDisk(file);
    }
        @Test
    public void testReadFileFromDisk3() throws Exception {
        System.out.println("readFileFromDisk");
        String url = "file:///Users/Stian/Documents/glider2.cells";
        Path file= Paths.get(URI.create(url));
        byte[][] expResult = {{0,64,0},{0,0,64},{64,64,64}};
        byte[][] result = ReadFile.readFileFromDisk(file);
        
    }
    
}
