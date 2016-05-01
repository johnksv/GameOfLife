package gol.controller;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author s305054, s305084, s305089
 */
public class UsefullMethodsTest {

    public UsefullMethodsTest() {
    }

    @Before
    public void setUp() {
    }
    @Test
    public void testTranspose() {
        System.out.println("transposeMatrix");
        byte[][] arrayToRotate = {  {64, 64, 0},
                                    {0,  0,  0},
                                    {0,  64, 0}};
        byte[][] exp = {{64, 0, 0},
                        {64, 0, 64},
                        {0,  0, 0}};
        byte[][] result = UsefullMethods.transposeMatrix(arrayToRotate);
        assertArrayEquals(exp, result);
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
        byte[][] result = UsefullMethods.rotateArray90Deg(arrayToRotate);
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
        byte[][] result = UsefullMethods.rotateArray90Deg(arrayToRotate);
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
        byte[][] result = UsefullMethods.rotateArray90Deg(arrayToRotate);
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
        byte[][] result = UsefullMethods.rotateArray90Deg(arrayToRotate);
        assertArrayEquals(exp, result);
    }

}
