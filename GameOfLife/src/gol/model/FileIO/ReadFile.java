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
        
        short linesOfComments = 0;
        //counts the number of comment lines
        for (String line : file) {
            if (line.startsWith("!")) {
                linesOfComments++;
            }
        }

        int greatestlength = 0;
        for (int i = 0; i < file.length; i++) {
            if (file[i].length() > greatestlength) {
                greatestlength = file[i].length();
            }
        }
        
        //the nummber of lines is removed from the total length
        byte[][] activeBoard = new byte[file.length - linesOfComments][greatestlength];

        for (int i = 0; i < (file.length - linesOfComments); i++) {
            char[] charArray = file[i + linesOfComments].toCharArray();

            for (int j = 0; j < greatestlength; j++) {
                if (j < charArray.length) {
                    if (Character.toLowerCase(charArray[j]) == 'o') {
                        activeBoard[i][j] = 64;
                    } else if (charArray[j] == '.') {
                        activeBoard[i][j] = 0;

                    } else {
                        throw new PatternFormatException("Error in file");
                    }
                } else {
                    activeBoard[i][j] = 0;
                }
            }
        }
        return activeBoard;
    }
}
