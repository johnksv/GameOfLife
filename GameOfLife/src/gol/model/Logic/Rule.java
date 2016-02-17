package gol.model.Logic;

/**
 * @author s305084, s305089, s305054
 */
public interface Rule {
    
    //byte[] cellsToLive;
    //byte[] cellsToSpawn;
    
    public byte checkRules(byte cellToCheck);

    
}
