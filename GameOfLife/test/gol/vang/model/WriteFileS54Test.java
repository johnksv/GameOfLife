/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.vang.model;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
import gol.model.Board.DynamicBoard;
import gol.model.FileIO.ReadFile;
import gol.other.Configuration;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.scene.control.TextField;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author trygvevang
 */
public class WriteFileS54Test {
    
    public WriteFileS54Test() {
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

    /**
     * Test of writeRLE method, of class WriteFileS54. This method assumes that
     * the read file methods are working, and if they are, the correct pattern
     * that will be parsed from the "test.rle"-file is equals to the originale 
     * board's boundingbox.
     */
    @Test
    public void testWriteRLE() throws Exception {
        System.out.println("writeRLE");
        
        Configuration.loadConfig();
        Board boardToParse = new ArrayBoard(5, 5);
        byte[][] pattern = new byte[][] {
            {0, 64, 0},
            {0, 0, 64},
            {64, 64, 64}};
        
        boardToParse.insertArray(pattern);
        
        Path sLocation = Paths.get("test.rle");
        
        WriteFileS54 instance = new WriteFileS54();
        instance.writeRLE(boardToParse, "", "", "", sLocation);
        
        assertArrayEquals(boardToParse.getBoundingBoxBoard(), ReadFile.readFileFromDisk(sLocation));
        
        sLocation.toFile().delete();
        
    }
    
}
