/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.controller;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author John Kasper
 */
public class usefullMethodsTest {

    public usefullMethodsTest() {
    }

    @Before
    public void setUp() {
    }
    
    @Test
    public void testRotateArray90Deg() {
        System.out.println("rotateArray90Deg");
        byte[][] arrayToRotate = {{64, 64, 0},
                                    {0, 0, 0},
                                    {0, 0, 0}};
        byte[][] exp = {{0, 0, 64},
                        {0, 0, 64},
                        {0, 0, 0}};
        byte[][] result = usefullMethods.rotateArray90Deg(arrayToRotate);
        assertArrayEquals(exp, result);
    }
    
    @Test
    public void testRotateArray90Deg2() {
        System.out.println("rotateArray90Deg2");        
        byte[][] arrayToRotate = {{64, 64, 0, 64},
                            {0, 64, 0},
                            {0, 0, 0}};
        byte[][] exp = {{0, 0, 64},
                        {0, 64, 64},
                        {0, 0, 0},
                        {0, 0, 64}};
        byte[][] result = usefullMethods.rotateArray90Deg(arrayToRotate);
        assertArrayEquals(exp, result);
    }
    
    @Test
    public void testRotateArray90Deg3() {
        System.out.println("rotateArray90Deg3");        
        byte[][] arrayToRotate = {{64, 64, 0, 64},
                            {64}};
        byte[][] exp = {{64, 64},
                        {0, 64,},
                        {0, 0},
                        {0, 64}};
        byte[][] result = usefullMethods.rotateArray90Deg(arrayToRotate);
        assertArrayEquals(exp, result);
    }
    
    @Test
    public void testRotateArray90Deg4() {
        System.out.println("rotateArray90Deg4");        
        byte[][] arrayToRotate = {{64, 64, 0, 64}};
        byte[][] exp = {{64},
                        {64,},
                        {0},
                        {64}};
        byte[][] result = usefullMethods.rotateArray90Deg(arrayToRotate);
        assertArrayEquals(exp, result);
    }

}
