package gol.svergja.controller;

import gol.model.Board.Board;
import gol.model.Board.DynamicBoard;
import gol.svergja.Util;
import gol.svergja.model.GifMaker;
import gol.svergja.model.Stats;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * FXML Controller class
 *
 * @author s305089 - John Kasper Svergja
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
    @FXML
    private ImageView imgViewOriginalPattern;

    private Stats gameStats;
    private Board activeBoard;
    private byte[][] originalPattern;
    private int[][] gameData;

    private final XYChart.Series<String, Integer> livingCells = new XYChart.Series();
    private final XYChart.Series<String, Integer> changeLivingCells = new XYChart.Series();
    private final XYChart.Series<String, Integer> similarityMeasure = new XYChart.Series();
    private final ArrayList<Integer> simMeasureClosest = new ArrayList();

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
        cbLivingCells.selectedProperty().addListener(this::calculatGameStats);
        cbChangeLiving.selectedProperty().addListener(this::calculatGameStats);
        cbSimilarity.selectedProperty().addListener(this::calculatGameStats);
        rbCheckPrev.selectedProperty().addListener(this::calculatGameStats);
    }

    private void calculatGameStats(ObservableValue observable, Object oldValue, Object newValue) {
        displayData((int) spinnerIterations.getValue());
    }

    @FXML
    private void onActionCalculate() {
        calculatGameStats(null, null, null);
    }

    private void displayData(int iterations) {

        if (iterations < livingCells.getData().size()) {
            plotDataOnChart(iterations);
        } else {
            gameStats.setCheckSimilarityPrevGen(rbCheckPrev.isSelected());

            //TODO Threading(?)
            gameData = gameStats.getStatistics(iterations,
                    cbChangeLiving.isSelected(), cbSimilarity.isSelected());

            plotDataOnChart(gameData.length - 1);
        }
        //TODO Threading of tooltips    
        updateTooltips();
        try {
            updateTooltipSimilarity();
        } catch (IOException ex) {
            System.err.println("Could not update tooltips:\n" + ex);
        }
    }

    private void plotDataOnChart(int iterations) {
        //Removes previous data
        livingCells.getData().clear();
        changeLivingCells.getData().clear();
        similarityMeasure.getData().clear();
        simMeasureClosest.clear();

        //Ignors the last iteration of the list
        for (int i = 0; i < iterations; i++) {
            if (cbLivingCells.isSelected()) {
                livingCells.getData().add(new XYChart.Data("" + i, gameData[i][0]));
            }
            if (cbChangeLiving.isSelected()) {
                changeLivingCells.getData().add(new XYChart.Data("" + i, gameData[i][1]));
            }
            if (cbSimilarity.isSelected()) {
                similarityMeasure.getData().add(new XYChart.Data("" + i, gameData[i][2]));
                simMeasureClosest.add(gameData[i][3]);
            }
        }
    }

    private void updateTooltips() {
        for (XYChart.Data<String, Integer> data : livingCells.getData()) {
            Tooltip tip = new Tooltip("Living: " + data.getYValue());
            tip.setFont(new Font(15));
            setTooltipMouseHandler(data, tip);
        }
        for (XYChart.Data<String, Integer> data : changeLivingCells.getData()) {
            Tooltip tip = new Tooltip("Change in living: " + data.getYValue());
            tip.setFont(new Font(15));
            setTooltipMouseHandler(data, tip);
        }
    }

    private void updateTooltipSimilarity() throws IOException {
        setPattern(originalPattern);

        GifMaker gifmaker = new GifMaker();

        //Set the original pattern to imgView First.
        gifmaker.setCenterPattern(true);
        gifmaker.setAutoCalcCellSize(true);
        generateTolltipGIF(gifmaker, imgViewOriginalPattern);

        for (int i = 0; i < similarityMeasure.getData().size(); i++) {
            XYChart.Data<String, Integer> data = similarityMeasure.getData().get(i);

            Tooltip tooltip = new Tooltip();

            ImageView imgViewCurrentPattern = new ImageView();
            generateTolltipGIF(gifmaker, imgViewCurrentPattern);
            Label labelInfo = new Label("Iteration " + i + ".");
            labelInfo.setFont(new Font(15));
            Label currentPattern = new Label("Current pattern:");
            currentPattern.setFont(new Font(13));
            Label labelMatch = new Label(data.YValueProperty().getValue() + " % match.");
            labelMatch.setFont(new Font(14));
            Label labelMatchWith;
            if (simMeasureClosest.get(i) == -1) {
                labelMatchWith = new Label("No close match was found...");
            } else {
                labelMatchWith = new Label("First/closest match on iteration number: " + simMeasureClosest.get(i));
            }
            labelMatchWith.setFont(new Font(14));

            VBox container = new VBox();
            container.getChildren().addAll(labelInfo, labelMatch, labelMatchWith, currentPattern, imgViewCurrentPattern);

            tooltip.setGraphic(container);
            setTooltipMouseHandler(data, tooltip);

            activeBoard.nextGen();
        }
    }

    private void setTooltipMouseHandler(XYChart.Data<String, Integer> data, Tooltip tooltip) {
        data.getNode().setOnMouseEntered(event -> Util.showTooltip(event, data.getNode(), tooltip));
        data.getNode().setOnMouseExited(event -> tooltip.hide());
    }

    private void generateTolltipGIF(GifMaker gifmaker, ImageView imgViewcurrentPattern) throws IOException {
        gifmaker.setBoard(activeBoard);
        File tempFileToolTip = File.createTempFile("golStats", ".gif");

        gifmaker.writePatternToGIF(1, tempFileToolTip.getAbsolutePath());

        Image current = new Image(tempFileToolTip.toURI().toString());
        imgViewcurrentPattern.setImage(current);
        tempFileToolTip.delete();
    }

    public void setBoard(Board boardToSet) {
        gameStats.setBoard(boardToSet);
        originalPattern = boardToSet.getBoundingBoxBoard();
        activeBoard = new DynamicBoard(10, 10);
        activeBoard.setRule(boardToSet.getRule());
        setPattern(originalPattern);
    }

    private void setPattern(byte[][] Pattern) {
        activeBoard.clearBoard();
        activeBoard.insertArray(Pattern);
    }

}
