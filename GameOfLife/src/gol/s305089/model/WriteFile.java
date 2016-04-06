package gol.s305089.model;

import gol.model.Board.Board;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author s305089
 */
public final class WriteFile {

    private WriteFile() {
    }
    private static String patternName, author, comment;

    /**
     *
     * @param boardToWrite
     * @param saveLocation
     * @return True if file was sucsessfully written to, otherwise false
     */
    public static boolean writeToRLE(byte[][] boardToWrite, Path saveLocation) {

        int xRows = 0;
        int yCols = 0;
        xRows = boardToWrite.length;
        for (byte[] gameRow : boardToWrite) {
            if (gameRow.length > yCols) {
                yCols = gameRow.length;
            }
        }
        String bordDimensions = "x = " + xRows + ", y = " + yCols;

        try (BufferedWriter writer = Files.newBufferedWriter(saveLocation)) {
            if (patternName != null) {
                writer.append("#N: " + patternName);
                writer.newLine();
            }
            if (author != null) {
                writer.append("#O: " + author);
                writer.newLine();
            }
            if (comment != null) {
                writer.append("#C: " + comment.replace("\n", " "));
                writer.newLine();
            }
            
            writer.append(bordDimensions);
            //TODO append rules
            writer.newLine();
            writer.append(parseGameBoard(boardToWrite));

            patternName = null;
            author = null;
            comment = null;

            return true;
        } catch (IOException e) {

        }

        return false;
    }

    public static void setPatternName(String newPatternName) {
        patternName = newPatternName;
    }

    public static void setAuthor(String patternAuthor) {
        author = patternAuthor;
    }

    public static void setComment(String patternComment) {
        comment = patternComment;
    }

    private static StringBuilder compressedRow(StringBuilder row) {
        StringBuilder result = new StringBuilder();
        char[] rowArray = row.toString().toCharArray();

        int countOfSameChar = 0;
        char curentChar;
        char lastChar;
        for (int i = 0; i < rowArray.length; i++) {
            if (i == 0) {
                lastChar = rowArray[i];
            } else {
                lastChar = rowArray[i - 1];
            }
            curentChar = rowArray[i];
            if (curentChar == lastChar) {
                countOfSameChar++;
            } else {
                if (countOfSameChar > 1) {
                    result.append(countOfSameChar);
                    result.append(lastChar);
                    result.append('$');
                } else if (countOfSameChar == rowArray.length - 1) {
                    result.append(countOfSameChar);
                } else if (curentChar == '$') {
                    result.append(curentChar);
                } else {
                    result.append(lastChar);
                }
                countOfSameChar = 0;
            }
        }
        return result;
    }

    private static StringBuilder parseGameBoard(byte[][] boardToWrite) {
        StringBuilder patternFile = new StringBuilder();
        StringBuilder row = new StringBuilder();
        for (byte[] gameRow : boardToWrite) {
            for (int j = 0; j < gameRow.length; j++) {
                if (gameRow[j] == 64) {
                    row.append('o');
                } else {
                    row.append('b');
                }
            }
            row.append('$');
            row = compressedRow(row);

            patternFile.append(row);
            patternFile.append(System.lineSeparator());
            row.delete(0, row.length());
        }
        return patternFile;
    }

}
