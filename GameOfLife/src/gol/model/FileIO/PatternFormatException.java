package gol.model.FileIO;

/**
 * The <code>PatternFormatException</code> is thrown if an imported pattern is
 * wrong formated.
 *
 * @author s305054, s305084, s305089
 */
public class PatternFormatException extends Exception {

    /**
     * Signals that an error has occurred while reading a pattern.
     */
    public PatternFormatException() {
        super();
    }

    /**
     * Signals that an error has occurred while reading a pattern.
     *
     * @param message the error message
     */
    public PatternFormatException(String message) {
        super(message);
    }
}
