/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gol.model.Board;

import gol.model.Logic.Rule;
import java.util.Arrays;
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
        arrayInstance = new ArrayBoard(5, 5);
        dynamicInstance = new DynamicBoard(5, 5);
        patternGlider = new byte[][]{
            {0, 64},
            {0, 0, 64},
            {64, 64, 64}};
    }

    @After
    public void tearDown() {
    }

    Board arrayInstance;
    Board dynamicInstance;
    byte[][] patternGlider;

    /**
     * Test of nextGen method, of class Board.
     */
    @Test
    public void testNextGen() {
        System.out.println("nextGen");
        byte[][] gameBoard = {
            {64, 64},
            {64, 0}};
        Board gol = new ArrayBoard(4, 4);
        gol.insertArray(gameBoard, 1, 1);
        gol.nextGen();
        String expResult = "0000011001100000";
        assertEquals(gol.toString(), expResult);
    }

    @Test
    public void testSetCellState() {
        System.out.println("setCellState");
        Board instance = new ArrayBoard(4, 4);
        Board instance2 = new ArrayBoard(5, 5);
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
        boolean result = arrayInstance.getCellState(x, y);
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
        arrayInstance.insertArray(patternGlider, 1, 1);
        arrayInstance.clearBoard();
        String result = arrayInstance.toString();

        String expResult = "0000000000000000000000000";
        assertEquals(expResult, result);

    }

    /**
     * Test of getArrayLength method, of class Board.
     */
    @Test
    public void testGetArrayLength_0args() {
        System.out.println("getArrayLength");

        int expResult = 0;
        int result = arrayInstance.getArrayLength();
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
        int result = arrayInstance.getArrayLength(i);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    @Test
    public void testGetBoundingBoxBoard() {
        System.out.println("getBoundingBoxBoard");
        byte[][] expResult = new byte[][]{{0, 64, 0}, {0, 0, 64}, {64, 64, 64}};
        byte[][] result;

        arrayInstance.insertArray(patternGlider, 1, 1);
        result = arrayInstance.getBoundingBoxBoard();
        assertArrayEquals(expResult, result);

        dynamicInstance.insertArray(patternGlider, 1, 1);
        result = dynamicInstance.getBoundingBoxBoard();
        assertArrayEquals(expResult, result);

        dynamicInstance.clearBoard();
        dynamicInstance.insertArray(patternGlider, -10, -10);
        result = dynamicInstance.getBoundingBoxBoard();
        System.out.println(Arrays.deepToString(result));
        assertArrayEquals(expResult, result);

        dynamicInstance.clearBoard();
        dynamicInstance.insertArray(patternGlider, 1, 1);
        result = dynamicInstance.getBoundingBoxBoard();
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetBoundingBox() {
        System.out.println("getBoundingBox");

        int[] expResult = null;
        int[] result = arrayInstance.getBoundingBox();
        assertArrayEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    /**
     * DynamicBoards insert array is tested through setCellState.
     */
    @Test
    public void testInsertArray() {
        System.out.println("insertArray");
        arrayInstance.insertArray(patternGlider, 1, 1);
        assertEquals("0000000100000100111000000", arrayInstance.toString());

        arrayInstance.clearBoard();
        arrayInstance.insertArray(patternGlider, 2, 2);
        assertEquals("0000000000000100000100111", arrayInstance.toString());

        arrayInstance.clearBoard();
        arrayInstance.insertArray(patternGlider, 3, 2);
        assertEquals("0000000000000000001000001", arrayInstance.toString());

        //see test for setCellState, for DynamicBoard
        dynamicInstance.insertArray(patternGlider, 1, 1);
        assertEquals("00100010111000000", dynamicInstance.toString());

        dynamicInstance.insertArray(patternGlider, -2, -2);
       // assertEquals("00100010111000000000000000000000", dynamicInstance.toString());

    }

    @Test
    public void testSetCellState_3args() {
        System.out.println("setCellState");
        int y = 0;
        int x = 0;
        boolean alive = false;

        arrayInstance.setCellState(y, x, alive);
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

        arrayInstance.setCellState(y, x, alive, offsetX, offsetY);
        fail("The test case is a prototype.");
    }
}
