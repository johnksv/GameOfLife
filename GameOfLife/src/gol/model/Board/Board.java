package gol.model.Board;

import gol.model.Logic.Logic;

/**
 * @author s305084, s305089, s305054
 */
public abstract class Board extends Logic {

    //Variabels
    private double cellSize;
    private double gridSpacing;

    /**
     *
     * @param cellSize
     * @param gridSpacing
     */
    public Board(int cellSize, int gridSpacing) {
        setCellSize(cellSize);
        setGridSpacing(gridSpacing);
    }

    /**
     * @return the cellSize
     */
    public double getCellSize() {
        return cellSize;
    }

    /**
     * @param cellSize the cellSize to set
     */
    public final void setCellSize(double cellSize) {
        if (cellSize == 0) {
            this.cellSize = 10;
        } else if (cellSize < 0) {
            this.cellSize = Math.abs(cellSize);
        } else {
            this.cellSize = cellSize;
        }
    }

    /**
     * @return the gridSpacing
     */
    public double getGridSpacing() {
        return gridSpacing;
    }

    /**
     * @param gridSpacing the gridSpacing to set
     */
    public final void setGridSpacing(double gridSpacing) {
        if (gridSpacing == 0) {
            this.gridSpacing = 10;
        } else if (gridSpacing < 0) {
            this.gridSpacing = -gridSpacing;
        } else {
            this.gridSpacing = gridSpacing;
        }
    }

    /*
     * Abstract Methods
     */
    public abstract void setCellState(int x, int y, boolean alive);

    public abstract boolean getCellState(int x, int y);

    public abstract void clearBoard();

    public abstract Object getGameBoard();

    public abstract int length() ;
    
    public abstract int length(int i) ;

}
