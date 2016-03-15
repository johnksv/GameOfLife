package gol.model.Board;

import gol.model.Logic.Rule;

/**
 * //TODO Comment about Arrayboard
 *
 *
 *
 *
 * @author s305054, s305084, s305089
 */
public class ArrayBoard extends Board {

    private final int WIDTH, HEIGHT;
    private byte[][] gameBoard;

    /**
     * Constructs a new Arrayboard with default width and height
     */
    public ArrayBoard() {
        super();
        WIDTH = 1800;
        HEIGHT = 1800;
        gameBoard = new byte[WIDTH][HEIGHT];
    }

    /**
     * Constructs a new Arrayboard with given width and height
     *
     * @param width the width of the gameboard
     * @param height the height of the gameboard
     */
    public ArrayBoard(int width, int height) {
        super();
        this.WIDTH = width;
        this.HEIGHT = height;
        gameBoard = new byte[WIDTH][HEIGHT];

    }

    @Override
    public void clearBoard() {
        for (int i = 1; i < getArrayLength(); i++) {
            for (int j = 1; j < getArrayLength(i); j++) {
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
    public void insertArray(byte[][] boardFromFile, int y, int x) {
        for (int i = 0; i < boardFromFile.length; i++) {
            for (int j = 0; j < boardFromFile[i].length; j++) {
                if (i + y < gameBoard.length && j + x < gameBoard[y + i].length) {
                    if (i + y >= 0 && j + x >= 0) {
                        gameBoard[i + y][j + x] = boardFromFile[i][j];
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
    public boolean getCellState(int y, int x) {
        return gameBoard[y][x] >= 64;
    }

    @Override
    public boolean getCellState(double x, double y) {
        y = y / (cellSize + gridSpacing);
        x = x / (cellSize + gridSpacing);

        if (((int) y) < gameBoard.length && y >= 0) {
            if (((int) x) < gameBoard[(int) y].length && x >= 0) {
                return gameBoard[(int) y][(int) x] == 64;
            }
        }
        System.err.println("x and y was not in gameboard.");
        return false;
    }

    @Override
    public byte[][] getGameBoard() {
        return gameBoard;
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
}
