package gol.stensli;

import gol.model.Board.Board;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

/**
 * Contains static methods for writing a board to RLE.
 *
 * @author s305084 - Stian H. Stensli
 */
public final class WriteRLE {

    private WriteRLE() {
    }

    /**
     * Converts a non empty Board to RLE format. Supports the metadata: name,
     * author, comment
     *
     * @param path File location
     * @param board Pattern
     * @param name Matadata Name
     * @param author Matadata author
     * @param comment Matadata comment
     * @throws java.io.IOException if problems while saving
     */
    public static void toRLE(Path path, Board board, String name, String author, String comment) throws IOException {
        byte[][] g = board.getBoundingBoxBoard();
        List<String> lines = new LinkedList<>();

        //Meta tags
        if (!name.equals("")) {
            lines.add("#N " + name);
        }
        if (!author.equals("")) {
            lines.add("#O " + author);
        }
        if (!comment.equals("")) {
            lines.add("#C " + comment);
        }
        //Pattern dimensions. No other rule then conway's are supported.
        lines.add("x = " + g[0].length + ", y = " + g.length + ", Rule = B3/S23");
        int counter = 0;

        //Write pattern
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < g.length; i++) {
            byte last = g[i][0];

            for (byte cell : g[i]) {
                if (cell != last) {
                    if (last == 64) {
                        appendChar(counter, 'o', line);

                    } else {
                        appendChar(counter, 'b', line);
                    }
                    
                    //New line if a line gets above 70 characters 
                    if (line.length() > 70) {
                        lines.add(line.toString());
                        line.delete(0, line.length());
                    }

                    counter = 1;
                    last = cell;

                } else {
                    counter++;
                }
            }
            if (last == 64) {
                appendChar(counter, 'o', line);
            }
            counter = 0;
            i = appendBlank(line, g, i);
        }
        line.append("!");
        lines.add(line.toString());

        Files.write(path, lines);

    }

    /**
     * Sets all end of line signs, and handles end of file sign. If several rows
     * are blank, this method sets the number of blank lines before the dollar
     * sign. e.g.: writes bob2$bob! not bob$3b$bob!
     *
     * appendBlank makes sure the file do not end with:"$!" e.g.: bob$!
     *
     */
    private static int appendBlank(StringBuilder line, byte[][] pattern, int i) {
        //returns if the last line
        if (i + 1 >= pattern.length) {
            return i;
        }
        int blankCount = 1;
        boolean blankLine = true;
        for (int j = i + 1; j < pattern.length; j++) {
            for (byte cell : pattern[j]) {
                if (cell == 64) {
                    blankLine = false;
                }
            }
            if (blankLine) {
                blankCount++;
            }
        }

        if (blankCount == 1) {
            line.append('$');
            return i;
        } else {
            line.append(blankCount).append('$');
            return i + blankCount - 1;
        }
    }

    private static void appendChar(int i, char letter, StringBuilder line) {
        if (i == 1) {
            line.append(letter);
        } else {
            line.append(i).append(letter);
        }
    }
}
