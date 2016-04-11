package gol.s305084;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

/**
 * Calculates and shows Statistics of a board. Shows living cells after chosen
 * amount of generations.
 *
 * @author S305084
 */
public class Statistics {

    private static double alpha = 0.5;
    private static double beta = 3.0;
    private static double gamma = 0.25;

    //Make dynamic board
    //FXML for the scene, make it start at -1 not 0
    public static void showStatistics(Stage stage, Board activeBoard) {

        Board statBoard = new ArrayBoard(activeBoard.getArrayLength(), activeBoard.getArrayLength(0));
        //My break :(
        statBoard.insertArray(activeBoard.getGameBoard(), 0, 0);
        stage.setTitle("Gol: Statistics");
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Number of Generations");
        //creating the chart
        final LineChart<Number, Number> lineChart
                = new LineChart<>(xAxis, yAxis);

        lineChart.setTitle("Game of life: Statistics");
        XYChart.Series livingCells = new XYChart.Series();
        XYChart.Series cellChange = new XYChart.Series();
        XYChart.Series simProcent = new XYChart.Series();
        cellChange.setName("Cell change");
        livingCells.setName("Living cells");
        simProcent.setName("Sim value");

        double firstSimValue = 0;
        for (int i = 0; i < 50; i++) {
            byte[][] pattern = statBoard.getBoundingBoxBoard();
            //Counting
            int living = countLivingCells(statBoard);
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
        Scene scene = new Scene(lineChart, 900, 600);
        lineChart.getData().addAll(livingCells, cellChange, simProcent);

        stage.setScene(scene);
        stage.show();
    }

    public static int countLivingCells(Board pattern) {
        int count = 0;
        for (int i = 0; i < pattern.getArrayLength(); i++) {
            for (int j = 0; j < pattern.getArrayLength(0); j++) {
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
            for (int j = 0; j < pattern[0].length; j++) {
                //Problem with 0,0 cells?
                if (pattern[i][j] == 64) {
                    geoSum += 2 * (i + 1) + (j + 1);
                }
            }
        }
        return alpha * aliveCount + beta * aliveChange + gamma * geoSum;
    }
}
