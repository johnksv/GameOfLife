/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.s305089.model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author John Kasper
 */
public class StatsTest {
    
    public StatsTest() {
    }
    
    @Before
    public void setUp() {
    }

    @Test
    public void testGetStatistics() {
        System.out.println("getStatistics");
        int iterations = 0;
        Stats instance = new Stats();
        int[][] expResult = null;
        int[][] result = instance.getStatistics(iterations);
        assertArrayEquals(expResult, result);
        fail("The test case is a prototype.");
    }

    @Test
    public void testSetPattern() {
        System.out.println("setPattern");
        byte[][] Pattern = null;
        Stats instance = new Stats();
        instance.setPattern(Pattern);
        fail("The test case is a prototype.");
    }

    @Test
    public void testCountLiving() {
        System.out.println("countLiving");
        int time = 1;
        Stats instance = new Stats();
        instance.setPattern(new byte[][]{{0,1,0},{0,0,1},{1,1,1}});
        int expResult = 5;
        int result = instance.countLiving(time);
        
        assertEquals(expResult, result);
        
    }
    
}
