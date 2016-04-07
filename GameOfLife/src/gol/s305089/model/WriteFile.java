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
        yCols = boardToWrite.length;
        for (byte[] gameRow : boardToWrite) {
            if (gameRow.length > xRows) {
                xRows = gameRow.length;
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

    public static StringBuilder compressedRow(StringBuilder row) {
        //TODO Rewrite Method. Returns wrong
        
        StringBuilder result = new StringBuilder();
        char[] rowArray = row.toString().toCharArray();

        int countOfSameChar = 0;
        char curentChar;
        char lastChar;
        for (int i = 0; i < rowArray.length; i++) {
            curentChar = rowArray[i];

            if (i == 0) {
                lastChar = curentChar;
            } else {
                lastChar = rowArray[i - 1];
            }

            if (curentChar == lastChar) {
                if (rowArray.length - 1 == 1) {
                    result.append(lastChar);
                } else {
                    countOfSameChar++;
                }
            } else {
                if (countOfSameChar > 1) {
                    result.append(countOfSameChar);
                    result.append(lastChar);
                    if (i == rowArray.length - 1) {
                        result.append(curentChar);
                    }
                } else if (countOfSameChar == rowArray.length - 1) {
                    result.append(countOfSameChar);
                } else if (curentChar == '$' || curentChar == '!') {
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
        for (int i = 0; i < boardToWrite.length; i++) {
            for (int j = 0; j < boardToWrite[i].length; j++) {
                if (boardToWrite[i][j] == 64) {
                    row.append('o');
                } else {
                    row.append('b');
                }
            }

            if (i != boardToWrite.length - 1) {
                row.append('$');
            } else {
                row.append('!');
            }
            patternFile.append(compressedRow(row));

            if (i != boardToWrite.length - 1) {
                patternFile.append(System.lineSeparator());
            }

            row.delete(0, row.length());
        }
        return patternFile;
    }

}
