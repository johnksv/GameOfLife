package gol.model.FileIO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * This class has the necessary static methods for parsing files into gameboards.
 * 
 * @author s305054, s305084, s305089
 */
public class ReadFile {

    /**
     * Reads a file, makes an array. 
     * This static method checks the file format, and calls for the correct
     * method for parsing the file.
     * 
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
                throw new PatternFormatException("Pattern format is not supported");
        }
    }

    /**
     * Parses a PlainText file into a playable board.
     * 
     * @param file array with rows of plaintext
     * @return a parsed board where living cells get the value 64, and dead cells get the value 0
     * @throws IOException
     * @throws PatternFormatException Constructs a new exception, is thrown if there is an error reading/parsing the file.
     */
    private static byte[][] readPlainText(String[] file) throws IOException, PatternFormatException {
        int greatestlength = 0;
        int commentLines = 0;
        for (String line : file) {
            if (!line.startsWith("!")) {
                if (line.length() > greatestlength) {
                    greatestlength = line.length();
                }
            } else {
                //TODO appendMetaData(line);
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
                        throw new PatternFormatException("Unknow character at line: " + i);
                    }
                } else {
                    parsedBoard[i][j] = 0;
                }
            }
        }
        return parsedBoard;
    }

    /**
     * @bug number then $ is to say that there is that number of lines between
     * this line and the next 264bobo1447b6$ the last part is saying that there
     * is 6 blank lines before the next line
     *
     * @param file
     * @return
     * @throws IOException
     * @throws PatternFormatException
     * @throws ArrayIndexOutOfBoundsException
     */
    private static byte[][] readRLE(String[] file) throws IOException, PatternFormatException, ArrayIndexOutOfBoundsException {
        int commentLines = 0;
        for (String line : file) {
            if (line.startsWith("#")) {
                commentLines++;
                //TODO appendMetaData(line);
            }
        }

        StringBuilder pattern = new StringBuilder();
        int xLength = 0;
        int yLength = 0;
        int emptyLines =0;

        //Reads x and y value from file.
        String[] attributes = file[commentLines].replaceAll("\\s", "").split(",");
        for (String line : attributes) {
            if (line.matches("x=\\d+")) {
                xLength = Integer.parseInt(line.replaceAll("\\D", ""));
            } else if (line.matches("y=\\d+")) {
                yLength = Integer.parseInt(line.replaceAll("\\D", ""));
            }
        }
        if (xLength == 0 || yLength == 0) {
            throw new PatternFormatException("x or y in file is not found.");
        }

        byte[][] parsedBoard = new byte[yLength][xLength];

        //Appends the whole file to one line.
        for (int i = commentLines + 1; i < file.length; i++) {
            pattern.append(file[i]);
        }
        String[] lines = pattern.toString().split("\\$");

        int offset = 0;
        
        for (int i = 0; i < (yLength-offset); i++) {
            if (i >= lines.length) {
                throw new PatternFormatException("Missing end of file symbol TOP");

            }
            String[] numbers = lines[i-emptyLines].split("\\D+");
            String[] letters = lines[i-emptyLines].split("\\d+");

            int cellPosition = 0;
            int letterPosition = 0;

            for (int j = 0; j < letters.length; j++) {

                if (letters[j].length() != 0) {
                    if (j != 0) {

                        if (numbers[letterPosition].equals("")) {
                            letterPosition++;
                        }

                        for (int k = 0; k < Integer.parseInt(numbers[letterPosition]); k++) {

                            setCellStateRLE(parsedBoard, letters[j].charAt(0), i + offset, cellPosition);

                            cellPosition++;
                        }

                        letterPosition++;
                    } else {

                        setCellStateRLE(parsedBoard, letters[j].charAt(0), i + offset, cellPosition);

                        cellPosition++;
                    }

                    if (letters[j].length() > 1) {
                        for (int k = 1; k < letters[j].length(); k++) {
                            if (letters[j].charAt(k) == '!') {
                                return parsedBoard;
                            }

                            setCellStateRLE(parsedBoard, letters[j].charAt(k), i + offset, cellPosition);

                            cellPosition++;

                        }

                    }

                }
            }

            if ((numbers.length > letters.length) || 
               (numbers.length == letters.length && letters[0].equals(""))) {
                
                for (int k = 1; k < Integer.parseInt(numbers[numbers.length - 1]); k++) {
                    offset++;
                }
                System.out.println(offset);

            }

        }
        throw new PatternFormatException("Missing end of file symbol.");
    }

    /**
     * Parsed board is made into a playable board.
     * @param parsedBoard
     * @param letter
     * @param x column of board
     * @param y row of board
     * @throws PatternFormatException Constructs a new exception, is thrown if there is an error reading/parsing the file.
     */
    private static void setCellStateRLE(byte[][] parsedBoard, char letter, int x, int y) throws PatternFormatException {
        if (x >= parsedBoard.length || y >= parsedBoard[x].length) {
            throw new PatternFormatException("Line exceeds the defined width. After line/\"$ number\": " + x);
        } else {
            if (letter == 'b') {
                parsedBoard[x][y] = 0;

            } else if (letter == 'o') {
                parsedBoard[x][y] = 64;

            } else {
                throw new PatternFormatException("Unknow character at line: " + x);
            }
        }

    }
}
