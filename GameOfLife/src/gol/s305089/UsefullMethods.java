package gol.s305089;

import gol.model.Board.Board;
import gol.model.Board.DynamicBoard;
import javafx.stage.Screen;

/**
 * @author John Kasper
 */
public class UsefullMethods {

    public static int[] getBiggestDimension(byte[][] patternToCalculate, int iterations) {
        int[] result = new int[2];
        Board board = new DynamicBoard();
        board.insertArray(patternToCalculate, 1, 1);
        for (int i = 0; i < iterations; i++) {
            result[0] = patternToCalculate.length > result[0] ? patternToCalculate.length : result[0];
            for (byte[] row : patternToCalculate) {
                result[1] = row.length > result[1] ? row.length : result[1];
            }
            board.nextGen();
        }
        return result;
    }

    //TODO REMOVE
    //Made by håkon. Not used
    public byte[][] deepCopy(byte[][] board) {
        byte[][] deepCopyArray = new byte[board.length][];
        for (int i = 0; i < board.length; i++) {
            byte[] matrix = new byte[board[i].length];
            int length = matrix.length;
            System.arraycopy(board, 0, matrix, 0, length);
            deepCopyArray[i] = matrix;
        }
        return deepCopyArray;
    }

    /**
     * Gets the size of the screens width and height in pixels.
     *
     * @return Array with width at index 0, and Height at index 1
     */
    public static double[] getScreenSize() {
        double[] result = new double[2];
        result[0] = Screen.getPrimary().getVisualBounds().getWidth();
        result[1] = Screen.getPrimary().getVisualBounds().getHeight();
        return result;
    }

}
