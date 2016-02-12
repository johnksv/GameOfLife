package gol.controller;

import gol.model.Board.Board;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * @author s305054, s305084, s305089
 */
public class GameEngine implements Initializable {

    @FXML
    private Canvas ca;
    //Variables
    private double animationSpeed;
    private Color cellColor;
    private Color backgroundColor;
    private Board activeBoard;
    private GraphicsContext gc;

    @FXML
    public void draw() {
        gc.fillRect(10, 10, 10, 10);
    }

    public void startAnimation() {

    }

    public void stopAnimation() {

    }

    public void constructRule(byte[] cellsToLive, byte[] cellsToSpawn) {

    }

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc = ca.getGraphicsContext2D();
        System.out.println("hei");
    }

}
