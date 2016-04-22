package gol.s305084;

import gol.model.Board.Board;
import gol.model.Board.DynamicBoard;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

/**
 *
 *
 * @author s305084
 */
public class StatisticsController implements Initializable {

    @FXML
    LineChart lineChart;

    Board statBoard = new DynamicBoard();

    XYChart.Series livingCells = new XYChart.Series();
    XYChart.Series cellChange = new XYChart.Series();
    XYChart.Series simProcent = new XYChart.Series();

    private static double alpha = 0.5;
    private static double beta = 3.0;
    private static double gamma = 0.25;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        lineChart.setTitle("Game of life: Statistics");

        cellChange.setName("Cell change");
        livingCells.setName("Living cells");
        simProcent.setName("Sim value");
        
        

    }

    public void loadeBoard(Board activeBoard) {
        statBoard.insertArray(activeBoard.getBoundingBoxBoard(), 1, 1);
        System.out.println("Board Loaded");
    }

    public void showStats() {
        double firstSimValue = 0;
        for (int i = 0; i < 20; i++) {
            byte[][] pattern = statBoard.getBoundingBoxBoard();
            //Counting
            int living = countLivingCells(statBoard);
            System.out.println(living);
            livingCells.getData().add(new XYChart.Data(i, living));

            //Life Change
            statBoard.nextGen();
            int change = calcChangeCells(countLivingCells(statBoard), living);
            cellChange.getData().add(new XYChart.Data(i, change));
            //Similarity measure
            if (i == 0) {
                firstSimValue = simValue(pattern, living, change);
                simProcent.getData().add(new XYChart.Data(0, 100));
            } else {
                double simValue = simValue(pattern, living, change);
                int asasgas = (int) Math.floor((Math.min(firstSimValue, simValue) / Math.max(firstSimValue, simValue)) * 100);
                simProcent.getData().add(new XYChart.Data(i, asasgas));
            }
        }
        
        lineChart.getData().addAll(livingCells, cellChange, simProcent);
        
        System.out.println(livingCells.getData().get(0));
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
        return alpha * aliveCount + beta * aliveChange + gamma * geoSum;
    }
}
