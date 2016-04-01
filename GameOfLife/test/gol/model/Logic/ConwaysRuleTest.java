/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.model.Logic;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author trygvevang
 */
public class ConwaysRuleTest {
    
    public ConwaysRuleTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of setLife method, of class ConwaysRule.
     */
    @Test
    public void testSetLife() {
        System.out.println("setLife");
        byte cellToCheck = 0;
        ConwaysRule instance = new ConwaysRule();
        byte expResult = 0;
        byte result = instance.setLife(cellToCheck);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        
    }
    
}
