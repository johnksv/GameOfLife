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
public class BoardTest {

    public BoardTest() {
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
     * Test of nextGen method, of class Board.
     */
    @Test
    public void testNextGen() {
        System.out.println("nextGen");
        byte[][] gameBoard = {
            {0, 0, 0, 0},
            {0, 64, 64, 0},
            {0, 64, 0, 0},
            {0, 0, 0, 0}};
        Board gol = new ArrayBoard(2,2);
        gol.setGameBoard(gameBoard);
        gol.nextGen();
        assertEquals(gol.toString(), "0000011001100000");
    }

    /**
     * Test of setCellSize method, of class Board.
     */
    @Test
    public void testSetCellSize() {
        System.out.println("setCellSize");
        double cellSize = 0.0;
        Board instance = null;
        instance.setCellSize(cellSize);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getGridSpacing method, of class Board.
     */
    @Test
    public void testGetGridSpacing() {
        System.out.println("getGridSpacing");
        Board instance = null;
        double expResult = 0.0;
        double result = instance.getGridSpacing();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setGridSpacing method, of class Board.
     */
    @Test
    public void testSetGridSpacing() {
        System.out.println("setGridSpacing");
        double gridSpacing = 0.0;
        Board instance = null;
        instance.setGridSpacing(gridSpacing);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCellSize method, of class Board.
     */
    @Test
    public void testGetCellSize() {
        System.out.println("getCellSize");
        Board instance = null;
        double expResult = 0.0;
        double result = instance.getCellSize();
        assertEquals(expResult, result, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setGameRule method, of class Board.
     */
    @Test
    public void testSetGameRule() {
        System.out.println("setGameRule");
        Rule activeRule = null;
        Board instance = null;
        instance.setGameRule(activeRule);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setCellState method, of class Board.
     */
    @Test
    public void testSetCellState() {
        System.out.println("setCellState");
        double x = 0.0;
        double y = 0.0;
        boolean alive = false;
        Board instance = null;
        instance.setCellState(x, y, alive);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getCellState method, of class Board.
     */
    @Test
    public void testGetCellState() {
        System.out.println("getCellState");
        int x = 0;
        int y = 0;
        Board instance = null;
        boolean expResult = false;
        boolean result = instance.getCellState(x, y);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of clearBoard method, of class Board.
     */
    @Test
    public void testClearBoard() {
        System.out.println("clearBoard");
        Board instance = null;
        instance.clearBoard();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getGameBoard method, of class Board.
     */
    @Test
    public void testGetGameBoard() {
        System.out.println("getGameBoard");
        Board instance = null;
        Object expResult = null;
        Object result = instance.getGameBoard();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getArrayLength method, of class Board.
     */
    @Test
    public void testGetArrayLength_0args() {
        System.out.println("getArrayLength");
        Board instance = null;
        int expResult = 0;
        int result = instance.getArrayLength();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getArrayLength method, of class Board.
     */
    @Test
    public void testGetArrayLength_int() {
        System.out.println("getArrayLength");
        int i = 0;
        Board instance = null;
        int expResult = 0;
        int result = instance.getArrayLength(i);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of countNeigh method, of class Board.
     */
    @Test
    public void testCountNeigh() {
        System.out.println("countNeigh");
        Board instance = null;
        instance.countNeigh();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of checkRules method, of class Board.
     */
    @Test
    public void testCheckRules() {
        System.out.println("checkRules");
        Rule activeRule = null;
        Board instance = null;
        instance.checkRules(activeRule);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setGameBoard method, of class Board.
     */
    @Test
    public void testSetGameBoard() {
        System.out.println("setGameBoard");
        Object gameBoard = null;
        Board instance = null;
        instance.setGameBoard(gameBoard);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
