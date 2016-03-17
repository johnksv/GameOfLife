package gol.model.FileIO;

/**
 * The <code>PatternFormatException</code> is thrown if an imported pattern is
 * wrong formated.
 * 
 * @author s305054, s305084, s305089
 */
public class PatternFormatException extends Exception {

    public PatternFormatException() {
        super();
    }

    public PatternFormatException(String message) {
        super(message);
    }
}
