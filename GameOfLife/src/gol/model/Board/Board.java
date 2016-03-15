package gol.model.Board;

import gol.model.Logic.ConwaysRule;
import gol.model.Logic.Rule;

/**
 * The abstract class <code>Board</code> is the superclass of
 * <code>ArrayBoard</code> and <code>DynamicBoard</code>. The main objective of
 * this class is to store and compute next generation of activeBoard.
 * 
 * The board is represented through row-major arrays.
 * This implies that y represents rows, and x represents columns.
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
     * Sets the width and height of all cells. If input is less than 0.2, then
     * set to default value of 0.2.
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
     * Sets the active rule to this rule.
     *
     * @param activeRule The rule to be used when calculation next generation
     */
    public final void setGameRule(Rule activeRule) {
        this.activeRule = activeRule;
    }

    /**
     * Sets the spacing between each cell when the cells are drawn.
     *
     * @param gridSpacing A value of 0 or larger. If negativ, it sets this value
     * to the absolute value.
     */
    public final void setGridSpacing(double gridSpacing) {
        if (gridSpacing < 0) {
            this.gridSpacing = Math.abs(gridSpacing);
        } else {
            this.gridSpacing = gridSpacing;
        }
    }

    /**
     * Returns the current size of the cell.
     *
     * @return the width/height in pixels.
     */
    public double getCellSize() {
        return cellSize;
    }

    /**
     * Returns the current spacing between each cell in the grid.
     *
     * @return the current spacing in pixels.
     */
    public double getGridSpacing() {
        return gridSpacing;
    }

    
    /**
     * Get the representation of the smallest possible bord
     * </p>
     * Author: Henrik Lieng (Vedlegg 1 ark 5)
     * @return String of the smallest possible board
     */
    public abstract String getBoundingBoxPattern();
    
    /**
     * Calculates the smallest possible gamebord
     * </p>
     * Author: Henrik Lieng (Vedlegg 1 ark 5)
     * @return Array of minrow, maxrow, mincolumn, maxcolumn 
     */
    public abstract int[] getBoundingBox();
    
    /**
     * Sets all the cells in the board to dead. This method should call a method
     * to update the view (e.g. {@link gol.controller.GameController#draw()}.
     */
    public abstract void clearBoard();

    /**
     * Counts the number of living neighbors. This is done by going through each
     * living cell, and incrementing its neighbors.
     *
     * @see gol.model.Board.ArrayBoard#gameBoard
     * @see gol.model.Board.DynamicBoard TODO DynamicBoard
     */
    protected abstract void countNeigh();

    /**
     * Checks each cell to this rule. The cell is set to alive or dead,
     * depending on the rule.
     *
     * @param activeRule {@link gol.model.Logic.Rule}
     */
    protected abstract void checkRules(Rule activeRule);

    /**
     * Inserts a byte 2D-array into the current gameboard at the given (y, x)
     * position
     *
     * @param boardFromFile bytearray to insert into the current gameboard.
     * @param y coordinate for where the first row is placed
     * @param x coordinate for where the first column is placed
     */
    public abstract void insertArray(byte[][] boardFromFile, int y, int x);

    /**
     * Sets the cell state at position (y,x)
     *
     * @param y y coordinate of the cell
     * @param x x coordinate of the cell
     * @param alive The state to set the cell to. True for alive. False for
     * dead.
     */
    public abstract void setCellState(int y, int x, boolean alive);

    /**
     * Sets the cell state from canvas coordinats, given from a mouse click.
     *
     * @param y y coordinate of the mouse click.
     * @param x x coordinate of the mouse click.
     * @param alive The state to set the cell to. True for alive. False for
     * dead.
     * @param offsetY y offset of the canvas 
     * @param offsetX x offset of the canvas
     */
    public abstract void setCellState(double y, double x, boolean alive, double offsetX, double offsetY);


    /**
     * Returns the number of rows in the gameboard
     *
     * @return rows in the current gameboard
     */
    public abstract int getArrayLength();

    /**
     * Returns the number of elements in a row
     *
     * @param i the row to check
     * @return elements in row i
     */
    public abstract int getArrayLength(int i);

    /**
     * Returns the cell state at position (y,x)
     *
     * @param y the y coordinate of the cell
     * @param x the x coordinate of the cell
     * @return The cells state at position (y,x). true if alive. false if dead.
     */
    public abstract boolean getCellState(int y, int x);

    /**
     * Returns the cell state from canvas coordinats, given from a mouse click.
     * Mouse clicks are column major, therefore it recieves the parameters as (x,y).
     * @param x the x coordinate of the mouse click.
     * @param y the y coordinate of the mouse click.
     * @return The cells state at position (y,x). true if alive. false if dead.
     */
    public abstract boolean getCellState(double x, double y);

    /**
     *
     * @return
     */
    public abstract Object getGameBoard();

}
