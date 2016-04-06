package gol.model.Logic;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author s305054, s305084, s305089
 */
public class ConwaysRuleTest {

    public ConwaysRuleTest() {
    }


    /**
     * Test of setLife method, of class ConwaysRule.
     */
    @Test
    public void testSetLife() {
        System.out.println("setLife");
        ConwaysRule instance = new ConwaysRule();
        byte cellToCheck;
        byte expResult;
        byte result;
        for (byte i = 0; i < 127; i++) {
            cellToCheck = i;
            if (i == 66 || i == 67 || i == 3) {
                expResult = 64;
            } else { 
                expResult = 0;
            }
            result = instance.setLife(cellToCheck);
            assertEquals(expResult, result);
        }
        for (byte i = 0; i > -128; i--) {
            cellToCheck = i;
            expResult = 0;         
            
            result = instance.setLife(cellToCheck);
            assertEquals(expResult, result);
        }
    }
}
