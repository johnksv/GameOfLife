package gol.model.Logic;

/**
 * @author s305054, s305084, s305089
 */
public class CustomRule implements Rule {

    private byte[] toBeBorn;
    private final byte[] toSurvive;

    public CustomRule(byte[] toSurvive, byte[] toBeBorn) throws unsupportedRuleException {
        this.toSurvive = toSurvive;
        if(toBeBorn[0] == -1){
            throw new unsupportedRuleException();
        }else{
            this.toBeBorn = toBeBorn;
        }
    }

    /**
     * Determines whether a living will survive the next generation or not.
     *
     *
     * @param cellToCheck cell that is being checked for living neighbors
     * @return true if living neighbors is two or three, else false
     */
    private boolean toSurvive(byte cellToCheck) {
        if (toSurvive[0] == -1) {
            return false;
        } else {
            for (byte i : toSurvive) {
                if (i + 64 == cellToCheck) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if a dead cell will be born the next generation.
     *
     * @param cellToCheck cell that is being checked for living neighbors
     * @return true if there are exactly three living neighbors
     */
    private boolean toBeBorn(byte cellToCheck) {
        for (byte i : toBeBorn) {
            if (i == cellToCheck) {
                return true;
            }
        }
        return false;
    }

    @Override
    public byte setLife(byte cellToCheck) {
        if (toSurvive(cellToCheck) || toBeBorn(cellToCheck)) {
            return 64;
        }
        return 0;
    }

}
