package gol.model.Board;

import gol.model.Logic.ConwaysRule;
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
        instance = new ArrayBoard(5, 5);
        pattern = new byte[][]
                   {{0,0,0,0,0},
                    {0,64,0,0,0},
                    {0,64,64,0,0},
                    {0,0,0,0,0},
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
        //TODO fiks denne testen? Eller skal den slettes? conwaysRule og customRule er testet.
        System.out.println("checkRules");
        Rule activeRule = new ConwaysRule();
        instance.clearBoard();
        instance.insertArray(pattern,0,0);
        
        byte[][] expectedPattern = new byte[][] //This is pattern next gen with Conways Rule
                    {{0,0,0,0,0},
                    {0,0,0,0,0},
                    {0,64,64,0,0},
                    {0,64,64,0,0},
                    {0,0,0,0,0}};
        
        
        instance.checkRules(activeRule);
        System.out.println(Arrays.deepToString((byte[][])instance.getGameBoard()));
        System.out.println(Arrays.deepToString((byte[][])expectedPattern));
        
        assertArrayEquals(pattern, expectedPattern);
    }

    /**
     * Test of toString method, of class ArrayBoard.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        
        Board toStringInstance = new ArrayBoard(4, 4);
        
        byte[][] board = {
            {0, 0, 0, 0},
            {0, 64, 64, 0},
            {0, 64, 64, 0},
            {0, 0, 0, 0}};
        toStringInstance.insertArray(board, 0, 0);
        String expResult = "0000011001100000";
        String result = toStringInstance.toString();
        assertEquals(expResult, result);
        
    }

}
