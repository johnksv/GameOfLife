package gol.model.Board;

import gol.model.Logic.ConwaysRule;
import gol.model.Logic.Rule;
import gol.model.ThreadPool;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The abstract class <code>Board</code> is the superclass of
 * <code>ArrayBoard</code> and <code>DynamicBoard</code>. The main objective of
 * this class is to ensure functionality to subclasses, and compute next
 * generation of activeBoard.
 *
 * The board is represented through row-major arrays. This implies that y
 * represents rows, and x represents columns.
 * <h3>Technical information</h3><p>
 * All calculations is done on the same underlaying gameboard. This is made
 * possible by taking use of the fact that a byte consists of 8 bits (where the
 * 8th bit (MSB) is because of 2-complement).</p>
 * <p>
 * <b>The 7th-bit (64) represents alive.</b>
 * This means that living cell will have a value of 64, while dead cells has the
 * value 0.
 * <b>The first 4 least significant bits represents neighbour count.</b>
 * </p>
 *
 * <h4>Counting neighbours</h4>
 * Counting of neighbours is done by incrementing the current cell for each
 * living neighbours.
 * <p>
 * E.g. Consider the following board, where x is the current cell, 1 is alive,
 * and 0 is dead:</p>
 *
 * <pre>
 * 010
 * 0x0
 * 010
 * </pre> x has 2 neighbours. After counting, x will have incremented its value
 * by 2. If x was alive while counting, its new value would have been 64+2 = 66.
 * If x was dead, its value would have been 0+2 = 2.
 *
 * <h4>Check rules</h4>
 * After the neighbours have been counted, we check the new value of each cell
 * with the given rule. For Conways standard rules (spawn at 3, survive at 2 and
 * 3) this means that the value of a cell must be 3, 66, or 67 to be alive next
 * generation.
 *
 * <p>
 * This method for counting neighbours and checking rules means that we don't
 * need to work with a second array, and calculations can be done in real-time
 * on the actual game board. </p>
 *
 *
 * @author s305054, s305084, s305089
 */
public abstract class Board {

    /**
     * Width and height of all cells, defined in pixels
     */
    protected double cellSize = 5;

    /**
     * Gives access for use of Threadpool
     *
     * @see ThreadPool
     */
    protected final ThreadPool threadPool = new ThreadPool();

    /**
     * * If the board should expand in left (x) direction next generation.
     */
    protected final AtomicBoolean EXPAND_X = new AtomicBoolean();
    /**
     * If the board should expand in top (y) direction next generation.
     */
    protected final AtomicBoolean EXPAND_Y = new AtomicBoolean();

    /**
     * Offset of each cell in the board.
     * 
     * <table summary="Content of offsetValue">
     * <tr><th>Index</th><th>Value</th></tr>
     * <tbody>
     * <tr><td>0</td><td>Current offset x</td></tr>
     * <tr><td>1</td><td>Current offset y</td></tr>
     * <tr><td>2</td><td>Old mouse position x</td></tr>
     * <tr><td>3</td><td>Old mouse position</td></tr>
     * </tbody>
     * </table>
     */
    public final double[] offsetValues = {0, 0, -Double.MAX_VALUE, -Double.MAX_VALUE};

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
     * Call the required methods to create next generation with support for
     * multiple threads.
     *
     * @see #countNeighConcurrent(int)
     * @see #checkRulesConcurrent(gol.model.Logic.Rule, int)
     */
    public void nextGenConcurrent() {
        //May be work for expanding board.
        threadPool.runWorkers();

        for (int i = 0; i < ThreadPool.THREAD_NR; i++) {
            countNeighConcurrent(i);
        }
        threadPool.runWorkers();
        for (int i = 0; i < ThreadPool.THREAD_NR; i++) {
            checkRulesConcurrent(activeRule, i);
        }
        threadPool.runWorkers();

        if (EXPAND_X.get()) {
            threadPool.addWork(() -> {
                expandBoard(0, -1);
                EXPAND_X.set(false);
            });
        }
        if (EXPAND_Y.get()) {
            threadPool.addWork(() -> {
                expandBoard(-1, 0);
                EXPAND_Y.set(false);
            });
        }
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
     * Expands the board to fit the coordinates given.
     * Expands with the number of cells, defined as "expansion" in the config file.
     * Will not be implemented in {@link ArrayBoard arraybors.}
     * 
     * @param y pixel coordinate.
     * @param x pixel coordinate.
     */
    protected abstract void expandBoard(int y, int x);

    /**
     * Calculates the smallest possible array of living cells, and returns that array.
     * <p>
     * Author: Henrik Lieng (Vedlegg 1 ark 5)
     *
     * @return Array of minrow, maxrow, mincolumn, maxcolumn
     */
    public abstract byte[][] getBoundingBoxBoard();

    /**
     * Calculates the smallest possible gamebord
     * <p>
     * Author: Henrik Lieng (Vedlegg 1 ark 5)
     *
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
     */
    protected abstract void countNeigh();
     
    /**
     * Creates a runnable that counts the number of living neighbors. This is done by going through each
     * living cell, and incrementing its neighbors.
     * Adds the runnable to the current {@link #threadPool threadPool.}
     * 
     * @see #nextGenConcurrent() 
     */
    protected abstract void countNeighConcurrent(int threadNr);

    /**
     * Checks each cell to this rule. The cell is set to alive or dead,
     * depending on the rule.
     *
     * @param activeRule {@link gol.model.Logic.Rule}
     */
    protected abstract void checkRules(Rule activeRule);

    /**
     * Creates a runnable that checks each cell to this rule. The cell is set to alive or dead,
     * depending on the rule.
     * Adds the runnable to the current {@link #threadPool threadPool.}
     * 
     * @param activeRule {@link gol.model.Logic.Rule}
     */
    protected abstract void checkRulesConcurrent(Rule activeRule, int threadNr);

    /**
     * Inserts a byte 2D-array into the current gameboard at the given (y, x)
     * position.
     * <p>
     * For example: To insert boardFromFile to upper left corner of the current
     * gameboard, insert at position (0,0).
     * <p>
     * Elements from boardFromFile that exceeds the dimensions of the current
     * gameboard is not inserted.
     *
     *
     * @param boardToInsert bytearray to insert into the current gameboard.
     * @param y coordinate for where the first row is placed
     * @param x coordinate for where the first column is placed
     */
    public abstract void insertArray(byte[][] boardToInsert, int y, int x);

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
     * Returns the Maximum elements in a array row or List.
     *
     * @return elements in row i
     */
    public abstract int getMaxRowLength();

    /**
     * Returns the cell state at position (y,x)
     *
     * @param y the y coordinate of the cell
     * @param x the x coordinate of the cell
     * @return The cells state at position (y,x). true if alive. false if dead.
     */
    public abstract boolean getCellState(int y, int x);

}
