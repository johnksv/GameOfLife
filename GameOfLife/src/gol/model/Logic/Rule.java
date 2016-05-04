package gol.model.Logic;

/**
 * All classes that implements this interface can set a specific set of rules.
 *
 * @author s305054, s305084, s305089
 */
public interface Rule {

    /**
     * Assigns the value a cell will have the next generation.
     *
     * @param cellToCheck cell that is being modified
     * @return 64 for alive cells, 0 for dead cells
     * @see gol.model.Board.Board#nextGen()
     */
    byte setLife(byte cellToCheck);

    /**
     * Returns an array of how many neighbors you need to survive.
     *
     * @return an array containing the values a cell must have to survive.
     */
    byte[] getSurvive();

    /**
     * Returns an array of how many neighbors you need to be born.
     *
     * @return an array containing the values a cell must have to be born.
     */
    byte[] getToBorn();
}
