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
        instance.setPattern(new byte[][]{{0, 1, 0}, {0, 1, 0}, {0, 1, 0}});
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
        int time = 1;

        int expResult = 0;
        int result = instance.changeInLiving(time);
        assertEquals(expResult, result);

    }

}
