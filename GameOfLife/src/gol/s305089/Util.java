package gol.s305089;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
import gol.model.Board.DynamicBoard;
import javafx.stage.Screen;

/**
 * @author John Kasper
 */
public class Util {

    private Util() {
    }

    public static int[] calculateBiggestDimension(byte[][] patternToCalculate, int iterations) {
        int[] result = new int[2];
        Board board = new ArrayBoard(1800, 1800);
        board.insertArray(patternToCalculate, 1, 1);

        for (int it = 0; it < iterations; it++) {
            byte[][] boundedBoard = board.getBoundingBoxBoard();
            result[0] = boundedBoard.length > result[0] ? boundedBoard.length : result[0];
            for (byte[] row : boundedBoard) {
                result[1] = row.length > result[1] ? row.length : result[1];
            }
            board.nextGen();
        }
        return result;
    }

    /**
     * Retrives the users screens width and height in pixels.
     *
     * @return Array with screen width at index 0, and screen height at index 1
     */
    public static double[] getScreenSize() {
        double[] result = new double[2];
        result[0] = Screen.getPrimary().getVisualBounds().getWidth();
        result[1] = Screen.getPrimary().getVisualBounds().getHeight();
        return result;
    }

}
