package gol.model.Logic;

import java.util.List;

/**
 * @author s305084, s305089, s305054
 */
public abstract class Logic {
    
    /**
     * Variables
     */
    
    private Rule activeRule;
    private List<Byte> nextGen;
    
    /**
     *  Methods
     */
    
    public void setGameRule(Rule activeRule) {
        //TODO
        this.activeRule = activeRule;
    }
    
    public void setNextGen() {
        //TODO
    }
    
    public void nextGeneration() {
        //TODO
    }
    
    private void checkRule() {
        //TODO
    }
    

}
