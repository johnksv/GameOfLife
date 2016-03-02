package gol.model.FileIO;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author s305054, s305084, s305089
 */
public class ReadFile {

    
    /**
     *Leser filen, lagerer en array. Sjekker format, 
     *      og kaller på metode for parsing
     * 
     * 
     */
    public static void readGameBoard(BufferedReader r) throws IOException {
        String line = null;
        while ((line = r.readLine()) != null) {
            System.out.println(line);
        }
        
    }

    public static void readGameBoardFromDisk(Path file) throws IOException {
        readGameBoard(Files.newBufferedReader(file));
    }
}
