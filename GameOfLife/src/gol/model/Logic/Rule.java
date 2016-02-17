package gol.model.Logic;

/**
 * @author s305054, s305084, s305089
 */
public interface Rule {
    
    //byte[] cellsToLive;
    //byte[] cellsToSpawn;
    
    public byte checkRules(byte cellToCheck);

    
}
