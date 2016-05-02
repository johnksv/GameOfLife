package gol.model.Board;

import gol.model.Logic.Rule;
import java.util.Arrays;

/**
 * This class implements all needed features for a Board in game of Life. The
 * most vital are the nexGen method.
 *
 * @author s305054, s305084, s305089
 */
@Deprecated
public class ArrayBoard extends Board {

    private final int HEIGHT, WIDTH;
    private byte[][] gameBoard;

    /**
     * Constructs a new Arrayboard with default width and height
     */
    public ArrayBoard() {
        super();
        HEIGHT = 200;
        WIDTH = 200;
        gameBoard = new byte[HEIGHT][WIDTH];
    }

    /**
     * Constructs a new Arrayboard with given width and height
     *
     * @param width the width of the gameboard
     * @param height the height of the gameboard
     */
    public ArrayBoard(int width, int height) {
        super();
        this.HEIGHT = width;
        this.WIDTH = height;
        gameBoard = new byte[HEIGHT][WIDTH];

    }

    /*
     * Bruker gameBoard.length i stedet for getArrayLength(i/j) for å ta med ramme.
     */
    @Override
    public void clearBoard() {
        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard.length; j++) {
                gameBoard[i][j] = 0;
            }
        }
    }

    @Override
    protected void countNeigh() {

        //Goes through the board
        for (int i = 1; i < getArrayLength(); i++) {
            for (int j = 1; j < getArrayLength(i); j++) {

                //If cell is alive
                if (gameBoard[i][j] >= 64) {

                    //Goes through surrounding neighbours
                    for (int k = -1; k <= 1; k++) {
                        for (int l = -1; l <= 1; l++) {

                            //To not count itself
                            if (!(k == 0 && l == 0)) {
                                gameBoard[i + k][j + l] += 1;
                            }
                        }
                    }
                }

            }
        }
    }

    @Override
    protected void checkRules(Rule activeRule) {
        for (int i = 1; i < (gameBoard.length - 1); i++) {
            for (int j = 1; j < (gameBoard[i].length - 1); j++) {
                if (gameBoard[i][j] != 0) {
                    gameBoard[i][j] = activeRule.setLife(gameBoard[i][j]);
                }
            }
        }
    }

    @Override
    public void insertArray(byte[][] boardToInsert, int y, int x) {
        for (int i = 0; i < boardToInsert.length; i++) {
            for (int j = 0; j < boardToInsert[i].length; j++) {
                if (i + y < gameBoard.length && j + x < gameBoard[y + i].length) {
                    if (i + y >= 1 && j + x >= 1) {
                        gameBoard[i + y][j + x] = boardToInsert[i][j];
                    }
                }
            }
        }
    }

    @Override
    public void setCellState(int y, int x, boolean alive) {
        byte value = 0;
        if (alive) {
            value = 64;
        }
        if (y < gameBoard.length && y >= 0) {
            if (x < gameBoard[y].length && x >= 0) {
                gameBoard[y][x] = value;
            } else {
                System.err.println("x or y was not in gameboard.");
            }

        } else {
            System.err.println("x or y was not in gameboard.");
        }
    }

    @Override
    public void setCellState(double y, double x, boolean alive, double offsetX, double offsetY) {

        /*
         * y is position of the first index of the matrix (column)
         * x is position of the second index of the matrix (row)
         */
        y = y / (cellSize + gridSpacing);
        x = x / (cellSize + gridSpacing);
        offsetY = offsetY / (cellSize + gridSpacing);
        offsetX = offsetX / (cellSize + gridSpacing);

        setCellState((int) Math.floor(y - offsetY), (int) Math.floor(x - offsetX), alive);
    }

    @Override
    public int getArrayLength() {
        return gameBoard.length - 1;
    }

    @Override
    public int getArrayLength(int i) {
        return gameBoard[i].length - 1;
    }

    @Override
    public byte[][] getBoundingBoxBoard() {

        int[] boundingBox = getBoundingBox();
        if ((boundingBox[1] - boundingBox[0] + 1) > 0 || (boundingBox[3] - boundingBox[2] + 1) > 0) {
            byte[][] board = new byte[boundingBox[1] - boundingBox[0] + 1][boundingBox[3] - boundingBox[2] + 1];

            for (int y = 0; y < board.length; y++) {
                for (int x = 0; x < board[y].length; x++) {
                    if (gameBoard[boundingBox[0] + y][x + boundingBox[2]] == 64) {
                        board[y][x] = 64;
                    } else {
                        board[y][x] = 0;
                    }
                }
            }
            return board;
        } else {
            return new byte[][]{{}};
        }
    }

    @Override
    public int[] getBoundingBox() {
        int[] boundingBox = new int[4]; // minrow maxrow mincolumn maxcolumn 
        boundingBox[0] = gameBoard.length;
        boundingBox[1] = 0;
        boundingBox[2] = gameBoard[0].length;
        boundingBox[3] = 0;
        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[i].length; j++) {
                if (gameBoard[i][j] != 64) {
                    continue;
                }
                if (i < boundingBox[0]) {
                    boundingBox[0] = i;
                }
                if (i > boundingBox[1]) {
                    boundingBox[1] = i;
                }
                if (j < boundingBox[2]) {
                    boundingBox[2] = j;
                }
                if (j > boundingBox[3]) {
                    boundingBox[3] = j;
                }
            }
        }
        return boundingBox;
    }

    @Override
    public boolean getCellState(int y, int x) {
        if (y < 1 || y >= gameBoard.length) {
            return false;
        }
        if (x < 1 || x >= gameBoard[y].length) {
            return false;
        }
        return gameBoard[y][x] >= 64;
    }

    @Override
    public String toString() {

        StringBuilder result = new StringBuilder();
        for (byte row[] : gameBoard) {
            for (byte cell : row) {
                if (cell >= 64) {
                    result.append(1);
                } else {
                    result.append(0);
                }
            }
        }
        return result.toString();
    }

    @Override
    public int getMaxRowLength() {
        return WIDTH;
    }

    @Override
    protected void countNeighConcurrent(int threadNr) {
        throw new UnsupportedOperationException("Threads for ArrayBoard is not supported.");
    }

    @Override
    protected void checkRulesConcurrent(Rule activeRule, int threadNr) {
        throw new UnsupportedOperationException("Threads for ArrayBoard is not supported.");
    }

    @Override
    protected void expandBoard(int y, int x) {
        throw new UnsupportedOperationException("Threads for ArrayBoard is not supported.");
    }

    @Override
    protected int expandBoardY(int y) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected int expandBoardX(int y, int x) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
