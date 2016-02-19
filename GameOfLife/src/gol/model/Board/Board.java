package gol.model.Board;

import gol.model.Logic.ConwaysRule;
import gol.model.Logic.Rule;

/**
 * @author s305054, s305084, s305089
 */
public abstract class Board {

    //Variabels
    protected double cellSize;
    protected double gridSpacing;
    private Rule activeRule;

    /**
     *
     * @param cellSize
     * @param gridSpacing
     */
    public Board(double cellSize, double gridSpacing) {
        setCellSize(cellSize);
        setGridSpacing(gridSpacing);
        activeRule = new ConwaysRule();
    }

    public void nextGen() {
        countNeigh();
        checkRules(activeRule);
    }

    public final void setCellSize(double cellSize) {
        if (cellSize == 0) {
            this.cellSize = 0.001;
        } else if (cellSize < 0) {
            this.cellSize = Math.abs(cellSize);
        } else {
            this.cellSize = cellSize;
        }
    }

    public double getGridSpacing() {
        return gridSpacing;
    }

    public final void setGridSpacing(double gridSpacing) {
        if (gridSpacing < 0) {
            this.gridSpacing = Math.abs(gridSpacing);
        } else {
            this.gridSpacing = gridSpacing;
        }
    }

    public double getCellSize() {
        return cellSize;
    }

    public void setGameRule(Rule activeRule) {
        this.activeRule = activeRule;
    }
    
    

    // Abstract Methods 

    public abstract void setCellState(double x, double y, boolean alive);

    public abstract boolean getCellState(int x, int y);
    
    public abstract boolean getCellStateFromMouseClick (double x, double y);

    public abstract void clearBoard();

    public abstract Object getGameBoard();

    public abstract int getArrayLength();

    public abstract int getArrayLength(int i);

    protected abstract void countNeigh();
    
    protected abstract void checkRules(Rule activeRule);

}
