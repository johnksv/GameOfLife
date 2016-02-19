package gol.model.Board;

/**
 * @author s305054, s305084, s305089
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
    public int getArrayLength() {
        return gameBoard.length;
    }

    @Override
    public int getArrayLength(int i) {
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
        try{
        gameBoard[(int) y][(int) x] = 64;
        }catch (ArrayIndexOutOfBoundsException e){
            System.err.println("Click was outside canvas");
        }
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
     * Goes thorugh each living cell, and increments each neighbour
     * neighbours-count.
     */
    @Override
    protected void countNeigh() {

        //Goes through the board
        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[i].length; j++) {

                //If cell is alive
                if (gameBoard[i][j] >= 64) {

                    //Goes through surrounding neighbours
                    for (int k = -1; k <= 1; k++) {
                        for (int l = -1; l <= 1; l++) {

                            //To not count itself
                            if (!(k == 0 && l == 0)) {
                                try {
                                    gameBoard[i + k][j + l] += 1;
                                } catch (ArrayIndexOutOfBoundsException exception) {
                                    System.out.println("Position is out of bounds");
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
        for (byte row[] : gameBoard) {
            for (byte cell : row) {
                if (cell >= 64) {
                    result += 1;
                } else {
                    result += 0;
                }
            }
            result += "\n";
        }

        return result;
    }
}
