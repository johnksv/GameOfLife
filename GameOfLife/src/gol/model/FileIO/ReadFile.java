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
                return readRLE(readFile);
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
                        throw new PatternFormatException("Error in file.");
                    }
                } else {
                    parsedBoard[i][j] = 0;
                }
            }
        }
        return parsedBoard;
    }

    private static byte[][] readRLE(String[] file) throws IOException, PatternFormatException {
        //TODO commentHandling
        StringBuilder pattern = new StringBuilder();
        int xLength = 0;
        int yLength = 0;

        //Reads x and y value from file.
        String[] attributes = file[0].replaceAll("\\s", "").split(",");
        for (String line : attributes) {
            if (line.matches("x=\\d+")) {
                xLength = Integer.parseInt(line.replaceAll("\\D", ""));
            } else if (line.matches("y=\\d+")) {
                yLength = Integer.parseInt(line.replaceAll("\\D", ""));
            }
        }
        if (xLength == 0 || yLength == 0) {
            throw new PatternFormatException("Error in file. x or y is not found.");
        }

        byte[][] parsedBoard = new byte[yLength][xLength];

        //Appends the whole file to one line.
        for (int i = 1; i < file.length; i++) {
            pattern.append(file[i]);
        }

        String[] lines = pattern.toString().split("\\$");

        for (int i = 0; i < xLength; i++) {
            String[] numbers = lines[i].split("\\D+");
            String[] letters = lines[i].split("\\d+");
            int cellPosition = 0;

            int letterPosition = 0;
            for (String letter : letters) {
                if (letter.length() != 0) {

                    for (int k = 0; k < Integer.parseInt(numbers[letterPosition]); k++) {
                        if (letter.startsWith("b")) {
                            parsedBoard[i][cellPosition] = 0;

                        } else if (letter.startsWith("o")) {
                            parsedBoard[i][cellPosition] = 64;

                        }
                        cellPosition++;
                    }
                    if (letter.length() > 1) {
                        //TODO add 1 verdier
                    }

                    letterPosition++;
                }
            }

        }

        return parsedBoard;
    }
}
