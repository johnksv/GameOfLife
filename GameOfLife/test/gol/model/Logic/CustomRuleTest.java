package gol.model.Logic;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.rules.ExpectedException;

/**
 *
 * @author s305054, s305084, s305089
 */
public class CustomRuleTest {

    CustomRule rule;
    byte[] toLive;
    byte[] toSpawn;

    int length = 10;
    byte[] cellToCheckAlive = new byte[length];
    byte[] cellToCheckDead = new byte[length];
    
    public CustomRuleTest() {
    }

    @Before
    public void setUp() {
        /*
         Sets the neighbours count of bouth dead and alive cells from 0 to 9 
         neighbours.
         */
        for (byte i = 0; i < length; i++) {
            cellToCheckAlive[i] = (byte) (64 + i);
            cellToCheckDead[i] = i;
        }
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSetLife() {

        toLive = new byte[]{1, 5};
        toSpawn = new byte[]{2, 3, 8};

        try {
            rule = new CustomRule(toLive, toSpawn);
        } catch (unsupportedRuleException ex) {
            Logger.getLogger(CustomRuleTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        CustomRule instance = rule;
        //First line is dead cells from 0-9 neighbours 
        // and the second line is alive cells with 0 to 9
        byte[] expResult = new byte[]{0, 0, 64, 64, 0, 0, 0, 0, 64, 0,
            0, 64, 0, 0, 0, 64, 0, 0, 0, 0};

        byte[] result = new byte[length * 2];
        for (int i = 0; i < length; i++) {
            result[i] = instance.setLife(cellToCheckDead[i]);
            result[i + length] = instance.setLife(cellToCheckAlive[i]);
        }

        assertArrayEquals(expResult, result);
    }

    @Test
    public void testSetLifeSpecialRule() {

        toLive = new byte[]{-1};
        toSpawn = new byte[]{0};

        try {
            rule = new CustomRule(toLive, toSpawn);
        } catch (unsupportedRuleException ex) {
            Logger.getLogger(CustomRuleTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        CustomRule instance = rule;

        //First line is dead cells from 0-9 neighbours 
        // and the second line is alive cells with 0 to 9
        byte[] expResult = new byte[]{64, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        byte[] result = new byte[length * 2];
        for (int i = 0; i < length; i++) {
            result[i] = instance.setLife(cellToCheckDead[i]);
            result[i + length] = instance.setLife(cellToCheckAlive[i]);
        }

        assertArrayEquals(expResult, result);
    }
    
    
    @Test (expected = unsupportedRuleException.class)
    public void testSetLifeSpecialRuleException() throws unsupportedRuleException {
        toLive = new byte[]{0};
        toSpawn = new byte[]{-1};
        rule = new CustomRule(toLive, toSpawn);

    }

}
