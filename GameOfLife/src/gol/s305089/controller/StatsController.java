/*2
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.s305089.controller;

import gol.model.Board.Board;
import gol.model.Board.DynamicBoard;
import gol.s305089.model.GifMaker;
import gol.s305089.model.Stats;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.PopupWindow;

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
    @FXML
    private ImageView imgViewOriginalPattern;

    private Stats gameStats;
    private Board activeBoard;
    private byte[][] originalPattern;

    private final XYChart.Series<String, Integer> livingCells = new XYChart.Series();
    private final XYChart.Series<String, Integer> changeLivingCells = new XYChart.Series();
    private final XYChart.Series<String, Integer> similarityMeasure = new XYChart.Series();

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
    private void onActionCalculate() {
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
        try {
            updateToolTips();
        } catch (IOException ex) {
            System.err.println("Could not update tooltips:\n" + ex);
        }
    }

    private void updateToolTips() throws IOException {
        setPattern(originalPattern);
        
        GifMaker gifmaker = new GifMaker();
        gifmaker.setCenterPattern(true);

        //Set the original pattern to imgView First.
        // gifmaker.setAutoCellSize(true);
        generateTolltipGIF(gifmaker, imgViewOriginalPattern);
       

        for (XYChart.Data<String, Integer> data : similarityMeasure.getData()) {
            Tooltip tooltip = new Tooltip();

            ImageView imgViewCurrentPattern = new ImageView();
            generateTolltipGIF(gifmaker, imgViewCurrentPattern);

            VBox container = new VBox();
            container.getChildren().addAll(new Label("Current:"), imgViewCurrentPattern);

            tooltip.setGraphic(container);
            data.getNode().setOnMouseEntered(event -> {
                double anchorX = data.getNode().getScene().getWindow().getX() + 20;
                double anchorY = data.getNode().getScene().getWindow().getY() + 20;
                tooltip.show(data.getNode(), anchorX + event.getSceneX(), anchorY + event.getSceneY());
            });
            data.getNode().setOnMouseExited(event -> tooltip.hide());

            activeBoard.nextGen();
        }
    }

    private void generateTolltipGIF(GifMaker gifmaker, ImageView imgViewcurrentPattern) throws IOException {
        gifmaker.setPattern(activeBoard.getBoundingBoxBoard());
        File tempFileToolTip = File.createTempFile("golStats", ".gif");

        gifmaker.setSaveLocation(tempFileToolTip.getAbsolutePath());
        gifmaker.writePatternToGIF(1);

        Image current = new Image(tempFileToolTip.toURI().toString());
        imgViewcurrentPattern.setImage(current);
        tempFileToolTip.delete();
    }

    public void setByteBoard(Board activeBoard) {
        setPattern(activeBoard.getBoundingBoxBoard());
        gameStats.setPattern(activeBoard.getBoundingBoxBoard());
    }

    public void setPattern(byte[][] Pattern) {
        //TODO dynaimc size of board
        //TODO Move Method to helper method?
        activeBoard = new DynamicBoard(10, 10);
        originalPattern = Pattern;
        activeBoard.insertArray(originalPattern, 1, 1);
    }

}
