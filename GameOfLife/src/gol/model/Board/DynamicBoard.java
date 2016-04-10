package gol.model.Board;

import gol.model.Logic.Rule;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author s305054, s305089, s305084
 */
public class DynamicBoard extends Board {

    private final CopyOnWriteArrayList<CopyOnWriteArrayList<Byte>> gameBoard;

    public DynamicBoard() {
        super();
        gameBoard = new CopyOnWriteArrayList<>();
    }

    @Override
    public void clearBoard() {
        gameBoard.clear();
    }

    @Override
    protected void countNeigh() {
        for (int i = 0; i < gameBoard.size(); i++) {
            for (int j = 0; j < gameBoard.get(i).size(); j++) {

                //If cell is alive
                if (gameBoard.get(i).get(j) >= 64) {

                    //Goes through surrounding neighbours
                    for (int k = -1; k <= 1; k++) {
                        for (int l = -1; l <= 1; l++) {

                            //To not count itself
                            if (!(k == 0 && l == 0)) {
                                incrementCellValue(i + k, j + l);
                            }
                        }
                    }
                }

            }
        }
    }

    @Override
    protected void checkRules(Rule activeRule) {
        for (int i = 0; i < gameBoard.size(); i++) {
            for (int j = 0; j < gameBoard.get(i).size(); j++) {
                if (gameBoard.get(i).get(j) != 0) {
                    gameBoard.get(i).set(j, activeRule.setLife(gameBoard.get(i).get(j)));
                }
            }
        }
    }

    @Override
    public void insertArray(byte[][] boardToInsert, int y, int x) {

        
        for (int i = 0; i < boardToInsert.length; i++) {
            for (int j = 0; j < boardToInsert[i].length; j++) {
                if (j + x < gameBoard.get(y + i).size()) {
                    if (i + y >= 1 && j + x >= 1) {
                        gameBoard.get(i + y).set(j + x, boardToInsert[i][j]);
                    }
                } else {
                    gameBoard.get(i + y).add(j + x, boardToInsert[i][j]);
                }
            }
        }
    }

    @Override
    public void setCellState(int y, int x, boolean alive) {

        expandBoard(y, x);

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
        if ((boundingBox[1] - boundingBox[0] + 1) > 0 || (boundingBox[3] - boundingBox[2] + 1) > 0) {
            byte[][] board = new byte[boundingBox[1] - boundingBox[0] + 1][boundingBox[3] - boundingBox[2] + 1];

            for (int y = 0; y < board.length; y++) {
                for (int x = 0; x < board[y].length; x++) {
                    if (gameBoard.get(boundingBox[0] + y).get(x + boundingBox[2]) == 64) {
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
        boundingBox[0] = gameBoard.size();
        boundingBox[1] = 0;
        boundingBox[2] = Integer.MAX_VALUE;
        boundingBox[3] = 0;
        for (int i = 0; i < gameBoard.size(); i++) {
            boundingBox[2] = gameBoard.get(i).size();
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
        if (boundingBox[2] == Integer.MAX_VALUE) {
            boundingBox[2] = 0;
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
        byte value = gameBoard.get(y).get(x);
        gameBoard.get(y).set(x, ++value);
    }

    private void expandBoard(int y, int x) {
        while (y < 1) {
            gameBoard.add(0, new CopyOnWriteArrayList<>());
            y++;
        }
        while (x < 1) {
            for (CopyOnWriteArrayList<Byte> row : gameBoard) {
                row.add(0, (byte) 0);
            }
            x++;
        }
        while (y >= gameBoard.size()) {
            gameBoard.add(new CopyOnWriteArrayList<>());
        }
        while (x >= gameBoard.get(y).size()) {
            gameBoard.get(y).add((byte) 0);
        }
    }
}
