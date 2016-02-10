package gol.model.Board;

import gol.model.Logic.Logic;

/**
 * @author s305084, s305089, s305054
 */
public abstract class Board extends Logic {

    //Variabels
    private int cellWidth;
    private int gridSpacing;

    /**
     *
     * @param cellWidth
     * @param gridSpacing
     */
    public Board(int cellWidth, int gridSpacing) {
        setCellWidth(cellWidth);
        setGridSpacing(gridSpacing);
    }

    /**
     * @return the cellWidth
     */
    public int getCellWidth() {
        return cellWidth;
    }

    /**
     * @param cellWidth the cellWidth to set
     */
    public final void setCellWidth(int cellWidth) {
        if (cellWidth == 0) {
            this.cellWidth = 10;
        } else if (cellWidth < 0) {
            this.cellWidth = -cellWidth;
        } else {
            this.cellWidth = cellWidth;
        }
    }

    /**
     * @return the gridSpacing
     */
    public int getGridSpacing() {
        return gridSpacing;
    }

    /**
     * @param gridSpacing the gridSpacing to set
     */
    public final void setGridSpacing(int gridSpacing) {
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

    public abstract Object getBoard();

}
