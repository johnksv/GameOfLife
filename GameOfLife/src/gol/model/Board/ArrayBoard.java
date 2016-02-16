package gol.model.Board;

/**
 * @author s305084, s305089, s305054
 */
public class ArrayBoard extends Board {

    private byte[][] gameBoard = new byte[][] {
            
        {1, 0, 0, 1},
        {0, 1, 1, 0},
        {0, 1, 1, 0},
        {1, 0, 0, 1}
    };

    public ArrayBoard(double cellSize, double gridSpacing) {
        super(cellSize, gridSpacing);
    }

    @Override
    public void clearBoard() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int length() {
        return gameBoard.length;
    }

    @Override
    public int length(int i) {
        return gameBoard[i].length;
    }

    @Override
    public byte[][] getGameBoard() {
        return gameBoard;
    }

    @Override
    public void setCellState(double x, double y, boolean alive) {

        /*
         * y is position of the first index of the matrix (column)
         * x is position of the second index of the matrix (row)
         */
        y = y / (cellSize + gridSpacing);
        x = x / (cellSize + gridSpacing);
        gameBoard[(int) y][(int) x] = 1;
    }

    @Override
    public boolean getCellState(int x, int y) {
        //TODO: Make with bit magic, not 1 and 0.
        if (gameBoard[x][y] == 1) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        String result = "";
        for (int i = 0; i < length(); i++) {
            for (int j = 0; j < length(i); j++) {
                result += gameBoard[i][j];
            }
            result += "\n";
        }
        return result;
    }
}
