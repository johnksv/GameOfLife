package gol.model.Board;

import gol.model.Logic.Rule;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author s305054, s305089, s305084
 */
public class DynamicBoard extends Board {

    private final int MAXWIDTH = 2000;
    private final int MAXHEIGHT = 2000;

    private CopyOnWriteArrayList<CopyOnWriteArrayList<Byte>> gameBoard;

    public DynamicBoard() {
        super();
        gameBoard = new CopyOnWriteArrayList<>();
    }

    public DynamicBoard(int y, int x) {
        this();
        expandBoard(y, x);
    }

    @Override
    public void clearBoard() {
        gameBoard.clear();
    }

    @Override
    protected void countNeigh() {
        for (int i = 1; i < gameBoard.size(); i++) {
            for (int j = 1; j < gameBoard.get(i).size(); j++) {

                //If cell is alive
                if (gameBoard.get(i).get(j) >= 64) {

                    //Goes through surrounding neighbours
                    for (int k = -1; k <= 1; k++) {
                        for (int l = -1; l <= 1; l++) {

                            //To not count itself
                            if (!(k == 0 && l == 0)) {
                                incrementCellValue(i + k, j + l);

                                i = (i + k < 1) ? i + 1 : i;
                                j = (j + l < 1) ? j + 1 : j;

                            }
                        }
                    }
                }

            }
        }
    }

    @Override
    protected void checkRules(Rule activeRule) {
        for (int i = 1; i < gameBoard.size(); i++) {
            for (int j = 1; j < gameBoard.get(i).size(); j++) {
                if (gameBoard.get(i).get(j) != 0) {
                    if (activeRule.setLife(gameBoard.get(i).get(j)) == 64) {
                        setCellState(i, j, true);
                    } else {
                        setCellState(i, j, false);
                    }

                }
            }
        }
    }

    @Override
    public void insertArray(byte[][] boardToInsert, int y, int x) {
        if (x < 0 || y < 0) {
            expandBoard(y - 1, x - 1);
            y = (y < 1) ? 1 : y;
            x = (x < 1) ? 1 : x;
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

        //All zero or less cordinates will be set to 1.
        //This is a fundamental part of DynamicBoard.
        y = (y < 1) ? 1 : y;
        x = (x < 1) ? 1 : x;

        if (alive) {
            gameBoard.get(y).set(x, (byte) 64);
        } else {
            gameBoard.get(y).set(x, (byte) 0);
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
        if (y < 0 || y > gameBoard.size()) {
            return false;
        }
        if (x < 0 || x > gameBoard.get(y).size()) {
            return false;
        }
        return gameBoard.get(y).get(x) >= 64;
    }

    @Override
    public byte[][] getBoundingBoxBoard() {
        int[] boundingBox = getBoundingBox();
        if ((boundingBox[1] - boundingBox[0] + 1) > 0 && (boundingBox[3] - boundingBox[2] + 1) > 0) {
            byte[][] board = new byte[boundingBox[1] - boundingBox[0] + 1][boundingBox[3] - boundingBox[2] + 1];
            for (int y = 0; y < board.length; y++) {
                for (int x = 0; x < board[y].length; x++) {
                    if (getArrayLength(boundingBox[0] + y) > x + boundingBox[2]) {
                        if (gameBoard.get(boundingBox[0] + y).get(x + boundingBox[2]) == 64) {
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
                if (gameBoard.get(i).get(j) != 64) {
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
    public String toString() {
        StringBuilder result = new StringBuilder();
        gameBoard.stream().forEach((row) -> {
            row.stream().forEach((cell) -> {
                if (cell >= 64) {
                    result.append(1);
                } else {
                    result.append(0);
                }
            });
        });
        return result.toString();
    }

    private void incrementCellValue(int y, int x) {
        expandBoard(y, x);

        //All zero or less cordinates will be set to 1.
        //This is a fundamental part of DynamicBoard.
        y = (y < 1) ? 1 : y;
        x = (x < 1) ? 1 : x;

        byte value = gameBoard.get(y).get(x);
        gameBoard.get(y).set(x, ++value);
    }

    private void expandBoard(int y, int x) {
        if (gameBoard.size() < MAXHEIGHT) {
            while (y < 1) {
                gameBoard.add(0, new CopyOnWriteArrayList<>());
                y++;
            }
            while (y >= gameBoard.size()) {
                gameBoard.add(new CopyOnWriteArrayList<>());
            }
        }
        if (getMaxRowLength() < MAXWIDTH) {
            while (x < 1) {
                for (CopyOnWriteArrayList<Byte> row : gameBoard) {
                    row.add(0, (byte) 0);
                }
                x++;
            }

            while (x >= gameBoard.get(y).size()) {
                gameBoard.get(y).add((byte) 0);
            }
        }

    }

    @Override
    public int getMaxRowLength() {
        int max = 0;
        for (CopyOnWriteArrayList<Byte> row : gameBoard) {
            if (max < row.size()) {
                max = row.size();
            }
        }

        return max;
    }
}
