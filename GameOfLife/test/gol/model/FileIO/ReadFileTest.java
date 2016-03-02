package gol.model.FileIO;

import java.io.BufferedReader;
import java.nio.file.Path;
import javafx.stage.FileChooser;
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
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of readGameBoard method, of class ReadFile.
     */
    @Test
    public void testReadGameBoard() throws Exception {
        System.out.println("readGameBoard");
        BufferedReader r = null;
        ReadFile.readGameBoard(r);

        fail("The test case is a prototype.");
    }

    /**
     * Test of readGameBoardFromDisk method, of class ReadFile.
     */
    @Test
    public void testReadGameBoardFromDisk() throws Exception {
        System.out.println("readGameBoardFromDisk");
        Path file = new FileChooser().showOpenDialog(null).toPath();       
        ReadFile.readGameBoardFromDisk(file);
        
        String expected = "010001111";
        
        
        fail("The test case is a prototype.");
    }
    
}
