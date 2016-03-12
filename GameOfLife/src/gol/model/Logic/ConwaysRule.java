package gol.model.Logic;

/**
 * This class implements all the logic behind Conways rules of Game of Life.
 * 
 * @author s305054, s305084, s305089
 */
public class ConwaysRule implements Rule {
    
    /**
     * Determines whether a living will survive the next generation or not.
     * 
     * 
     * @param cellToCheck cell that is being checked for living neighbors
     * @return true if living neighbors is two or three, else false
     */
    private boolean toLive(byte cellToCheck) {
        return cellToCheck == 66 || cellToCheck == 67;
    }

    /**
     * Check if a dead cell will be spawned the next generation.
     * 
     * @param cellToCheck cell that is being checked for living neighbors
     * @return true if there are exactly three living neighbors
     */
    private boolean toSpawn(byte cellToCheck) {
        return cellToCheck == 3;
    }

    @Override
    public byte setLife(byte cellToCheck) {
        if (toLive(cellToCheck) || toSpawn(cellToCheck)) {
            return 64;
        }
        return 0;
    }

}
