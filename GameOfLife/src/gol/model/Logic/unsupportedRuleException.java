package gol.model.Logic;

/**
 * When trying to make an illegal custom rule, this exception is thrown.
 * @author s305054, s305084, s305089
 */
public class unsupportedRuleException extends Exception {
    
    /**
     * Constructs a new exception with null as its detail message.
     */
    public unsupportedRuleException() {
        super();
    }

    /**
     * Constructs a new exception with a specified message.
     * @param message the detail message.
     */
    public unsupportedRuleException(String message) {
        super(message);
    }
    
}
