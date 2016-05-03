package gol.s305084;

import gol.model.Board.Board;
import gol.model.Board.DynamicBoard;
import gol.other.Configuration;
import java.net.URL;
import java.util.ResourceBundle;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author s305084
 */
public class StatisticsControllerTest {
    
    public StatisticsControllerTest() {
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
    
    @Test
    public void testCountLivingCells() {
        System.out.println("countLivingCells");
        byte[][] gameBoard = {
            {64, 64, 64},
            {64, 0, 0},
            {0, 64, 0}};
        Board pattern = new DynamicBoard();
        pattern.insertArray(gameBoard, 0, 0);
        int expResult = 5;
        int result = StatisticsController.countLivingCells(pattern);
        assertEquals(expResult, result);
    }
    @Test
    public void testCalcChangeCells(){
        System.out.println("calcChangeCells");
       
        int expResult = -4;
        int result = StatisticsController.calcChangeCells(54,50);
        assertEquals(expResult, result);
        
        expResult = 7;
        result = StatisticsController.calcChangeCells(3,10);
        assertEquals(expResult, result);
    }
      
}
