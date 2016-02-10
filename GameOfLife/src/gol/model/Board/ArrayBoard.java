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

    public ArrayBoard(int cellWidth, int gridSpacing) {
        super(cellWidth, gridSpacing);
    }

    @Override
    public void setCellState(int x, int y, boolean alive) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean getCellState(int x, int y) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clearBoard() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getBoard() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
