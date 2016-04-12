package gol.model.FileIO;

import gol.model.Logic.CustomRule;
import gol.model.Logic.Rule;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author s305054, s305084, s305089
 */
public class ReadFileTest {

    public ReadFileTest() {
    }

    @Test
    public void testReadFileFromDisk() throws Exception {
        System.out.println("readFileFromDisk");
        File url;
        Path file;
        byte[][] expResult;
        byte[][] result;

        url = new File("test/patternTestFiles/glider.cells");
        file = Paths.get(url.toURI());
        expResult = new byte[][]{{0, 64, 0}, {0, 0, 64}, {64, 64, 64}};
        result = ReadFile.readFileFromDisk(file);
        assertArrayEquals(expResult, result);

        url = new File("test/patternTestFiles/glider.rle");
        file = Paths.get(url.toURI());
        result = ReadFile.readFileFromDisk(file);
        assertArrayEquals(expResult, result);

        url = new File("test/patternTestFiles/custom.cells");
        file = Paths.get(url.toURI());
        expResult = new byte[][]{{0, 0, 0}, {0, 64, 64}, {0, 64, 64}};
        result = ReadFile.readFileFromDisk(file);
        assertArrayEquals(expResult, result);

        url = new File("test/patternTestFiles/custom.rle");
        file = Paths.get(url.toURI());
        result = ReadFile.readFileFromDisk(file);
        assertArrayEquals(expResult, result);

    }

    @Test
    public void testReadFileFromDiskRule() throws Exception {
        
        File url = new File("test/patternTestFiles/glider.rle");
        Path file= Paths.get(url.toURI());
        byte[] expResultSurvive =new byte[]{2,3};
        byte[] expResultToBorn =new byte[]{3};
        
        byte[] resultBorn;
        byte[] resultSurvive;
        
        
        ReadFile.readFileFromDisk(file);
        Rule importedRule = ReadFile.getParsedRule();
        System.out.println(Arrays.toString(importedRule.getSurvive()));
        
        System.out.println(Arrays.toString(importedRule.getToBorn()));
        assertArrayEquals(expResultSurvive, importedRule.getSurvive());
        assertArrayEquals(expResultToBorn, importedRule.getToBorn());
        
        url = new File("test/patternTestFiles/custom.rle");
        file= Paths.get(url.toURI());
        expResultSurvive =new byte[]{2,3,5};
        expResultToBorn =new byte[]{2,3,7};
        
        
        
        ReadFile.readFileFromDisk(file);
        importedRule = ReadFile.getParsedRule();
        System.out.println(Arrays.toString(importedRule.getSurvive()));
        
        System.out.println(Arrays.toString(importedRule.getToBorn()));
        assertArrayEquals(expResultSurvive, importedRule.getSurvive());
        assertArrayEquals(expResultToBorn, importedRule.getToBorn());
        
        url = new File("test/patternTestFiles/glider_2.rle");
        file= Paths.get(url.toURI());
        expResultSurvive =new byte[]{2,3};
        expResultToBorn =new byte[]{3};
        
        
        
        ReadFile.readFileFromDisk(file);
        importedRule = ReadFile.getParsedRule();
        System.out.println(Arrays.toString(importedRule.getSurvive()));
        
        System.out.println(Arrays.toString(importedRule.getToBorn()));
        assertArrayEquals(expResultSurvive, importedRule.getSurvive());
        assertArrayEquals(expResultToBorn, importedRule.getToBorn());
        
    }
}
