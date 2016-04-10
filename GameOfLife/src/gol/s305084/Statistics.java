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

    //Make dynamic board
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

        lineChart.setTitle("Stock Monitoring, 2010");
        XYChart.Series series = new XYChart.Series();
        series.setName("Living cells");
        
        for (int i = 0; i < 50; i++) {
            series.getData().add(new XYChart.Data(i, countLivingCells(statBoard)));
            statBoard.nextGen();
        }
        Scene scene = new Scene(lineChart, 800, 600);
        lineChart.getData().add(series);

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

}
