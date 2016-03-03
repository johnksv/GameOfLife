package gol.model.FileIO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * @author s305054, s305084, s305089
 */
public class ReadFile {

    private static final StringBuilder metadata = new StringBuilder();

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

        List<String> readFileList = Files.readAllLines(file);

        String[] readFile = readFileList.toArray(new String[0]);

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
        int greatestlength = 0;
        for (String line : file) {
            if (line.length() > greatestlength && !(line.startsWith("!"))) {
                greatestlength = line.length();
            }
        }
        byte[][] activeBoard = new byte[file.length][greatestlength];

        for (int i = 0; i < file.length; i++) {
            if (file[i].startsWith("!")) {
                appendMetadata(file[i]);
            } else {

                char[] charArray = file[i].toCharArray();

                for (int j = 0; j < greatestlength; j++) {
                    if (j < charArray.length) {
                        switch (Character.toLowerCase(charArray[j])) {
                            case 'o':
                                activeBoard[i][j] = 64;
                                break;
                            case '.':
                                activeBoard[i][j] = 0;
                                break;
                            default:
                                throw new PatternFormatException("Error in file");
                        }
                    } else {
                        activeBoard[i][j] = 0;
                    }
                }
            }
        }
        return activeBoard;
    }

    private static void appendMetadata(String metadataLine) {
        metadata.append(metadataLine).append("\n");
    }

    private static String getMetadata() {
        return metadata.toString();
    }

}
