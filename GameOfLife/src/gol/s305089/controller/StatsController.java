/*2
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.s305089.controller;

import gol.model.Board.Board;
import gol.s305089.model.Stats;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;

/**
 * FXML Controller class
 *
 * @author John Kasper
 */
public class StatsController implements Initializable {

    @FXML
    private LineChart chart;
    
    private Stats stats;
    private byte[][] activeByteBoard;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        stats = new Stats();
        stats.setPattern(new byte[][]{{0, 1, 0}, {0, 1, 0}, {0, 1, 0}});
        stats.changeInLiving(5);
    }

    public void setByteBoard(Board activeBoard) {
        this.activeByteBoard = activeBoard.getBoundingBoxBoard();
        stats.setPattern(activeByteBoard);

    }

}
