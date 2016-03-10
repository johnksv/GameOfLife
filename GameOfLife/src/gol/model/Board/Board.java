package gol.model.Board;

import gol.model.Logic.ConwaysRule;
import gol.model.Logic.Rule;

/**
 * The abstract class <code>Board</code> is the superclass of
 * <code>ArrayBoard</code> and <code>DynamicBoard</code>. The main objective of
 * this class is to store and compute next generation of activeBoard.
 *
 * @author s305054, s305084, s305089
 */
public abstract class Board {

    /**
     * Width and height of all cells, defined in pixels
     */
    protected double cellSize;

    /**
     * Padding between cells, defined in pixels
     */
    protected double gridSpacing;
    private Rule activeRule;

    /**
     * Constructs a new board, with Conway's rule as default
     *
     * @see gol.model.Logic.ConwaysRule
     */
    public Board() {
        activeRule = new ConwaysRule();
    }

    /**
     * Call the required methods to create next generation
     *
     * @see #countNeigh()
     * @see #checkRules(gol.model.Logic.Rule)
     */
    public void nextGen() {
        countNeigh();
        checkRules(activeRule);
    }

    /**
     * Sets the width and height of all cells. 
     * If input is less than 0.2, then set to default value 0.2.
     *
     * @param cellSize Cell size in pixels
     *
     */
    public final void setCellSize(double cellSize) {
        if (cellSize < 0.2) {
            this.cellSize = 0.2;
        } else {
            this.cellSize = cellSize;
        }
    }

    /**
     * Sets the active rule to this rule
     * 
     * @param activeRule 
     */
    public void setGameRule(Rule activeRule) {
        this.activeRule = activeRule;
    }

    /**
     *
     * @param gridSpacing
     */
    public final void setGridSpacing(double gridSpacing) {
        if (gridSpacing < 0) {
            this.gridSpacing = Math.abs(gridSpacing);
        } else {
            this.gridSpacing = gridSpacing;
        }
    }

    /**
     *
     * @return
     */
    public double getCellSize() {
        return cellSize;
    }

    /**
     *
     * @return
     */
    public double getGridSpacing() {
        return gridSpacing;
    }

    // Abstract Methods 
    /**
     *
     */
    public abstract void clearBoard();

    /**
     * Counts the number of living neighbors. 
     * This is done by going through each living cell, and incrementing its
     * neighbors.
     * 
     * @see gol.model.Board.ArrayBoard#gameBoard  
     * @see gol.model.Board.DynamicBoard 
     * TODO DynamicBoard 
     */
    protected abstract void countNeigh();

    /**
     * Checks each cell to this rule. The cell is set to
     * alive or dead, depending on the rule.
     * @param activeRule {@link gol.model.Logic.Rule}
     */
    protected abstract void checkRules(Rule activeRule);

    /**
     *
     * @param boardFromFile
     * @param x
     * @param y
     */
    public abstract void insertArray(byte[][] boardFromFile, int x, int y);

    /**
     *
     * @param x
     * @param y
     * @param alive
     */
    public abstract void setCellState(int x, int y, boolean alive);

    /**
     *
     * @param x
     * @param y
     * @param alive
     */
    public abstract void setCellState(double x, double y, boolean alive);

    /**
     *
     * @param gameBoard
     */
    protected abstract void setGameBoard(Object gameBoard);

    /**
     *
     * @return
     */
    public abstract int getArrayLength();

    /**
     *
     * @param i
     * @return
     */
    public abstract int getArrayLength(int i);

    /**
     *
     * @param x
     * @param y
     * @return
     */
    public abstract boolean getCellState(int x, int y);

    /**
     *
     * @param x
     * @param y
     * @return
     */
    public abstract boolean getCellState(double x, double y);

    /**
     *
     * @return
     */
    public abstract Object getGameBoard();

}
