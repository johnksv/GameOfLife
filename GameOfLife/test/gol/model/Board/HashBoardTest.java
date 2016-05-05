package gol.model.Board;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author s305084 - Stian Stensli
 */
public class HashBoardTest {

    byte[][] patternGlider;

    public HashBoardTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        patternGlider = new byte[][]{
            {0, 64, 0},
            {0, 0, 64},
            {64, 64, 64}};
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testNextGen() {
        System.out.println("nextGen/evolve");
        HashBoard instance = new HashBoard();
        instance.insertArray(patternGlider, 5, 5);
        instance.nextGen();
        assertArrayEquals(new byte[][]{{64,0, 64}, {0, 64, 64}, {0, 64, 0}}, instance.getBoundingBoxBoard());
        //With the values hashed.
        instance.clearBoard();
        instance.insertArray(patternGlider, 5, 5);
        instance.nextGen();
        assertArrayEquals(new byte[][]{{64,0, 64}, {0, 64, 64}, {0, 64, 0}}, instance.getBoundingBoxBoard());
        

    }
}
