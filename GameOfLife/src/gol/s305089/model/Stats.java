package gol.s305089.model;

import com.sun.deploy.util.StringUtils;
import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;

/**
 * @author s305089
 */
public class Stats {

    private Board gameboard;
    private byte[][] pattern;

    public int[][] getStatistics(int iterations) {

        for (int time = 0; time <= iterations; time++) {
            for (int j = 0; j < 3; j++) {
                countLiving(time);
                changeInLiving(time);
                similarityMeasure();
            }
            gameboard.nextGen();
        }

        return null;
    }

    /**
     * Consturcts an new Board instance, and inserts this board
     *
     * @see gol.model.Board.Board#insertArray(byte[][], int, int)
     * @param Pattern the pattern to set
     */
    public void setPattern(byte[][] Pattern) {
        //TODO dynaimc size of board
        gameboard = new ArrayBoard(10, 10);
        this.pattern = Pattern;
        gameboard.insertArray(pattern, 2, 2);
    }

    private int countLiving(int time) {
        int countOfLiving = 0;
        char[] cell = gameboard.toString().toCharArray();
        for(char value : cell){
            if(value == '1'){
                countOfLiving++;
            }
        }
        return countOfLiving;
    }

    private void changeInLiving(int time) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
