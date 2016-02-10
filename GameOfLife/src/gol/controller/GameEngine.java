package gol.controller;

import gol.model.Board.Board;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

/**
 * @author s305054, s305084, s305089
 */
public class GameEngine {
 
    //Variables
    private double animationSpeed;
    private Color cellColor;
    private Color backgroundColor;
    private Board activeBoard;
    
    
    /**
     * @return the animationSpeed
     */
    public double getAnimationSpeed() {
        return animationSpeed;
    }

    /**
     * @param animationSpeed the animationSpeed to set
     */
    public void setAnimationSpeed(double animationSpeed) {
        this.animationSpeed = animationSpeed;
    }

    /**
     * @param cellColor the cellColor to set
     */
    public void setCellColor(Color cellColor) {
        this.cellColor = cellColor;
    }

    /**
     * @param backgroundColor the backgroundColor to set
     */
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * @return the activeBoard
     */
    public Board getActiveBoard() {
        return activeBoard;
    }

    /**
     * @param activeBoard the activeBoard to set
     */
    public void setActiveBoard(Board activeBoard) {
        this.activeBoard = activeBoard;
    }
    

    public void draw(Canvas canvas){
        
    }
    
    public void startAnimation(){
        
    }
    
    public void stopAnimation(){
        
    }
    
    public void constructRule(byte[] cellsToLive, byte[] cellsToSpawn){
        
    }
    
}
