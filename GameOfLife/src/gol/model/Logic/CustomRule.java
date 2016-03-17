package gol.model.Logic;

/**
 * @author s305054, s305084, s305089
 */
public class CustomRule implements Rule {

    private byte[] toSpawn, toLive;

    public CustomRule(byte[] toLive, byte[] toSpawn) {
        this.toLive = toLive;
        this.toSpawn = toSpawn;
    }

    /**
     * Determines whether a living will survive the next generation or not.
     *
     *
     * @param cellToCheck cell that is being checked for living neighbors
     * @return true if living neighbors is two or three, else false
     */
    private boolean toLive(byte cellToCheck) {
        for (byte i : toLive) {
            if (i + 64 == cellToCheck) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a dead cell will be spawned the next generation.
     *
     * @param cellToCheck cell that is being checked for living neighbors
     * @return true if there are exactly three living neighbors
     */
    private boolean toSpawn(byte cellToCheck) {
        for (byte i : toSpawn) {
            if (i == cellToCheck) {
                return true;
            }
        }
        return false;
    }

    @Override
    public byte setLife(byte cellToCheck) {
        if (toLive(cellToCheck) || toSpawn(cellToCheck)) {
            return 64;
        }
        return 0;
    }

}
