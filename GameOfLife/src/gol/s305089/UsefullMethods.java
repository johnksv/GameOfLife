package gol.s305089;

/**
 * @author John Kasper
 */
public class UsefullMethods {
    private UsefullMethods(){}

    public static byte[][] deepCopy(byte[][] board) {
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
     * Returns the max board size (row*column) for t iteterations
     * 
     */
    public static int[] calcualteMaxSize(int iterationsToCalculate){
        
        return null;
    }

    
    /**
     * Returns a board 
     */


}
