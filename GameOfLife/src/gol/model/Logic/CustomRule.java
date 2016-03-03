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
            return cellToCheck == value + 64;
        }
        return false;
    }

    private boolean toSpawn(byte cellToCheck) {
        for (byte value : cellsToSpawn) {
            return cellToCheck == value + 64;
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
