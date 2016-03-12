package gol.model.Logic;

/**
 * Super to {@link gol.model.Logic.ConwaysRule} and (CustomRule). All classes
 * that implements this rule will have to override the setLife method.
 * @author s305054, s305084, s305089
 */
public interface Rule {

    /**
     * Assigns the value a cell will have the next generation.
     * 
     * @param cellToCheck cell that is being edited
     * @return the correct value of cell that is being checked
     * @see gol.model.Board.Board#nextGen() 
     */
    byte setLife(byte cellToCheck);

}
