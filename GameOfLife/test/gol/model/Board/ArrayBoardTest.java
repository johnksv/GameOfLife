package gol.model.Board;

import gol.model.Logic.Rule;
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
    }
    Board instance;

    /**
     * Test of clearBoard method, of class ArrayBoard.
     */
    @Test
    public void testClearBoard() {
        System.out.println("clearBoard");
        instance.clearBoard();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getArrayLength method, of class ArrayBoard.
     */
    @Test
    public void testGetArrayLength_0args() {
        System.out.println("getArrayLength");
        int expResult = 0;

        int result = instance.getArrayLength();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getArrayLength method, of class ArrayBoard.
     */
    @Test
    public void testGetArrayLength_int() {
        System.out.println("getArrayLength");
        int i = 0;
        int expResult = 0;
        int result = instance.getArrayLength(i);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getGameBoard method, of class ArrayBoard.
     */
    @Test
    public void testGetGameBoard() {
        System.out.println("getGameBoard");
        byte[][] expResult = null;
        byte[][] result = (byte[][]) instance.getGameBoard();
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setCellState method, of class ArrayBoard.
     */
    @Test
    public void testSetCellState() {
        System.out.println("setCellState");
        int x = 0;
        int y = 0;
        boolean alive = false;
        instance.setCellState(x, y, alive);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCellState method, of class ArrayBoard.
     */
    @Test
    public void testGetCellState() {
        System.out.println("getCellState");
        int x = 0;
        int y = 0;
       
        boolean expResult = false;
        boolean result = instance.getCellState(x, y);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
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
    }

}
