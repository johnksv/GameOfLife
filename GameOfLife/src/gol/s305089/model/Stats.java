package gol.s305089.model;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;

/**
 * @author s305089
 */
public class Stats {

    private Board gameboard;
    private byte[][] pattern;

    public int[][] getStatistics(int iterations){
        
        
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

}
