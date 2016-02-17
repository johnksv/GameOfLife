package gol.model.Board;

/**
 * @author s305084, s305089, s305054
 */
public class ArrayBoard extends Board {

    private byte[][] gameBoard = {
        {0, 0, 0, 0},
        {0, 0, 64, 0},
        {0, 0, 0, 0},
        {64, 0, 0, 0}
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
        if (gameBoard[x][y] >= 64) {
            return true;
        }
        return false;
    }

    /**
     * Goes thorugh each living cell, and increments each neighbor
     * neighbors-count.
     */
    @Override
    public void countNeighbors() {
        
        //Goes through the board
        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[i].length; j++) {
                
                //If cell is alive
                if (gameBoard[i][j] >= 64) {
                    
                    //Goes through surrounding neighbors
                    for (int k = -1; k <= 1; k++) {
                        for (int l = -1; l <= 1; l++) {
                            
                            //To not count itself
                            if (!(k == 0 && l == 0)) {
                                try {
                                    gameBoard[i + k][j + l] += 1;
                                } catch (ArrayIndexOutOfBoundsException exception) {
                                    System.out.println("Out of bounds " + exception);
                                }
                            }
                        }
                    }

                }

            }
        }
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
