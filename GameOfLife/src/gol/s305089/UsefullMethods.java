package gol.s305089;

import javafx.stage.Screen;

/**
 * @author John Kasper
 */
public class UsefullMethods {

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
     * @return Array with width at index 0, and Height at index 1
     */
    public static double[] getScreenSize(){
        double[] result = new double[2];
        result[0] = Screen.getPrimary().getVisualBounds().getWidth();
        result[1] = Screen.getPrimary().getVisualBounds().getHeight();
        return result;
    }

}
