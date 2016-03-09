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

    public Board() {
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

    public void setGameRule(Rule activeRule) {
        this.activeRule = activeRule;
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

    public double getGridSpacing() {
        return gridSpacing;
    }

    // Abstract Methods 
    public abstract void clearBoard();

    protected abstract void countNeigh();

    protected abstract void checkRules(Rule activeRule);

    public abstract void insertArray(byte[][] boardFromFile, int i, int i0);

    public abstract void setCellState(int x, int y, boolean alive);

    public abstract void setCellState(double x, double y, boolean alive);

    protected abstract void setGameBoard(Object gameBoard);

    public abstract int getArrayLength();

    public abstract int getArrayLength(int i);

    public abstract boolean getCellState(int x, int y);

    public abstract boolean getCellState(double x, double y);

    public abstract Object getGameBoard();

}
