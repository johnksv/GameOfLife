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
                    result.append(countOfSameChar + lastChar);
                } else if (countOfSameChar == rowArray.length-1) {
                    result.append(countOfSameChar);
                }else if(curentChar == '$'){
                    result.append(curentChar);
                }else {
                    result.append(lastChar);
                }
                countOfSameChar = 0;
            }
        }
        return result;
//TODO  Teste om dette fungerer
    }

    /**
     *
     * @param boardToWrite
     * @param saveLocation
     * @return True if file was sucsessfully written to, otherwise false
     */
    public static boolean writeToRLE(byte[][] boardToWrite, Path saveLocation) {
        StringBuilder row = new StringBuilder();
        
        try (BufferedWriter writer = Files.newBufferedWriter(saveLocation)) {
            for (int i = 0; i < boardToWrite.length; i++) {
                for (int j = 0; j < boardToWrite[i].length; j++) {
                    if (boardToWrite[i][j] == 64) {
                        row.append('o');
                    } else {
                        row.append('b');
                    }
                }
                row.append('$');
                row = compressedRow(row);
                System.out.println(row);
                writer.newLine();
                row.delete(0, row.length());
            }

        } catch (IOException e) {

        }

        return false;
    }

}
