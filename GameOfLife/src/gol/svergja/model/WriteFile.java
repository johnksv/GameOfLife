package gol.svergja.model;

import gol.model.FileIO.PatternFormatException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author s305089 - John Kasper Svergja
 */
public final class WriteFile {

    private WriteFile() {
    }
    private static String patternName, author, comment;

    /**
     * Writes this byte board to RLE-format, and saves at this path.<b>The given
     * byte board should be rectangular.</b>If the byte board is empty (length is
     * zero), the method will return false,as it does not make sense to write an
     * empty board to a file.
     *
     * @param boardToWrite Byte board that should be written to RLE
     * @param saveLocation Where the RLE file should be stored
     * @return True if file was successfully written to. Otherwise false.
     */
    public static boolean writeToRLE(byte[][] boardToWrite, Path saveLocation) {
        //Check if the board is empty. We know its rectangular, so its ok to check index 0.
        if (boardToWrite[0].length == 0) {
            return false;
        }

        int xRows = 0;
        int yCols = boardToWrite.length;
        for (byte[] gameRow : boardToWrite) {
            if (gameRow.length > xRows) {
                xRows = gameRow.length;
            }
        }
        String bordDimensions = "x = " + xRows + ", y = " + yCols;

        try (BufferedWriter writer = Files.newBufferedWriter(saveLocation)) {
            if (patternName != null && !patternName.equals("")) {
                writer.append("#N: " + patternName);
                writer.newLine();
            }
            if (author != null && !author.equals("")) {
                writer.append("#O: " + author);
                writer.newLine();
            }
            if (comment != null && !comment.equals("")) {
                writer.append("#C: " + comment.replace("\n", " "));
                writer.newLine();
            }


            writer.append(bordDimensions);
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

    /**
     * Sets the name of the pattern to be used when writing file to RLE
     * @param newPatternName The name of the pattern that will be written
     */
    public static void setPatternName(String newPatternName) {
        patternName = newPatternName;
    }

    /**
     * Sets the author to be used when writing file to RLE
     * @param patternAuthor The name of the author
     */
    public static void setAuthor(String patternAuthor) {
        author = patternAuthor;
    }

    /**
     * Sets the comment to be used when writing file to RLE
     * @param patternComment The comment to the pattern
     */
    public static void setComment(String patternComment) {
        comment = patternComment;
    }


    /**
     *
     * @param row
     * @return
     */
    private static StringBuilder compressedRow(StringBuilder row) {
        //TODO Rewrite Method. for empty rows it return $1$1$ and not 3$

        StringBuilder result = new StringBuilder();
        char[] rowArray = row.toString().toCharArray();

        int countOfSameChar = 0;
        char currentChar;
        char lastChar;
        for (int i = 0; i < rowArray.length; i++) {
            currentChar = rowArray[i];
            if (i == 0) {
                lastChar = currentChar;
            } else {
                lastChar = rowArray[i - 1];
            }

            if (i == rowArray.length - 1) {
                if (countOfSameChar > 1) {
                    if (lastChar == 'b' && countOfSameChar == rowArray.length - 1) {
                    } else {
                        result.append(countOfSameChar);
                        result.append(lastChar);
                    }
                } else {
                    result.append(lastChar);
                }
                result.append(currentChar);
            } else if (lastChar == currentChar) {
                countOfSameChar++;
            } else {
                if (countOfSameChar > 1) {
                    result.append(countOfSameChar);
                    result.append(lastChar);
                } else {
                    result.append(lastChar);
                }
                countOfSameChar = 1;
            }
        }

        return result;
    }

    private static StringBuilder parseGameBoard(byte[][] boardToWrite) {
        StringBuilder patternFile = new StringBuilder();
        StringBuilder row = new StringBuilder();
        int numberOfEmptyRows = 0;
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
            String compressedRow = compressedRow(row).toString();
            if (compressedRow.equals("$")) {
                numberOfEmptyRows++;
            } else if (numberOfEmptyRows > 0) {
                patternFile.append(numberOfEmptyRows);
                patternFile.append('$');
                patternFile.append(compressedRow);
                numberOfEmptyRows = 0;
            } else {
                patternFile.append(compressedRow);
            }

            if (i % 8 == 1) {
                patternFile.append(System.lineSeparator());
            }

            row.delete(0, row.length());
        }
        return patternFile;
    }

}
