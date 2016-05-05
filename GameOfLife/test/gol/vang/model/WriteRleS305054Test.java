/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.vang.model;

import gol.vang.model.WriteFileS54;
import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
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
public class WriteRleS305054Test {
    
    public WriteRleS305054Test() {
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
     * Test of writeRLE method, of class WriteFileS54.
     */
    @Test
    public void testWriteRLE() throws Exception {
        System.out.println("writeRLE");
        Board boardToParse = new ArrayBoard(7,7);
        TextField title = new TextField();
        TextField author = new TextField();
        TextField description = new TextField();
        
        title.setText("Glider");
        author.setText("");
        description.setText("");
        byte[][] pattern = {
            {0,64,0},
            {0,0,64},
            {64,64,64}
        };
        boardToParse.insertArray(pattern, 1, 1);
        
        WriteFileS54 instance = new WriteFileS54();
        //instance.writeRLE(boardToParse, title, author, description);
        // TODO review the generated test code and remove the default call to fail.
        
    }
    
}
