package gol.model.Board;

import gol.model.FileIO.PatternFormatException;
import gol.model.FileIO.ReadFile;
import gol.model.ThreadPool;
import gol.other.Configuration;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author s305054, s305089, s305084
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
        Configuration.loadConfig();
        arrayInstance = new ArrayBoard(5, 5);
        dynamicInstance = new DynamicBoard(5, 5);
        patternGlider = new byte[][]{
            {0, 64, 0},
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
     * //TODO: Comment about expand board
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

        dynamicInstance.insertArray(gameBoard, 1, 1);
        dynamicInstance.nextGen();
        expResult = "0000011001100000";
        assertEquals(gol.toString(), expResult);
    }

    @Test
    public void testSetGetCellState() {
        System.out.println("setCellState & getCellState");
        //Array test
        arrayInstance.setCellState(-1, 1, true);
        arrayInstance.setCellState(1, 1, true);
        arrayInstance.setCellState(1, 2, true);
        arrayInstance.setCellState(2, 1, true);
        String result1 = arrayInstance.toString();

        String expResult1 = "0000001100010000000000000";
        assertEquals(result1, expResult1);

        //Get negeative velues arrayBoard
        arrayInstance.setCellState(-2, 1, true);
        assertEquals(false, arrayInstance.getCellState(-2, 1));
        
        //Dynamic test
        dynamicInstance.clearBoard();
        dynamicInstance.setCellState(-1, -1, true);
        assertEquals(true, dynamicInstance.getCellState(50, 50));

        dynamicInstance.clearBoard();
        dynamicInstance.setCellState(-1, -2, true);
        assertEquals(true, dynamicInstance.getCellState(50, 50));

        dynamicInstance.clearBoard();
        dynamicInstance.setCellState(1, 1, true);
        assertEquals(true, dynamicInstance.getCellState(50, 50));

        dynamicInstance.clearBoard();
        dynamicInstance.setCellState(2, 2, true);
        System.out.println(Arrays.toString(dynamicInstance.getBoundingBox()));
        assertEquals(true, dynamicInstance.getCellState(2, 2));

        dynamicInstance.clearBoard();
        dynamicInstance.setCellState(0, 0, true);
        assertEquals(true, dynamicInstance.getCellState(50, 50));

        assertEquals(false, dynamicInstance.getCellState(-5, -5));
        assertEquals(false, dynamicInstance.getCellState(-1, -3));
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

        dynamicInstance.insertArray(patternGlider, 0, 0);
        dynamicInstance.clearBoard();
        assertEquals(expResult, result);

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
        assertArrayEquals(expResult, result);

        dynamicInstance.clearBoard();
        dynamicInstance.insertArray(patternGlider, 1, 1);
        result = dynamicInstance.getBoundingBoxBoard();
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetBoundingBox() {
        System.out.println("getBoundingBox");
        arrayInstance = new ArrayBoard(100, 100);
        arrayInstance.clearBoard();
        arrayInstance.setCellState(7, 5, true);
        arrayInstance.setCellState(6, 4, true);
        arrayInstance.setCellState(5, 11, true);
        arrayInstance.setCellState(3, 8, true);

        int[] expResult = new int[4];
        expResult[0] = 3;
        expResult[1] = 7;
        expResult[2] = 4;
        expResult[3] = 11;

        int[] result = arrayInstance.getBoundingBox();
        assertArrayEquals(expResult, result);
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
        dynamicInstance.clearBoard();
        dynamicInstance.insertArray(patternGlider, 1, 1);
        assertEquals("010001111", dynamicInstance.toString());
        
        dynamicInstance.clearBoard();
        dynamicInstance.insertArray(patternGlider, -2, -2);
        assertEquals("010001111", dynamicInstance.toString());

    }

    //@Test
    public void printPerformanceConcurrent() throws IOException, PatternFormatException {
        System.out.println("printPerformance:");
        File url = new File("test/patternTestFiles/turingmachine.rle");
        Path file = Paths.get(url.toURI());
        byte[][] byteBoard = ReadFile.readFileFromDisk(file);
        System.out.println("Number of threads: " + ThreadPool.THREAD_NR);
        System.out.println("Brett: " + url.getName());
        System.out.println("Testing dynamic concurrent");
        dynamicInstance.clearBoard();
        dynamicInstance.insertArray(byteBoard, 5, 5);
        dynamicInstance.nextGenConcPrintPerformance();

        System.out.println("Testing dynamicBoard");
        dynamicInstance.clearBoard();
        dynamicInstance.insertArray(byteBoard, 5, 5);
        dynamicInstance.nextGenPrintPerformance();

        System.out.println("Testing Arrayboard");
        Board arrayBoard = new ArrayBoard(2000, 2000);
        arrayBoard.insertArray(byteBoard, 100, 100);
        arrayBoard.nextGenPrintPerformance();

    }

    public void testSetCellState_5args() {
        //This is a method witch calculates a mouse click to a point on the board
        //Testing this is much easier with a visual test.
    }
}
