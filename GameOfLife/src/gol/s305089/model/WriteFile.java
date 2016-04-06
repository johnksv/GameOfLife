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

    private static void compressedRow(StringBuilder row) {
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
                    result.append(countOfSameChar + lastChar);
                } else if (countOfSameChar == rowArray.length) {
                    result.append(countOfSameChar);
                } else {
                    result.append(lastChar);
                }
                countOfSameChar = 0;
            }
        }
        result.append('$');
//TODO Om en hel rad er tom. Teste om dette fungerer
    }

    private WriteFile() {
    }

    /**
     *
     * @param saveLocation
     * @return True if file was sucsessfully written to, otherwise false
     */
    public static boolean writeToRLE(Board boardToWrite, Path saveLocation) {
        StringBuilder row = new StringBuilder();
        int countOfLivingRow;
        try (BufferedWriter writer = Files.newBufferedWriter(saveLocation)) {
            for (int i = 1; i < boardToWrite.getArrayLength(); i++) {
                countOfLivingRow = 0;
                for (int j = 1; j < boardToWrite.getArrayLength(i); j++) {
                    if (boardToWrite.getCellState(i, j)) {
                        row.append('o');
                    } else {
                        row.append('b');
                    }
                }
                row.append('$');
                compressedRow(row);

                writer.newLine();

            }

        } catch (IOException e) {

        }

        return false;
    }

}
