package gol.s305084;

import gol.model.Board.Board;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains static methods for writing a board to an rle file.
 *
 * @author Stian
 */
public final class WriteRLE {

    private WriteRLE() {
    }

    /**
     * Converts a non empty Board to RLE format. Supports the metadata: name,
     * author, comments
     *
     * @param path
     * @param board
     * @throws java.io.IOException
     */
    public static void toRLE(Path path, Board board) throws IOException {
        byte[][] g = board.getBoundingBoxBoard();
        List<String> lines = new ArrayList<>();
        lines.add("x = " + g[0].length + ", y = " + g.length+", Rule = ");
        Files.write(path, lines);
    }
/*
    private void toRLE(Sprite[][] k) throws IOException {
        String line = "";
        List<String> lines = new ArrayList<>();
        Path file = Paths.get("the-file-name.rle");
        for (Sprite[] l : k) {

            int teller = 0;

            line = "";
            for (int i = 0; i < l.length; i++) {
                if (i != 0) {

                    if (l[i].getAlive() == l[i - 1].getAlive()) {
                        teller++;
                    } else {

                        if (l[i - 1].getAlive() == true) {
                            line += teller + "l";//skriv til fil.
                        } else {
                            line += teller + "d";//skriv til fil.
                        }
                        teller = 1;
                    }
                } else {
                    teller++;
                }
            }
            if (l[l.length - 1].getAlive() == false) {
                line += teller + "d";
            } else {
                line += teller + "l";
            }

            lines.add(line);
        }

        Files.write(file, lines, Charset.forName("UTF-8"));
    }
*/
}
