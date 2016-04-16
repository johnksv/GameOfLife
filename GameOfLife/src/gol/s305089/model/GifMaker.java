package gol.s305089.model;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
import java.awt.Color;
import java.io.IOException;
import lieng.GIFWriter;

/**
 * @author s305089 Created: 18.03.2016 Last edited: 20.03.2016
 */
public final class GifMaker {

    private Board gameboard;
    private byte[][] pattern;

    private GIFWriter gifWriter;
    private int gifWidth = 100;
    private int gifHeight = 100;
    private String saveLocation;
    private int durationBetweenFrames = 1000;
    private int cellSize = 10;
    private boolean centerPattern = false;

    public GifMaker() throws IOException {
    }

    public void writePatternToGIF(int iterations) throws IOException {
        gifWriter = new GIFWriter(gifWidth, gifHeight, saveLocation, durationBetweenFrames);
        if (gameboard != null || pattern != null) {
            //If not called, manipulations is done on current gen of activeBoard.
            setPattern(pattern);
            writeGIF(iterations);
        } else {
            System.err.println("Pattern is not set. Be sure that setPattern() is called first");
        }
    }

    /**
     * //TODO Svar på spørmål om halerekursjon
     * Forstå hva halerekursjon (eng: tail recursion) er og fordelen er med slik
     * rekursjon. Test om metoden dere har implementert over utfører slik
     * halerekursjon. Ut ifra denne testen, diskuter nå fordeler/ulemper med
     * rekursjon for dette problemet. Til slutt, bruk Internett til å finne ut
     * om Java/JVM støtter halerekursjon og eventuelt hvordan.
     */
    private void writeGIF(int iterations) throws IOException {
        if (iterations > 0) {
            iterations--;

            for (int y = 0; y < gameboard.getArrayLength(); y++) {
                for (int x = 0; x < gameboard.getArrayLength(y); x++) {
                    if (gameboard.getCellState(x, y)) {
                        gifWriter.fillRect(y * cellSize, y * cellSize + cellSize, x * cellSize, x * cellSize + cellSize, Color.BLACK);
                    }
                }
            }
            gifWriter.insertAndProceed();
            if (centerPattern) {
                centerGameBoard();
            }
            gameboard.nextGen();
            writeGIF(iterations);
        } else {
            gifWriter.close();
        }
    }

    private void centerGameBoard() {
        //TODO centerGameboard
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @param CellSize the cellSize to set
     */
    public void setCellSize(int CellSize) {
        this.cellSize = CellSize;
    }

    /**
     * @param CenterPreview the centerPattern to set
     */
    public void setCenterPreview(boolean CenterPreview) {
        this.centerPattern = CenterPreview;
    }

    /**
     * @param DurationBetweenFrames the durationBetweenFrames to set
     */
    public void setDurationBetweenFrames(int DurationBetweenFrames) {
        this.durationBetweenFrames = DurationBetweenFrames;
    }

    /**
     * @param GifWidth the gifWidth to set
     */
    public void setGifWidth(int GifWidth) {
        this.gifWidth = GifWidth;
    }

    /**
     * @param GifHeight the gifHeight to set
     */
    public void setGifHeight(int GifHeight) {
        this.gifHeight = GifHeight;
    }

    /**
     * @param SaveLocation the saveLocation to set
     */
    public void setSaveLocation(String SaveLocation) {
        this.saveLocation = SaveLocation;
    }

    /**
     * Constructs an new Board instance, and inserts this board
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
