package gol.model.Board;

import gol.model.Logic.Rule;
import java.util.HashMap;

/**
 * HashBoard is a working next generation with hashLife, but all storing of cells is not.
 * HashBoard 
 * 
 * @author s305084
 */
public class HashBoard extends Board {

    private final int WIDTH, HEIGHT;
    private byte[][] gameBoard;
    private final int maxK = 8;

    private byte[][] nextBoard;
    private final HashMap<Number, byte[]> hash = new HashMap<>();
    //TODO save macrocells as short
    private int macroCell;

    public HashBoard() {
        super();
        WIDTH = (int) Math.pow(2, maxK);
        HEIGHT = (int) Math.pow(2, maxK);
        gameBoard = new byte[WIDTH][HEIGHT];
    }

    @Override
    public void nextGen() {
        nextBoard = new byte[HEIGHT][WIDTH];
        evolve(0, 0, maxK);
        gameBoard = nextBoard;
    }
    @Override
    public void nextGenConcurrent() {
        nextBoard = new byte[HEIGHT][WIDTH];
        evolve(0, 0, maxK);
        gameBoard = nextBoard;
    }

    @Override
    protected void expandBoard(int y, int x) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public void clearBoard() {
        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[i].length; j++) {
                gameBoard[i][j] = 0;
            }
        }
    }

    @Override
    protected void countNeigh() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void countNeighConcurrent(int threadNr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void checkRules(Rule activeRule) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void checkRulesConcurrent(Rule activeRule, int threadNr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        if (y < 1 || x < 1) {
            return;
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
        return gameBoard.length;
    }

    @Override
    public int getArrayLength(int i) {
        return gameBoard[i].length;
    }

    @Override
    public int getMaxRowLength() {
        return WIDTH;
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

    private byte[] nextGenMacro(int y, int x) {
        byte[] nexGenMacro = new byte[4];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                int counter = 0;

                for (int l = -1; l <= 1; l++) {
                    for (int k = -1; k <= 1; k++) {
                        if (getCellState(y + i + l, x + j + k)
                                && (l != 0 || k != 0)) {
                            counter++;
                        }
                    }
                }

                if (counter == 3 && !getCellState(y + i, x + j)) {
                    nexGenMacro[i * 2 + j] = 64;
                } else if ((counter == 2 || counter == 3) && getCellState(y + i, x + j)) {
                    nexGenMacro[i * 2 + j] = 64;
                } else {
                    nexGenMacro[i * 2 + j] = 0;
                }
            }
        }

        return nexGenMacro;
    }

    private void evolve(int y, int x, int k) {
        if (k <= 1) {

            //Creating Macrocell
            macroCell = 0;
            for (int i = -1; i < 3; i++) {
                for (int j = -1; j < 3; j++) {

                    if (getCellState(y + i, x + j)) {
                        macroCell = (++macroCell << 1);

                    } else {
                        macroCell = (macroCell << 1);

                    }
                }
            }
            //Hashing
            if (hash.containsKey(macroCell)) {
                for (int i = 0; i < 4; i++) {
                    nextBoard[y + (int) (i / 2)][x + i % 2] = hash.get(macroCell)[i];
                }

            } else {
                byte[] nextgen = nextGenMacro(y, x);
                hash.put(macroCell, nextgen);

                for (int i = 0; i < 4; i++) {
                    nextBoard[y + (int) (i / 2)][x + i % 2] = nextgen[i];
                    
                }
            }
            return;

        }
        k -= 1;
        evolve(y, x, k);//Topp Left
        evolve(y, x + (int) Math.pow(2, k), k); //Topp Rigth
        evolve(y + (int) Math.pow(2, k), x, k); //Bottom Left
        evolve(y + (int) Math.pow(2, k), x + (int) Math.pow(2, k), k); //Bottom Rigth
    }
}
