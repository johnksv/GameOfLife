package gol.s305084;

import gol.model.Board.Board;
import gol.model.Logic.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * This class stores all necessary methods for a simple version of the algorithm
 * HashLife
 *
 * @author s305084
 */
public final class HashLife {

    private static Board activeBoard;
    private static final ArrayList<ArrayList<Number>> nextlist = new ArrayList<>();
    private static byte[][] nextBoard = new byte[(int) Math.pow(2, 6)][(int) Math.pow(2, 6)];
    private static final Hashtable<String, byte[]> hash = new Hashtable<>();
    private static final StringBuilder macroCell = new StringBuilder();
    //TODO save macrocells as short
    private static short macroCell_;
    private static int startX = 0;
    private static int startY = 0;

    /**
     * Don't let anyone instantiate this class.
     *
     * @see Math.Math()
     */
    private HashLife() {
    }

    /**
     * Sets a board to be manipulated by this class. This needs to be done if
     * HashLife is wished for.
     *
     * @param otherBoard
     */
    public static void loadeBoard(Board otherBoard) {
        activeBoard = otherBoard;
    }

    public static void dynamicHash() {

        int k = 1;
        while (activeBoard.getArrayLength() + 1 >= Math.pow(2, k) || activeBoard.getArrayLength(0) + 1 >= Math.pow(2, k)) {
            k += 1;
        }
        while (nextlist.size() != Math.pow(2, k)) {
            if (nextlist.size() < Math.pow(2, k)) {
                nextlist.add(nextlist.size(), new ArrayList<>());
            } else {
                System.out.println("ififiu");
                nextlist.remove(nextlist.size() - 1);
            }
        }/*
        for (int i = 0; i < nextlist.size(); i++) {
            nextlist.get(i).add(64);
        }*/
        
        evolve(0, 0, k);
        activeBoard.insertArray(nextBoard, 0, 0);
        //dynamicEvolve(0, 0, k);
        //TODO Make this insert lists to
        
        //activeBoard.insertList(nextlist);

    }

    private static void dynamicEvolve(int y, int x, int k) {
        if (k <= 1) {

            //Creating Macrocell
            macroCell.delete(0, macroCell.length());
            for (int i = -1; i < 3; i++) {
                for (int j = -1; j < 3; j++) {

                    if (activeBoard.getCellState(y + i, x + j)) {
                        macroCell.append('1');
                    } else {
                        macroCell.append('0');
                    }

                }
            }
            //Hashing
            if (hash.containsKey(macroCell.toString())) {
                for (int i = 0; i < 4; i++) {
                    //nextBoard[y + (int) (i / 2)][x + i % 2] = hash.get(macroCell.toString())[i];
                    nextlist.get(y + (int) (i / 2)).add(x + i % 2, hash.get(macroCell.toString())[i]);
                }

            } else {
                byte[] nextgen = nextgen(y, x);
                hash.put(macroCell.toString(), nextgen);

                for (int i = 0; i < 4; i++) {
                    //nextBoard[y + (int) (i / 2)][x + i % 2] = nextgen[i];

                    nextlist.get(y + (int) (i / 2)).add(x + i % 2, nextgen[i]);
                }
            }
            return;

        }
        k -= 1;
        dynamicEvolve(y, x, k);//Topp Left
        dynamicEvolve(y, x + (int) Math.pow(2, k), k); //Topp Rigth
        dynamicEvolve(y + (int) Math.pow(2, k), x, k); //Bottom Left
        dynamicEvolve(y + (int) Math.pow(2, k), x + (int) Math.pow(2, k), k); //Bottom Rigth
    }

    //a macrocell is the size 2^n + 2  
    /**
     *
     */
    public static void preEvolve() {

        if (activeBoard != null) {
            int[] boundingBox = activeBoard.getBoundingBox();
            boundingBox[0]--;
            boundingBox[2]--;
            boundingBox[1]++;
            boundingBox[3]++;
            //TODO use the full array
            int k = 1;
            while (boundingBox[1] - boundingBox[0] >= Math.pow(2, k) || boundingBox[3] - boundingBox[2] >= Math.pow(2, k)) {
                k += 1;
            }

            startY = boundingBox[0];
            startX = boundingBox[2];

            nextBoard = new byte[(int) Math.pow(2, k)][(int) Math.pow(2, k)];
            evolve(0, 0, k);
            activeBoard.insertArray(nextBoard, boundingBox[0], boundingBox[2]);
        }
    }

    private static void evolve(int y, int x, int k) {

        if (k <= 1) {
            macroCell.delete(0, macroCell.length());

            if (startY + y + 2 < activeBoard.getArrayLength()
                    && startX + x + 2 < activeBoard.getArrayLength()) {

                for (int i = -1; i < 3; i++) {
                    for (int j = -1; j < 3; j++) {
                        if (activeBoard.getCellState(startY + y + i, startX + x + j)) {
                            macroCell.append('1');
                        } else {
                            macroCell.append('0');
                        }
                    }

                }
            }

            if (hash.containsKey(macroCell.toString())) {
                for (int i = 0; i < 4; i++) {
                    nextBoard[y + (int) (i / 2)][x + i % 2] = hash.get(macroCell.toString())[i];
                }

            } else {
                byte[] nextgen = nextgen(y, x);
                hash.put(macroCell.toString(), nextgen);
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
    /*
     * Helping method for hashlife, it is used to calculate non hashed macrocells.
     */

    private static byte[] nextgenDynamic(int y, int x) {
        byte[] nexGen = new byte[4];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                int counter = 0;

                for (int l = -1; l <= 1; l++) {
                    for (int k = -1; k <= 1; k++) {
                        //TODO remove this if we get dynamic board
                        if (activeBoard.getCellState(y + i + l, x + j + k)
                                && (l != 0 || k != 0)) {
                            counter++;
                        }

                    }
                }
                //System.out.println(counter);

                if (counter == 3 && !activeBoard.getCellState(y + i, x + j)) {

                    nexGen[i * 2 + j] = 64;
                } else if ((counter == 2 || counter == 3) && activeBoard.getCellState(y + i, x + j)) {

                    nexGen[i * 2 + j] = 64;
                } else {
                    nexGen[i * 2 + j] = 0;
                }
            }
        }

        System.out.println(Arrays.toString(nexGen));
        return nexGen;
    }

    private static byte[] nextgen(int y, int x) {

        byte[] nexGen = new byte[4];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                int counter = 0;
                //TODO handel outOfBounds
                for (int l = -1; l <= 1; l++) {
                    for (int k = -1; k <= 1; k++) {
                        if (activeBoard.getCellState(startY + y + i + l, startX + x + j + k)
                                && (l != 0 || k != 0)) {
                            counter++;
                        }
                    }
                }
                if (counter == 3 && !activeBoard.getCellState(startY + y + i, startX + x + j)) {

                    nexGen[i * 2 + j] = 64;
                } else if ((counter == 2 || counter == 3) && activeBoard.getCellState(startY + y + i, startX + x + j)) {

                    nexGen[i * 2 + j] = 64;
                } else {
                    nexGen[i * 2 + j] = 0;
                }
            }
        }
        return nexGen;
    }
}
