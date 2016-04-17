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
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
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
    private Spinner spinnerIterations;
    @FXML
    private CheckBox cbLivingCells;
    @FXML
    private CheckBox cbChangeLiving;
    @FXML
    private CheckBox cbSimilarity;
    @FXML
    private RadioButton rbCheckPrev;

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
        initView();
        intiListners();
    }

    private void initView() {
        livingCells.setName("Living cells");
        changeLivingCells.setName("Change in living cells");
        similarityMeasure.setName("Similarity Measure");
        chart.getData().addAll(livingCells, changeLivingCells, similarityMeasure);

        spinnerIterations.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(3, 100, 20, 1));
        spinnerIterations.setEditable(true);
    }

    private void intiListners() {
        spinnerIterations.valueProperty().addListener(this::calculatGameStats);
        cbLivingCells.selectedProperty().addListener(this::calculatGameStats);
        cbChangeLiving.selectedProperty().addListener(this::calculatGameStats);
        cbSimilarity.selectedProperty().addListener(this::calculatGameStats);
        rbCheckPrev.selectedProperty().addListener(this::calculatGameStats);
    }

    private void calculatGameStats(ObservableValue observable, Object oldValue, Object newValue) {
        displayData((int) spinnerIterations.getValue());
    }
    @FXML
    private void onActionCalculate(){
        calculatGameStats(null, null, null);
    }

    private void displayData(int iterations) {

        livingCells.getData().clear();
        changeLivingCells.getData().clear();
        similarityMeasure.getData().clear();

        gameStats.setCheckSimilarityPrevGen(rbCheckPrev.isSelected());

        //TODO Threading(?)
        int[][] gameData = gameStats.getStatistics(iterations,
                cbChangeLiving.isSelected(), cbSimilarity.isSelected());

        //Ignors the last iteration of the list
        for (int i = 0; i < gameData.length - 1; i++) {
            if (cbLivingCells.isSelected()) {
                livingCells.getData().add(new XYChart.Data("" + i, gameData[i][0]));
            }
            if (cbChangeLiving.isSelected()) {
                changeLivingCells.getData().add(new XYChart.Data("" + i, gameData[i][1]));
            }
            if (cbSimilarity.isSelected()) {
                similarityMeasure.getData().add(new XYChart.Data("" + i, gameData[i][2]));
            }

        }

    }

    public void setByteBoard(Board activeBoard) {
        gameStats.setPattern(activeBoard.getBoundingBoxBoard());
    }
}
