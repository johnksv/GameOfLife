/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.model.Logic;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author John Kasper
 */
public class CustomRuleTest {

    Rule testRule;
    Rule testRule1;

    public CustomRuleTest() {
    }

    @Before
    public void setUp() {
        testRule = new CustomRule(new byte[]{0}, new byte[]{6});
        testRule1 = new CustomRule(new byte[]{4, 7}, new byte[]{6});
    }

    @Test
    public void testSetLife() {
        System.out.println("setLife");
        byte cellToCheck = 64;
        byte expResult = 64;
        byte result = testRule.setLife(cellToCheck);
        assertEquals(expResult, result);
    }

    @Test
    public void testSetLife2() {
        System.out.println("setLife");
        byte cellToCheck = 6;
        byte expResult = 64;
        byte result = testRule.setLife(cellToCheck);
        assertEquals(expResult, result);
    }

    @Test
    public void testSetLife3() {
        System.out.println("setLife");
        byte cellToCheck = 68;
        byte expResult = 64;
        byte result = testRule1.setLife(cellToCheck);
        assertEquals(expResult, result);
    }

    @Test
    public void testSetLife4() {
        System.out.println("setLife");
        byte cellToCheck = 71;
        byte expResult = 64;
        byte result = testRule1.setLife(cellToCheck);
        assertEquals(expResult, result);
    }

    @Test
    public void testSetLife5() {
        System.out.println("setLife");
        byte cellToCheck = 69;
        byte expResult = 64;
        byte result = testRule1.setLife(cellToCheck);
        assertNotSame(expResult, result);
    }

}
