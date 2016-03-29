package gol.s305084;

import gol.model.Board.Board;
import gol.model.Logic.*;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;

/**
 *
 * @author Stian
 */
public class HashLife {

    private static Board activeBoard;
    //private static byte[][] activeBoard;
    private static byte[][] nextBoard;
    private static Hashtable<String, byte[]> hash = new Hashtable<>();
    private static StringBuilder macroCell = new StringBuilder();
    private static int startX;
    private static int startY;

    public static void loadeBoard(Board otherBoard) {
        activeBoard = otherBoard;
    }

    //a macrocell is the size 2^n + 2  
    public static void preEvolve() {
        if (activeBoard != null) {
            int[] boundingBox = activeBoard.getBoundingBox();
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
        System.out.println("y: " + y + " x:" + x);
        if (k <= 1) {
            macroCell.delete(0, macroCell.length());

            try {
                for (int i = -1; i < 3; i++) {
                    for (int j = -1; j < 3; j++) {
                        if (activeBoard.getCellState(startY + y + i, startX + x + j)) {
                            macroCell.append('1');
                        } else {
                            macroCell.append('0');
                        }
                    }

                }

            } catch (ArrayIndexOutOfBoundsException ex) {
                System.err.println("OUT!");
            }

            System.out.println(macroCell.toString());
            if (hash.containsKey(macroCell.toString())) {
                System.out.println("Du er i hash");
                for (int i = 0; i < 4; i++) {
                    nextBoard[y + i % 2][x + (int) (i / 2)] = hash.get(macroCell.toString())[i];
                }

            } else {
                byte[] nextgen =nextgen(y, x);
                hash.put(macroCell.toString(), nextgen);
                for (int i = 0; i < 4; i++) {
                    nextBoard[y + i % 2][x + (int) (i / 2)] = nextgen[i];
                }
            }
            return;

        }
        k--;
        System.out.println(k);
        evolve(y, x, k); //Topp Left
        evolve(y, x + (int) Math.pow(2, k), k); //Topp Rigth
        evolve(y + (int) Math.pow(2, k), x, k); //Bottom Left
        evolve(y + (int) Math.pow(2, k), x + (int) Math.pow(2, k), k); //Bottom Rigth

        return;
    }

    private static byte[] nextgen(int y, int x) {
        byte[] nexGen = new byte[4];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                int counter = 0;

                for (int l = -1; l <= 1; l++) {
                    for (int k = -1; k <= 1; k++) {
                        if (activeBoard.getCellState(startY + y+ i + l, startX + x +j+ k)) {
                            counter++;
                        }
                    }
                }
                System.out.println(counter);
                if (counter == 3 && !activeBoard.getCellState(startY + y + i, startX + x + j)) {

                    nexGen[i * 2 + j] = 64;
                } else if ((counter == 2 + 1 || counter == 3 + 1) && activeBoard.getCellState(startY + y + i, startX + x + j)) {

                    nexGen[i * 2 + j] = 64;
                } else {
                    nexGen[i * 2 + j] = 0;
                }
            }
        }
        System.out.println(Arrays.toString(nexGen));
        return nexGen;
    }
}
