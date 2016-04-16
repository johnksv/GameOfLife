package gol.s305089.model;

import gol.model.Board.Board;
import gol.model.Board.DynamicBoard;
import java.awt.Color;
import java.io.IOException;
import lieng.GIFWriter;

/**
 * @author s305089 Created: 18.03.2016 Last edited: 20.03.2016
 */
public final class GifMaker {

    private Board activeBoard;
    private byte[][] pattern;

    private GIFWriter gifWriter;
    private int gifWidth = 200;
    private int gifHeight = 200;
    private String saveLocation;
    private int durationBetweenFrames = 1000;
    private double[] moveGridValues;
    private double cellSize;

    public GifMaker() throws IOException {
    }

    public void writePatternToGIF(int iterations) throws IOException {
        gifWriter = new GIFWriter(gifWidth, gifHeight, saveLocation, durationBetweenFrames);
        if (activeBoard != null || pattern != null) {
            //If not called, manipulations is done on the current gen of this activeBoard.
            //Makes an new board with the "start" pattern.
            setPattern(pattern);
            writeGIF(iterations);
        } else {
            System.err.println("Pattern is not set. Be sure that setPattern() is called first");
        }
    }

    /**
     * //TODO Svar på spørmål om halerekursjon Forstå hva halerekursjon (eng:
     * tail recursion) er og fordelen er med slik rekursjon. Test om metoden
     * dere har implementert over utfører slik halerekursjon. Ut ifra denne
     * testen, diskuter nå fordeler/ulemper med rekursjon for dette problemet.
     * Til slutt, bruk Internett til å finne ut om Java/JVM støtter
     * halerekursjon og eventuelt hvordan.
     */
    private void writeGIF(int iterations) throws IOException {
        if (iterations > 0) {
            iterations--;

            
            //TODO Center board on center
            for (int y = 1; y < activeBoard.getArrayLength(); y++) {
                for (int x = 1; x < activeBoard.getArrayLength(y); x++) {
                    if (activeBoard.getCellState(y, x)) {

                        int x1 = (int) (x * cellSize + moveGridValues[0]);
                        int x2 = (int) (x * cellSize + cellSize + moveGridValues[0]);
                        int y1 = (int) (y * cellSize + moveGridValues[1]);
                        int y2 = (int) (y * cellSize + cellSize + moveGridValues[1]);

                        if (x1 >= 0 && x2 >= 0 && y1 >= 0 && y2 >= 0) {
                            if (x1 < gifWidth && x2 < gifWidth && y1 < gifHeight && y2 < gifHeight) {
                                gifWriter.fillRect(x1, x2, y1, y2, Color.BLACK);
                            }
                        }
                    }
                }
            }
            gifWriter.insertAndProceed();
            activeBoard.nextGen();
            writeGIF(iterations);
        } else {
            gifWriter.close();
        }
    }

    /**
     * @param cellSize the cellSize to set
     */
    public void setCellSize(double cellSize) {
        this.cellSize = cellSize;
    }

    /**
     * @param durationBetweenFrames the durationBetweenFrames to set
     */
    public void setDurationBetweenFrames(int durationBetweenFrames) {
        this.durationBetweenFrames = durationBetweenFrames;
    }

    /**
     * @param gifWidth the gifWidth to set
     */
    public void setGifWidth(int gifWidth) {
        this.gifWidth = gifWidth;
    }

    /**
     * @param gifHeight the gifHeight to set
     */
    public void setGifHeight(int gifHeight) {
        this.gifHeight = gifHeight;
    }

    /**
     * @param SaveLocation the saveLocation to set
     */
    public void setSaveLocation(String SaveLocation) {
        this.saveLocation = SaveLocation;
    }

    /**
     * Constructs an new Board instance, and inserts this board
     *
     * @see gol.model.Board.Board#insertArray(byte[][], int, int)
     * @param patternToSet the pattern to set
     */
    public void setPattern(byte[][] patternToSet) {
        activeBoard = new DynamicBoard(10, 10);
        moveGridValues = activeBoard.getMoveGridValues();
        this.pattern = patternToSet;
        activeBoard.insertArray(patternToSet, 2, 2);
        activeBoard.setCellSize(cellSize);
    }
}
