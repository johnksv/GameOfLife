/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gol.model.Board;

import gol.model.Logic.Rule;
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
public class ArrayBoardTest {

    public ArrayBoardTest() {
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
     * Test of clearBoard method, of class ArrayBoard.
     */
    @Test
    public void testClearBoard() {
        System.out.println("clearBoard");
        ArrayBoard instance = null;
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
        ArrayBoard instance = null;
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
        ArrayBoard instance = null;
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
        ArrayBoard instance = null;
        byte[][] expResult = null;
        byte[][] result = instance.getGameBoard();
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
        double x = 0.0;
        double y = 0.0;
        boolean alive = false;
        ArrayBoard instance = null;
        instance.setCellState(x, y, alive);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setBoard method, of class ArrayBoard. Only for testing, not used
     * at any point during runtime.
     */
    @Test
    public void testSetBoard() {
        System.out.println("setBoard");
        ArrayBoard instance = new ArrayBoard(2, 2);
        int[][] gameBoard = {
            {0, 0, 0, 0},
            {0, 64, 64, 0},
            {0, 64, 64, 0},
            {0, 0, 0, 0}};
        instance.setGameBoard(gameBoard);
        assertNotSame(instance.getGameBoard(), gameBoard);
    }

    @Test
    public void testSetBoard2() {
        System.out.println("setBoard test 2");
        ArrayBoard instance = new ArrayBoard(2, 2);
        int[][] gameBoard = null;
        instance.setGameBoard(gameBoard);
        assertNotNull(instance.getGameBoard());
    }

    /**
     * Test of getCellState method, of class ArrayBoard.
     */
    @Test
    public void testGetCellState() {
        System.out.println("getCellState");
        int x = 0;
        int y = 0;
        ArrayBoard instance = null;
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
        ArrayBoard instance = null;
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
        ArrayBoard instance = null;
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
        ArrayBoard instance = new ArrayBoard(2, 2);
        byte[][] board = {
            {0, 0, 0, 0},
            {0, 64, 64, 0},
            {0, 64, 64, 0},
            {0, 0, 0, 0}};
        instance.setGameBoard(board);
        String expResult = "0000011001100000";
        String result = instance.toString();
        assertEquals(expResult, result);
    }

}
