package gol.model.Board;

import java.util.HashMap;

/**
 * <p>
 * HashBoard is a working next generation with hashLife, but all storing of
 * cells is not. HashBoard extends ArrayBoard, since the storing method is yet to be made.
 * </p>
 * <b>To do:</b> implement a quadtree to store all cells, this will make the board dynamic and much much faster.
 * If a quadtree is implemented a new getCellState and setCellState must be implemented.
 *
 * @author s305084 - Stian h. Stensli
 */
public class HashBoard extends ArrayBoard {

    private final int WIDTH;
    private final int HEIGHT;
    private final int maxK = 8;

    private byte[][] nextBoard;
    private final HashMap<Number, byte[]> hash = new HashMap<>();
    //can be save as short, but easy with int as java returns int for all bit operations
    private int macroCell;
 
    /**
     * Creates a new hashBoard, that is now temporary set to a max width of 2^8. 
     * Will be dynamic when finished.
     */
    public HashBoard() {
        super((int) Math.pow(2, 8), (int) Math.pow(2, 8));
        this.WIDTH = (int) Math.pow(2, maxK);
        this.HEIGHT = (int) Math.pow(2, maxK);

    }
    /**
     * Calls the private method evolve which will use an implementation of hashlife to calculate the next generation.
     * Storing method not completed and the next generation will work directly on an array.
     */
    @Override
    public void nextGen() {
        nextBoard = new byte[HEIGHT][WIDTH];
        evolve(0, 0, maxK);
        gameBoard = nextBoard;
    }
    /**
     * <p>
     * Calls the private method evolve which will use an implementation of hashlife to calculate the next generation.
     * Storing method not completed and the next generation will work directly on an array.
     * </p>
     * <b>Note:</b> this version of hashlife does not support more than one thread. 
     */
    @Override
    public void nextGenConcurrent() {
        nextGen();
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
                //Normal nextgen
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
    
    /**
     * Gets coordinates to the top left corner of a macrocell.
     * Returns a 2*2 with the input coordinates a its top left corner
     */
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
}
