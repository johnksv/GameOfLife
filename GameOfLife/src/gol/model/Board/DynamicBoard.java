package gol.model.Board;

import gol.model.Logic.Rule;
import gol.model.ThreadPool;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author s305054, s305089, s305084
 */
public class DynamicBoard extends Board {

    private final int MAXWIDTH = 2000;
    private final int MAXHEIGHT = 2000;
    /**
     * The amount of cells the board will expand during expansion.
     */
    public final int EXPANSION = 50;

    private ArrayList<ArrayList<AtomicInteger>> gameBoard;

    public DynamicBoard() {
        super();
        gameBoard = new ArrayList<>();
    }

    public DynamicBoard(int y, int x) {
        this();
        expandBoard(-y, -x);
    }

    @Override
    public void clearBoard() {
        gameBoard.clear();
    }

    @Override
    protected void countNeigh() {

        for (int row = 1; row < gameBoard.size(); row++) {
            for (int col = 1; col < gameBoard.get(row).size(); col++) {

                //If cell is alive
                if (gameBoard.get(row).get(col).intValue() >= 64) {

                    //Goes through surrounding neighbours
                    for (int k = -1; k <= 1; k++) {
                        for (int l = -1; l <= 1; l++) {

                            //To not count itself
                            if (!(k == 0 && l == 0)) {
                                incrementCellValue(row + k, col + l);

                                row = (row + k < 1) ? row + 1 : row;
                                col = (col + l < 1) ? col + 1 : col;

                            }
                        }
                    }
                }

            }
        }
    }

    @Override
    protected void countNeighConcurrent(int thread) {

        int linesPerThread = gameBoard.size() / ThreadPool.THREAD_NR;
        int rest = gameBoard.size() % ThreadPool.THREAD_NR;
        int startRow = (linesPerThread * thread) + rest;

        int endRow = linesPerThread * (thread + 1) + rest;

        threadPool.addWork(() -> {
            for (int row = startRow; row < endRow; row++) {
                for (int col = 1; col < gameBoard.get(row).size(); col++) {

                    //If cell is alive
                    if (gameBoard.get(row).get(col).intValue() >= 64) {

                        //Goes through surrounding neighbours
                        for (int k = -1; k <= 1; k++) {
                            for (int l = -1; l <= 1; l++) {

                                //To not count itself
                                if (!(k == 0 && l == 0)) {
                                    //Will not expand top or left
                                    //Important: will expand bottom and rigth
                                    incrementCellValueNE(row + k, col + l);

                                    row = (row + k < 1) ? row + 1 : row;
                                    col = (col + l < 1) ? col + 1 : col;

                                }
                            }
                        }
                    }

                }
            }
        });
    }

    @Override
    protected void checkRules(Rule activeRule) {
        for (int row = 1; row < gameBoard.size(); row++) {
            for (int col = 1; col < gameBoard.get(row).size(); col++) {
                if (gameBoard.get(row).get(col).intValue() != 0) {
                    if (activeRule.setLife(gameBoard.get(row).get(col).byteValue()) == 64) {
                        setCellState(row, col, true);
                    } else {
                        setCellState(row, col, false);
                    }

                }
            }
        }
    }

    @Override
    protected void checkRulesConcurrent(Rule activeRule, int thread) {
        int linesPerThread = gameBoard.size() / ThreadPool.THREAD_NR;
        int rest = gameBoard.size() % ThreadPool.THREAD_NR;
        int startRow = (linesPerThread * thread) + rest;
        int endRow = linesPerThread * (thread + 1) + rest;

        threadPool.addWork(() -> {
            for (int row = startRow; row < endRow; row++) {
                for (int col = 1; col < gameBoard.get(row).size(); col++) {
                    if (gameBoard.get(row).get(col).intValue() != 0) {
                        if (activeRule.setLife(gameBoard.get(row).get(col).byteValue()) == 64) {
                            //Will not expand top or left sides
                            setCellStateNE(row, col, true);

                            //Will force expansion next gen if near border.
                            if (row < 3) {
                                EXPAND_X.set(true);
                            }
                            if (col < 3) {
                                EXPAND_Y.set(true);
                            }
                        } else {
                            setCellStateNE(row, col, false);
                        }

                    }
                }
            }

        });
    }

    @Override
    public void insertArray(byte[][] boardToInsert, int y, int x) {
        //Expand if close to border. This has to do with NextGen
        if (x < 2 || y < 2) {
            expandBoard(y - 1, x - 1);
            y = (y < 2) ? EXPANSION + 1 : y;
            x = (x < 2) ? EXPANSION + 1 : x;
        }
        
        for (int i = 0; i < boardToInsert.length; i++) {
            for (int j = 0; j < boardToInsert[i].length; j++) {
                if (boardToInsert[i][j] == 64) {

                    setCellState(y + i, x + j, true);

                }
            }
        }
    }

