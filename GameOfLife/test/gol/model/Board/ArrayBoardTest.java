package gol.model.Board;

import gol.model.Logic.Rule;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author s305054, s305084, s305089
 */
public class ArrayBoardTest {

    public ArrayBoardTest() {
    }

    @Before
    public void setUp() {
        instance = new ArrayBoard();
        pattern = new byte[][]
                   {{0,0,0,0,0},
                    {0,64,64,64,0},
                    {0,64,64,64,0},
                    {0,64,64,64,0},
                    {0,0,0,0,0}};
    }
    Board instance;
    byte[][] pattern;
    /**
     * Test of clearBoard method, of class ArrayBoard.
     * 
     */
    @Test
    public void testClearBoard() {
        System.out.println("clearBoard");
        
        instance.insertArray(pattern,1,1);
        instance.clearBoard();
        String result = instance.toString();
        System.out.println(Arrays.deepToString((byte[][])instance.getGameBoard()));
        
        String expResult = "0000000000000000000000000";
        assertEquals(expResult, result);
        
    }
    /**
     * Test of countNeigh method, of class ArrayBoard.
     */
    @Test
    public void testCountNeigh() {
        System.out.println("countNeigh");
       
        instance.countNeigh();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of checkRules method, of class ArrayBoard.
     */
    @Test
    public void testCheckRules() {
        System.out.println("checkRules");
        Rule activeRule = null;
       
        instance.checkRules(activeRule);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class ArrayBoard.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        
        byte[][] board = {
            {0, 0, 0, 0},
            {0, 64, 64, 0},
            {0, 64, 64, 0},
            {0, 0, 0, 0}};
        instance.insertArray(board, 1, 1);
        String expResult = "0000011001100000";
        String result = instance.toString();
        assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

}
