/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.s305089;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author John Kasper
 */
public class UsefullMethodsTest {

    public UsefullMethodsTest() {
    }

    @Before
    public void setUp() {
    }

    @Test
    public void testGetBiggestDimension() {
        System.out.println("getBiggestDimension");
        byte[][] patternToCalculate = new byte[][]{{64, 64, 64}};
        int iterations = 10;
        int[] expResult = new int[]{3,3};
        int[] result = UsefullMethods.getBiggestDimension(patternToCalculate, iterations);
        assertArrayEquals(expResult, result);
        
    }
}