    @Override
    public void setCellState(int y, int x, boolean alive) {

        expandBoard(y, x);

        //All zero or less cordinates will be set to the corresponding value after expanding.
        //This is a fundamental part of DynamicBoard.
        y = (y < 2) ? EXPANSION : y;
        x = (x < 2) ? EXPANSION : x;

        if (alive) {
            gameBoard.get(y).set(x, new AtomicInteger(64));
        } else {
            gameBoard.get(y).set(x, new AtomicInteger(0));
        }
    }

    @Override
    public void setCellState(double y, double x, boolean alive, double offsetX, double offsetY) {
        y = y / (cellSize + gridSpacing);
        x = x / (cellSize + gridSpacing);
        offsetY = offsetY / (cellSize + gridSpacing);
        offsetX = offsetX / (cellSize + gridSpacing);

        setCellState((int) Math.floor(y - offsetY), (int) Math.floor(x - offsetX), alive);

    }

    @Override
    public int getArrayLength() {
        return gameBoard.size();
    }

    @Override
    public int getArrayLength(int i) {
        return gameBoard.get(i).size();
    }

    @Override
    public boolean getCellState(int y, int x) {
        if (y <= 0 || y >= gameBoard.size()) {
            return false;
        }
        if (x <= 0 || x >= gameBoard.get(y).size()) {
            return false;
        }
        return gameBoard.get(y).get(x).intValue() >= 64;
    }

    @Override
    public byte[][] getBoundingBoxBoard() {
        int[] boundingBox = getBoundingBox();
        if ((boundingBox[1] - boundingBox[0] + 1) > 0 && (boundingBox[3] - boundingBox[2] + 1) > 0) {
            byte[][] board = new byte[boundingBox[1] - boundingBox[0] + 1][boundingBox[3] - boundingBox[2] + 1];
            for (int y = 0; y < board.length; y++) {
                for (int x = 0; x < board[y].length; x++) {
                    if (getArrayLength(boundingBox[0] + y) > x + boundingBox[2]) {
                        if (gameBoard.get(boundingBox[0] + y).get(boundingBox[2] + x).intValue() == 64) {
                            board[y][x] = 64;
                        } else {
                            board[y][x] = 0;
                        }
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
        boundingBox[0] = gameBoard.size();
        boundingBox[1] = 0;
        boundingBox[2] = Integer.MAX_VALUE;
        boundingBox[3] = 0;
        for (int i = 0; i < gameBoard.size(); i++) {
            for (int j = 0; j < gameBoard.get(i).size(); j++) {
                if (gameBoard.get(i).get(j).intValue() != 64) {
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
    public int getMaxRowLength() {
        int max = 0;
        for (ArrayList<AtomicInteger> row : gameBoard) {
            if (max < row.size()) {
                max = row.size();
            }
        }
        return max;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        byte[][] board = getBoundingBoxBoard();
        for (byte[] row : board) {
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

    private void incrementCellValue(int y, int x) {
        expandBoard(y, x);

        //All zero or less cordinates will be set to 1.
        //This is a fundamental part of DynamicBoard.
        y = (y < 2) ? EXPANSION : y;
        x = (x < 2) ? EXPANSION : x;
        //TODO CHECK
        gameBoard.get(y).get(x).incrementAndGet();

    }

    @Override
    protected void expandBoard(int y, int x) {

        if (y < 2) {
            while (y < EXPANSION) {
                gameBoard.add(0, new ArrayList<>());
                offsetValues[1] -= (cellSize + gridSpacing);
                y++;
            }
        }
        while (y >= gameBoard.size() - EXPANSION) {
            gameBoard.add(new ArrayList<>());
        }
        if (x < 2) {
            while (x < EXPANSION) {
                for (ArrayList<AtomicInteger> row : gameBoard) {
                    row.add(0, new AtomicInteger(0));
                }
                offsetValues[0] -= (cellSize + gridSpacing);
                x++;
            }
        }

        while (x >= gameBoard.get(y).size() - EXPANSION) {
            gameBoard.get(y).add(new AtomicInteger(0));
        }

    }

    private void setCellStateNE(int row, int col, boolean alive) {
        if (alive) {
            gameBoard.get(row).set(col, new AtomicInteger(64));
        } else {
            gameBoard.get(row).set(col, new AtomicInteger(0));
        }
    }

    private void incrementCellValueNE(int y, int x) {
        if (y < 0) {
            return;
        }
        if (x < 0) {
            return;
        }
        while (y >= gameBoard.size() - EXPANSION) {
            gameBoard.add(new ArrayList<>());
        }
        while (x >= gameBoard.get(y).size() - EXPANSION) {
            gameBoard.get(y).add(new AtomicInteger(0));
        }
        gameBoard.get(y).get(x).incrementAndGet();

    }
}
