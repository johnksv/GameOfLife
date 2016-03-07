package gol.model.FileIO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * @author s305054, s305084, s305089
 */
public class ReadFile {

    /**
     * Leser filen, lagerer en array. Sjekker format, og kaller på metode for
     * parsing
     *
     *
     */
    public static byte[][] readFileFromDisk(Path file) throws IOException, PatternFormatException {
        String path = file.toString();
        String[] token = path.split("\\.");
        String fileExt = token[token.length - 1];
        List<String> list = Files.readAllLines(file);

        String[] readFile = list.toArray(new String[0]);

        switch (fileExt) {
            case "cells":
                return readPlainText(readFile);
            case "rle":
                return null;
            case "life":
                return null;
            default:
                throw new PatternFormatException("Wrong pattern format");
        }
    }

    private static byte[][] readPlainText(String[] file) throws IOException, PatternFormatException {
        int greatestlength = 0;
        int commentLines = 0;
        for (int i = 0; i < file.length; i++) {
            if (!file[i].startsWith("!")) {
                if (file[i].length() > greatestlength) {
                    greatestlength = file[i].length();
                }
            } else {
                commentLines++;
            }

        }
        byte[][] parsedBoard = new byte[file.length - commentLines][greatestlength];

        for (int i = 0; i < file.length - commentLines; i++) {
            char[] charArray = file[i + commentLines].toCharArray();

            for (int j = 0; j < greatestlength; j++) {
                if (j < charArray.length) {
                    if (Character.toLowerCase(charArray[j]) == 'o') {
                        parsedBoard[i][j] = 64;
                    } else if (charArray[j] == '.') {
                        parsedBoard[i][j] = 0;

                    } else {
                        throw new PatternFormatException("Error in file");
                    }
                } else {
                    parsedBoard[i][j] = 0;
                }
            }
        }
        return parsedBoard;
    }

    private static byte[][] readRLE(String[] file) throws IOException, PatternFormatException {

        StringBuilder pattern = new StringBuilder();

        for (String line : file) {
            pattern.append(line);
        }
        
        String[] lines = pattern.toString().split("$");
        
        byte[][] parsedBoard;
        

        return parsedBoard;
    }
}
