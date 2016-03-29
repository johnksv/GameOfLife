/*2
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.s305089.controller;

import gol.model.Board.Board;
import gol.s305089.model.Stats;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

/**
 * FXML Controller class
 *
 * @author John Kasper
 */
public class StatsController implements Initializable {

    @FXML
    private LineChart chart;
    @FXML
    private ProgressIndicator progIndicator;
    @FXML
    private Spinner spinnerIterations;

    private Stats gameStats;

    private final XYChart.Series livingCells = new XYChart.Series();
    private final XYChart.Series changeLivingCells = new XYChart.Series();
    private final XYChart.Series similarityMeasure = new XYChart.Series();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gameStats = new Stats();
        gameStats.setPattern(new byte[][]{{0, 1, 0}, {0, 1, 0}, {0, 1, 0}});
        initView();
    }

    private void initView() {
        livingCells.setName("Living cells");
        changeLivingCells.setName("Change in living cells");
        similarityMeasure.setName("Similarity Measure");
        chart.getData().addAll(livingCells, changeLivingCells, similarityMeasure);

        spinnerIterations.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(3, 100, 20, 1));
        spinnerIterations.setEditable(true);
        spinnerIterations.valueProperty().addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
            calculatGameStats();
        });
    }

    @FXML
    private void calculatGameStats() {
        displayData((int) spinnerIterations.getValue());
    }

    private void displayData(int iterations) {
        progIndicator.setVisible(true);

        livingCells.getData().clear();
        changeLivingCells.getData().clear();
        similarityMeasure.getData().clear();

        //TODO Thread
        int[][] gameData = gameStats.getStatistics(iterations);

        //ignors the last iteration
        for (int i = 0; i < gameData.length - 1; i++) {
            livingCells.getData().add(new XYChart.Data("" + i, gameData[i][0]));
            changeLivingCells.getData().add(new XYChart.Data("" + i, gameData[i][1]));
            similarityMeasure.getData().add(new XYChart.Data("" + i, gameData[i][2]));
        }
        
        progIndicator.setVisible(false);
    }
    
    public void setByteBoard(Board activeBoard) {
        gameStats.setPattern(activeBoard.getBoundingBoxBoard());
    }
}
