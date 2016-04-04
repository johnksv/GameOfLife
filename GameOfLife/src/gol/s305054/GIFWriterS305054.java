package gol.s305054;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
import lieng.GIFWriter;

/**
 * This class is used to create a .gif file showing several generations of a GoL board.
 * @author Trygve Vang - s305054
 */
public class GIFWriterS305054 {
    private Board copiedBoard;
    
    int width = 100; //Width of .gif - Hardcoded value will be changed
    int height = 100; //Height of .gif - Hardcoded value will be changed
    int time = 1000; // 1000 ms = 1s
    
    GIFWriter gifWriter;
    
}
