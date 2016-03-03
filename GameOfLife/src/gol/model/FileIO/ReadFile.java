package gol.model.FileIO;

import java.io.BufferedReader;
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

        byte[][] activeBoard = new byte[file.length][file.length];

        for (int i = 0; i < file.length; i++) {
            char[] charArray = file[i].toCharArray();

            for (int j = 0; j < charArray.length; j++) {
                if (Character.toLowerCase(charArray[j]) == 'o') {
                    activeBoard[i][j] = 64;
                } else if (charArray[j] == '.') {
                    activeBoard[i][j] = 0;
                } else {
                    throw new PatternFormatException("Error in file");
                }
            }
        }
        return activeBoard;
    }
}
