package gol.model.Logic;

/**
 * @author John Kasper
 */
public class CustomRule implements Rule {

    private final byte[] cellsToLive, cellsToSpawn;

    public CustomRule(byte[] cellsToLive, byte[] cellsToSpawn) {
        this.cellsToLive = cellsToLive;
        this.cellsToSpawn = cellsToSpawn;
    }

    private boolean toLive(byte cellToCheck) {

        for (byte value : cellsToLive) {
            if (cellToCheck == value + 64) {
                return true;
            }
        }
        return false;
    }

    private boolean toSpawn(byte cellToCheck) {
        for (byte value : cellsToSpawn) {
            if (cellToCheck == value) {
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
