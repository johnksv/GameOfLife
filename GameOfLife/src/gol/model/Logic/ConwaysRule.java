package gol.model.Logic;

/**
 * @author s305054, s305084, s305089
 */
public class ConwaysRule implements Rule {

    private boolean toLive(byte cellToCheck) {
        return cellToCheck == 66 || cellToCheck == 67;
    }

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
