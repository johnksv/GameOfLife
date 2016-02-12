package gol.model.Board;

/**
 * @author s305084, s305089, s305054
 */
public class ArrayBoard extends Board {

    private byte[][] gameBoard = {
        {1, 0, 0, 1},
        {0, 1, 1, 0},
        {0, 1, 1, 0},
        {1, 0, 0, 1}
    };

    public ArrayBoard(int cellSize, int gridSpacing) {
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
    public void setCellState(int x, int y, boolean alive) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getCellState(int x, int y) {
        //TODO: Make with bit magic, not 1 and 0.
        if (gameBoard[x][y] == 1) {
            return true;
        }
        return false;
    }

}
