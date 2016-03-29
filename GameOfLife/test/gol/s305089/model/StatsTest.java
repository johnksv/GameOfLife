/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.s305089.model;

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
        instance.setPattern(new byte[][]{{0, 64, 0}, {0, 64, 0}, {0, 64, 0}});
    }
    Stats instance;

    @Rule
    public ExpectedException expectedExeption = ExpectedException.none();

    @Test
    public void testCountLiving() {
        System.out.println("countLiving");

        int[] result = instance.countLiving(10);
        for (int i = 0; i < result.length; i++) {
            assertEquals(3, result[i]);
        }

        result = instance.countLiving(0);
        assertEquals(0, result[0]);

        expectedExeption.expect(NegativeArraySizeException.class);
        instance.countLiving(-1);
        instance.countLiving(Integer.MIN_VALUE);

    }

    @Test
    public void testChangeInLiving() {
        System.out.println("changeInLiving");

        instance.setPattern(new byte[][]{{0, 64, 0}, {0, 64, 0}, {0, 64, 0}});
        int[] countChangeOfLiving = instance.changeInLiving(10);
        for (int i = 0; i < countChangeOfLiving.length; i++) {
            assertEquals(0, countChangeOfLiving[i]);
        }
    }

    @Test
    public void testSimilarityMeasure() {
        System.out.println("similarityMeasure");
        int time = 10;
        int expResult = 0;
        instance.setPattern(new byte[][]{{0, 64, 0}, {0, 64, 0}, {0, 64, 0}});
        int[] result = instance.similarityMeasure(time);
        // assertEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testGeometricFactor() {
        System.out.println("geometricFactor");
        int expResult;
        int result;
        instance.setPattern(new byte[][]{{0, 64, 0}, {0, 64, 0}, {0, 64, 0}});
        for (int i = 0; i < 10; i++) {
            expResult = 3;
            result = instance.geometricFactor(i);
            assertEquals(expResult, result);
        }

        instance.setPattern(new byte[][]{{0, 64, 0}, {0, 0, 64}, {64, 64, 64}});
        for (int i = 1; i < 10; i++) {
            if (i % 2 == 0) {
                expResult = 13;
            } else {
                expResult = 10;
            }
            result = instance.geometricFactor(i);
            assertEquals(expResult, result);
        }

    }

}
