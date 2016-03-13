package gol.model.Logic;

/**
 * All classes that implements this interface have to override the setLife method.
 * @author s305054, s305084, s305089
 */
public interface Rule {

    /**
     * Assigns the value a cell will have the next generation.
     * 
     * @param cellToCheck cell that is being edited
     * @return 64 for alive cells, 0 for dead cells
     * @see gol.model.Board.Board#nextGen() 
     */
    byte setLife(byte cellToCheck);

}
