package gol.s305084;

import gol.model.Board.Board;
import gol.model.Board.DynamicBoard;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;

/**
 *
 *
 * @author s305084
 */
public class StatisticsController implements Initializable {

    @FXML
    LineChart lineChart;
    @FXML
    Label txtGen;
    @FXML
    Label txtAlive;
    @FXML
    Label txtChange;

    //TODO Improve mouse position
    @FXML
    Line chartLength;

    Board statBoard = new DynamicBoard();

    private final XYChart.Series<Integer, Integer> LIVINGCELLS = new XYChart.Series();
    private final XYChart.Series<Integer, Integer> CELLCHANGE = new XYChart.Series();
    private final XYChart.Series<Integer, Integer> SIMPERCENT = new XYChart.Series();

    private final static double ALPHA = 0.5;
    private final static double BETA = 3.0;
    private final static double GAMMA = 0.25;

    private final int genIterations = 20;
    private double[] simValue = new double[genIterations + 1];

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lineChart.setTitle("Game of life: Statistics");
        lineChart.setAnimated(false);

        CELLCHANGE.setName("Cell change");
        LIVINGCELLS.setName("Living cells");
        SIMPERCENT.setName("Sim value");
        initMouseListener();

    }

    public void loadeBoard(Board activeBoard) {
        statBoard.insertArray(activeBoard.getBoundingBoxBoard(), 1, 1);
    }

    public void showStats() {
        for (int i = 0; i <= genIterations; i++) {
            byte[][] pattern = statBoard.getBoundingBoxBoard();

            //Counting
            int living = countLivingCells(statBoard);
            LIVINGCELLS.getData().add(new XYChart.Data(i, living));

            //Next gen
            statBoard.nextGen();

            //Life Change
            int change = calcChangeCells(countLivingCells(statBoard), living);
            CELLCHANGE.getData().add(new XYChart.Data(i, change));
            //Similarity measure
            simValue[i] = simValue(pattern, living, change);
            SIMPERCENT.getData().add(new XYChart.Data(i, relativeSim(i, 0)));

        }

        lineChart.getData().addAll(LIVINGCELLS, CELLCHANGE, SIMPERCENT);
        txtAlive.setText("Alive: " + LIVINGCELLS.getData().get(0).getYValue());
        txtChange.setText("Change: " + CELLCHANGE.getData().get(0).getYValue());
    }

    private double relativeSim(int i, int relativePosition) {
        if (Math.max(simValue[relativePosition], simValue[i]) == 0) {
            return 0;
        }
        return Math.floor((Math.min(simValue[relativePosition], simValue[i])
                / Math.max(simValue[relativePosition], simValue[i])) * 100);
    }

    public static int countLivingCells(Board pattern) {
        int count = 0;
        for (int i = 0; i < pattern.getArrayLength(); i++) {
            for (int j = 0; j < pattern.getArrayLength(i); j++) {
                if (pattern.getCellState(i, j)) {
                    count++;
                }
            }
        }
        return count;
    }

    private static int calcChangeCells(int cellsNextgen, int cells) {
        return cellsNextgen - cells;
    }

    private static double simValue(byte[][] pattern, int aliveCount, int aliveChange) {
        int geoSum = 0;
        for (int i = 0; i < pattern.length; i++) {
            for (int j = 0; j < pattern[i].length; j++) {
                //Problem with 0,0 cells?
                if (pattern[i][j] == 64) {
                    geoSum += 2 * (i + 1) + (j + 1);
                }
            }
        }
        return ALPHA * aliveCount + BETA * aliveChange + GAMMA * geoSum;
    }

    private void newRelSimCalc(int relativePosition) {
        SIMPERCENT.getData().clear();

        for (int i = 0; i <= genIterations; i++) {

            SIMPERCENT.getData().add(new XYChart.Data(i, relativeSim(i, relativePosition)));

        }
    }

    private void initMouseListener() {
        lineChart.addEventHandler(MouseEvent.MOUSE_PRESSED,
                (MouseEvent e) -> {
                    double tickSize = chartLength.getEndX() / genIterations;
                    double x = e.getX() - chartLength.getLayoutX() + tickSize / 2;
                    int newGen = (int) (x / tickSize);

                    if (newGen < 0) {
                        newGen = 0;
                    } else if (newGen > genIterations) {
                        newGen = genIterations;
                    }

                    newRelSimCalc(newGen);

                    txtAlive.setText("Alive: " + LIVINGCELLS.getData().get(newGen).getYValue());
                    txtChange.setText("Change: " + CELLCHANGE.getData().get(newGen).getYValue());
                    txtGen.setText("Generation: " + newGen);
                });
    }
}
