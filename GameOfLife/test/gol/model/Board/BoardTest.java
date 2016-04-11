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
 * @author 
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
        Board instance = new ArrayBoard();
    }

    @After
    public void tearDown() {
    }
    
    Board instance;

    /**
     * Test of nextGen method, of class Board.
     */
    @Test
    public void testNextGen() {
        System.out.println("nextGen");
        byte[][] gameBoard = {           
            {64, 64},
            {64, 0 }};
        Board gol = new ArrayBoard(4, 4);
        gol.insertArray(gameBoard, 1,1);
        gol.nextGen();
        String expResult ="0000011001100000";
        assertEquals(gol.toString(), expResult);
    }

    @Test
    public void testSetCellState() {
        System.out.println("setCellState");
        instance = new ArrayBoard(4,4);
        Board instance2 = new ArrayBoard(5,5);
        Board instance3 = new DynamicBoard();

        instance.setCellState(1, 1, true);
        instance.setCellState(1, 2, true);
        instance.setCellState(2, 1, true);
        String result1 = instance.toString();
        
        instance2.setCellState(1, 1, true);
        instance2.setCellState(2, 1, true);
        instance2.setCellState(2, 2, true);
        instance2.setCellState(3, 2, true);
        instance2.setCellState(3, 3, true);
        String result2 = instance2.toString();
        
        instance3.setCellState(-1, -1, true);
        instance3.setCellState(-1, -2, true);
        instance3.setCellState(-2, -1, true);
        
        String expResult1 = "0000011001000000";
        String expResult2 = "0000001000011000011000000";
        
        
        
        assertEquals(result1, expResult1);
        assertEquals(result2, expResult2);
        assertEquals(instance3.getCellState(-1, -1), true);
        assertEquals(instance3.getCellState(-5, -5), false);
        assertEquals(instance3.getCellState(-1, -3), false);
        
        
    }

    /**
     * Test of getCellState method, of class Board.
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
     * Test of clearBoard method, of class Board.
     */
    @Test
    public void testClearBoard() {
        System.out.println("clearBoard");

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

        Object expResult = null;
        //Object result = instance.getGameBoard();
        //assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getArrayLength method, of class Board.
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
     * Test of getArrayLength method, of class Board.
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
     * Test of countNeigh method, of class Board.
     */
    @Test
    public void testCountNeigh() {
        System.out.println("countNeigh");
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
        instance.checkRules(activeRule);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetBoundingBoxBoard() {
        System.out.println("getBoundingBoxBoard");
        Board instance = new BoardImpl();
        byte[][] expResult = null;
        byte[][] result = instance.getBoundingBoxBoard();
        assertArrayEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetBoundingBox() {
        System.out.println("getBoundingBox");
        Board instance = new BoardImpl();
        int[] expResult = null;
        int[] result = instance.getBoundingBox();
        assertArrayEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testInsertArray() {
        System.out.println("insertArray");
        byte[][] boardToInsert = null;
        int y = 0;
        int x = 0;
        Board instance = new BoardImpl();
        instance.insertArray(boardToInsert, y, x);
        fail("The test case is a prototype.");
    }

    @Test
    public void testSetCellState_3args() {
        System.out.println("setCellState");
        int y = 0;
        int x = 0;
        boolean alive = false;
        Board instance = new BoardImpl();
        instance.setCellState(y, x, alive);
        fail("The test case is a prototype.");
    }

    @Test
    public void testSetCellState_5args() {
        System.out.println("setCellState");
        double y = 0.0;
        double x = 0.0;
        boolean alive = false;
        double offsetX = 0.0;
        double offsetY = 0.0;
        Board instance = new BoardImpl();
        instance.setCellState(y, x, alive, offsetX, offsetY);
        fail("The test case is a prototype.");
    }

    public class BoardImpl extends Board {

        public byte[][] getBoundingBoxBoard() {
            return null;
        }

        public int[] getBoundingBox() {
            return null;
        }

        public void clearBoard() {
        }

        public void countNeigh() {
        }

        public void checkRules(Rule activeRule) {
        }

        public void insertArray(byte[][] boardToInsert, int y, int x) {
        }

        public void setCellState(int y, int x, boolean alive) {
        }

        public void setCellState(double y, double x, boolean alive, double offsetX, double offsetY) {
        }

        public int getArrayLength() {
            return 0;
        }

        public int getArrayLength(int i) {
            return 0;
        }

        public boolean getCellState(int y, int x) {
            return false;
        }
    }

}
