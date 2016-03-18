package gol.s305089;

import gol.model.Board.Board;
import lieng.GIFWriter;

/**
 * @author s305089
 */
public class BoardToGif {
    //public GIFWriter gifWriter = new GIFWriter(width, height, null, timeBetweenFramesMS);
    
    public static void writeBoardtoGIF (Board gameboard){
    
}

    public static void writeGIF(int iterations) {
        if (iterations > 0) {
            iterations--;
            
            
            writeGif(iterations);
        }
    }
}

