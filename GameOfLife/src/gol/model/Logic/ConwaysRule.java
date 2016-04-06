package gol.model.Logic;

/**
 * This class implements all the logic behind Conway's rule of Game of Life.
 * Conway's rule specifies the next generation with these requirement:
 * A cell survives on if it has two or three neigbhors.
 * A cell will be born if it has excatly three neigbhors.
 * Otherwise the cell will die.
 * 
 * Conway's Rule is the originale set of rules in Game of Life.
 * 
 * @author s305054, s305084, s305089
 */
public class ConwaysRule implements Rule {
    
    /**
     * Initiates a new Rule of type Conway.
     */
    public ConwaysRule(){
    }
    
    /**
     * Determines whether a living will survive the next generation or not.
     * 
     * 
     * @param cellToCheck cell that is being checked for living neighbors
     * @return true if living neighbors is two or three, else false
     */
    private boolean toSurvive(byte cellToCheck) {
        return cellToCheck == 66 || cellToCheck == 67;
    }

    /**
     * Check if a dead cell will be born the next generation.
     * 
     * @param cellToCheck cell that is being checked for living neighbors
     * @return true if there are exactly three living neighbors
     */
    private boolean toBeBorn(byte cellToCheck) {
        return cellToCheck == 3;
    }

    @Override
    public byte setLife(byte cellToCheck) {
        if (toSurvive(cellToCheck) || toBeBorn(cellToCheck)) {
            return 64;
        }
        return 0;
    }

}
