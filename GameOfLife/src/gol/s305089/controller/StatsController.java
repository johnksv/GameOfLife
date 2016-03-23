/*2
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.s305089.controller;

import gol.model.Board.Board;
import gol.s305089.model.Stats;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author John Kasper
 */
public class StatsController implements Initializable {

    private Stats stats;
    private byte[][] activeByteBoard;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        stats = new Stats();
    }

    public void setByteBoard(Board activeBoard) {
        this.activeByteBoard = activeBoard.getBoundingBoxBoard();
        stats.setPattern(activeByteBoard);

    }

}
