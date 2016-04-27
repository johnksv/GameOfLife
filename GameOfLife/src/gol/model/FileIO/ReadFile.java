package gol.model.FileIO;

import gol.model.Logic.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * This class has the necessary static methods for parsing files into
 * gameboards. Supported file formats are .cells, .rle
 *
 * @author s305054, s305084, s305089
 */
public class ReadFile {

    private static Rule parsedRule;

    private final static List<String> METADATA = new ArrayList<>(10);

    /**
     * Reads a file, makes an array. This static method checks the file format,
     * and calls for the correct method for parsing the file.
     *
     * @param file location of chosen file
     * @return a playable gameboard of type byte[][]
     * @throws IOException
     * @throws PatternFormatException if there are an error parsing or reading
     * the file
     */
    public static byte[][] readFileFromDisk(Path file) throws IOException, PatternFormatException {
        METADATA.clear();

        String path = file.toString();
        String[] token = path.split("\\.");
        String fileExt = token[token.length - 1];
        String[] readFile = Files.readAllLines(file).toArray(new String[0]);

        switch (fileExt) {
            case "cells":
                return readPlainText(readFile);
            case "rle":
                return readRLE(readFile);
            default:
                throw new PatternFormatException("Pattern format is not supported");
        }
    }

    public static void writeFromURL(String URLToSave) throws PatternFormatException, IOException {

        URL url = new URL(URLToSave);
        URLConnection connection = url.openConnection();

        String[] token = url.toString().split("\\.");
        String suffix = token[token.length - 1];

        Path saveLocation = File.createTempFile("golPattern", suffix).toPath();

        BufferedWriter writer;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            writer = Files.newBufferedWriter(saveLocation);
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
        }
        writer.close();

        readFileFromDisk(saveLocation);

    }

    /**
     * Returns the newly parsed Rule. If no rule was parsed from the last file
     * it will return ConwaysRule.
     *
     * @see Rule
     * @see ConwaysRule
     * @return Rule
     */
    public static Rule getParsedRule() {
        Rule returnRule = parsedRule;
        parsedRule = null;

        if (returnRule == null) {
            return new ConwaysRule();
        }
        return returnRule;
    }

    public static List<String> getMetadata() {
        return METADATA;
    }

    /**
     * Parses a PlainText file into a playable board.
     *
     * @param file array with rows of plaintext
     * @return a parsed board where living cells get the value 64, and dead
     * cells get the value 0
     * @throws IOException
     * @throws PatternFormatException Constructs a new exception, is thrown if
     * there is an error reading/parsing the file.
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
                appendMetaData(line.substring(1));
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
     * Parses the String array with rle format into a playable gameboard.
     *
     * @param file
     * @return playable gameboard
     * @throws IOException
     * @throws PatternFormatException if the given file is corrupt
     */
    private static byte[][] readRLE(String[] file) throws IOException, PatternFormatException {
        parsedRule = null;

        int commentLines = 0;
        for (String line : file) {
            if (line.startsWith("#")) {
                commentLines++;
                if (line.contains("#N")) {
                    appendMetaData("Name:" + line.substring(3));
                } else if (line.contains("#O")) {
                    appendMetaData("Author:" + line.substring(3));
                } else {
                    appendMetaData(line.substring(3));
                }
            }
        }

        StringBuilder pattern = new StringBuilder();
        int xLength = 0;
        int yLength = 0;
        int emptyLines = 0;

        //Reads x and y value from file.
        String[] attributes = file[commentLines].replaceAll("\\s", "").split(",");
        for (String line : attributes) {
            if (line.matches("x=\\d+")) {
                xLength = Integer.parseInt(line.replaceAll("\\D", ""));
            } else if (line.matches("y=\\d+")) {
                yLength = Integer.parseInt(line.replaceAll("\\D", ""));
            } else if (line.matches("rule.*")) {
                processRule(line);
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

        for (int i = 0; i < (yLength - offset); i++) {
            if (i >= lines.length) {
                throw new PatternFormatException("Missing end of file symbol");

            }
            String[] numbers = lines[i - emptyLines].split("\\D+");
            String[] letters = lines[i - emptyLines].split("\\d+");

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
            //Checks if there are empty lines.
            if ((numbers.length > letters.length)
                    || (numbers.length == letters.length && letters[0].equals(""))) {

                for (int k = 1; k < Integer.parseInt(numbers[numbers.length - 1]); k++) {
                    offset++;
                }

            }

        }
        throw new PatternFormatException("Missing end of file symbol.");
    }

    /**
     * Parsed board is made into a playable board.
     *
     * @param parsedBoard
     * @param letter
     * @param x column coordinate of board
     * @param y row coordinate of board
     * @throws PatternFormatException Constructs a new exception, is thrown if
     * there is an error reading/parsing the file.
     */
    private static void setCellStateRLE(byte[][] parsedBoard, char letter, int y, int x) throws PatternFormatException {
        if (y >= parsedBoard.length || x >= parsedBoard[y].length) {
            throw new PatternFormatException("Line exceeds the defined width. At line/\"$ number\": " + y);
        } else if (letter == 'b') {
            parsedBoard[y][x] = 0;

        } else if (letter == 'o') {
            parsedBoard[y][x] = 64;

        } else {
            throw new PatternFormatException("Unknow character at line: " + y);
        }

    }

    private static void processRule(String ruleLine) {
        byte[] born = null;
        byte[] survive = null;

        String[] rule = ruleLine.split("=");

        rule = rule[1].split("/");
        for (int i = 0; i < rule.length; i++) {
            if (rule[i].matches("[Ss]\\d*")) {
                if (rule[i].length() > 1) {
                    survive = new byte[rule[i].length() - 1];
                    for (int j = 1; j < rule[i].length(); j++) {
                        survive[j - 1] = (byte) Character.digit(rule[i].toCharArray()[j], 10);
                    }
                } else {
                    survive = new byte[]{-1};
                }

            } else if (rule[i].matches("[Bb]\\d*")) {
                if (rule[i].length() > 1) {
                    born = new byte[rule[i].length() - 1];
                    for (int j = 1; j < rule[i].length(); j++) {
                        born[j - 1] = (byte) Character.digit(rule[i].toCharArray()[j], 10);
                    }
                } else {
                    born = new byte[]{-1};
                }

            } else //expected Rule=3/23  (born/survive)
             if (i == 0) {
                    if (rule[i].length() >= 1) {
                        born = new byte[rule[i].length()];
                        for (int j = 0; j < rule[i].length(); j++) {
                            born[j] = (byte) Character.digit(rule[i].toCharArray()[j], 10);
                        }
                    } else {
                        born = new byte[]{-1};
                    }
                } else if (i == 1) {
                    if (rule[i].length() >= 1) {
                        survive = new byte[rule[i].length()];
                        for (int j = 0; j < rule[i].length(); j++) {
                            survive[j] = (byte) Character.digit(rule[i].toCharArray()[j], 10);
                        }
                    } else {
                        survive = new byte[]{-1};
                    }
                }
        }
        try {
            parsedRule = new CustomRule(survive, born);
        } catch (unsupportedRuleException ex) {
            parsedRule = new ConwaysRule();
        }
    }

    private static void appendMetaData(String line) {
        METADATA.add(line);
    }

}
