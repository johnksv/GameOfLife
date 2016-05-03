/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.s305089.model;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 *
 * @author John Kasper
 */
public class StatsTest {

    public StatsTest() {
    }

    @Before
    public void setUp() {
        instance = new Stats();
        patternBoard = new ArrayBoard(20, 20);
        patternBoard.insertArray(new byte[][]{{0, 64, 0}, {0, 64, 0}, {0, 64, 0}});
        instance.setBoard(patternBoard);
    }
    Stats instance;
    Board patternBoard;

    @Rule
    public ExpectedException expectedExeption = ExpectedException.none();

    @Test
    public void testGetStatistics() {
        System.out.println("getStatistics");

        instance.setBoard(patternBoard);
        int[][] result = instance.getStatistics(20, true, true);
        System.out.println(Arrays.deepToString(result));
        for (int iteration = 0; iteration < result.length - 1; iteration++) {

            assertEquals(5, result[iteration][0]);
            assertEquals(0, result[iteration][1]);

            //For each generation, there should be a 100% match with another generation
            //unless for the two last. Only checking for generation in future.
            if (iteration < result.length - 3) {
                assertEquals(100, result[iteration][2], 0.0005);
            } else if (iteration == result.length - 2) {
                assertEquals(0, result[iteration][2], 0.0005);
            }
        }
    }

    @Test
    public void testCountLiving() {
        System.out.println("countLiving");

        int[] result = instance.getCountLiving(10);
        for (int i = 0; i < result.length; i++) {
            assertEquals(3, result[i]);
        }

        result = instance.getCountLiving(0);
        assertEquals(0, result[0]);

        expectedExeption.expect(NegativeArraySizeException.class);
        instance.getCountLiving(-1);
        instance.getCountLiving(Integer.MIN_VALUE);

    }

    @Test
    public void testChangeInLiving() {
        System.out.println("changeInLiving");

        instance.setBoard(patternBoard);
        int[] countChangeOfLiving = instance.getChangeInLiving(10);
        for (int i = 0; i < countChangeOfLiving.length; i++) {
            assertEquals(0, countChangeOfLiving[i]);
        }

        //Need to make an new instance, or else calcualtions is done on old pattern.
        instance.setBoard(patternBoard);
        int[] resArray = instance.getChangeInLiving(10);
        System.out.println(Arrays.toString(resArray));
        assertEquals(0, resArray[0]);
        assertEquals(2, resArray[1]);
        assertEquals(0, resArray[2]);

    }

    //@Test
    public void testSimilarityMeasure() {
        System.out.println("similarityMeasure");
        int time = 10;
        int expResult = 0;
        instance.setBoard(patternBoard);
        int[][] result = instance.getSimilarityMeasure(time);
        // assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }
}
