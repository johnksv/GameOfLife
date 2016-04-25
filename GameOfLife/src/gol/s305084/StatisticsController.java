package gol.s305084;

import gol.model.Board.Board;
import gol.model.Board.DynamicBoard;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import lieng.GIFWriter;

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
    @FXML
    Label txtSim;

    @FXML
    CheckBox cbShowAll;

    //TODO Improve mouse position
    @FXML
    Line chartLength;

    Board statBoard = new DynamicBoard();

    private final XYChart.Series<Integer, Integer> LIVINGCELLS = new XYChart.Series();
    private final XYChart.Series<Integer, Integer> CELLCHANGE = new XYChart.Series();
    private final XYChart.Series<Integer, Integer> SIMPERCENT = new XYChart.Series();

    private final int GIFW = 140;
    private final int GIFH = 140;
    private int selectedGen = 0;

    private final static double ALPHA = 0.5;
    private final static double BETA = 3.0;
    private final static double GAMMA = 0.25;

    private final int genIterations = 20;
    private double[] simValue = new double[genIterations + 1];

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lineChart.setTitle("Game of life: Statistics");
        lineChart.getData().addAll(LIVINGCELLS, CELLCHANGE, SIMPERCENT);
        lineChart.setAnimated(false);
        lineChart.setCursor(Cursor.HAND);

        CELLCHANGE.setName("Cell change");
        LIVINGCELLS.setName("Living cells");
        SIMPERCENT.setName("Sim value");

        initMouseListener();

    }

    public void loadeBoard(Board activeBoard) {
        statBoard.insertArray(activeBoard.getBoundingBoxBoard(), 1, 1);
    }

    public void showStats() {
        LIVINGCELLS.getData().clear();
        SIMPERCENT.getData().clear();
        CELLCHANGE.getData().clear();

        Board copyBoard = new DynamicBoard();
        copyBoard.insertArray(statBoard.getBoundingBoxBoard(), 1, 1);
        for (int i = 0; i <= genIterations; i++) {
            byte[][] pattern = copyBoard.getBoundingBoxBoard();

            //Counting
            int living = countLivingCells(copyBoard);
            LIVINGCELLS.getData().add(new XYChart.Data(i, living));

            //Next gen
            copyBoard.nextGen();

            //Life Change
            int change = calcChangeCells(countLivingCells(copyBoard), living);
            CELLCHANGE.getData().add(new XYChart.Data(i, change));
            //Similarity measure
            simValue[i] = simValue(pattern, living, change);
            SIMPERCENT.getData().add(new XYChart.Data(i, relativeSim(i, 0)));

        }
        txtSim.setText("");
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

    @FXML
    public void handleShowAll() {
        if (cbShowAll.isSelected()) {
            SIMPERCENT.getData().clear();
            for (int i = 0; i < genIterations; i++) {
                double maxSim = 0;

                for (int j = i; j <= genIterations; j++) {
                    if (j == i) {
                        continue;
                    }
                    if (maxSim < relativeSim(j, i)) {
                        maxSim = relativeSim(j, i);
                    }
                }

                SIMPERCENT.getData().add(new XYChart.Data(i, maxSim));
            }

            txtAlive.setText("Alive: " + LIVINGCELLS.getData().get(0).getYValue());
            txtChange.setText("Change: " + CELLCHANGE.getData().get(0).getYValue());
            txtSim.setText("Simvalue: " + SIMPERCENT.getData().get(0).getYValue());
        } else {
            showStats();
        }

    }

    @FXML
    public void handleGifBtn() {
        Board activeBoard = new DynamicBoard();
        activeBoard.insertArray(statBoard.getBoundingBoxBoard(), 1, 1);

        //Finding best loop
        //loop array contains generation-Index, value
        double[] loop = new double[2];
        for (int i = selectedGen; i < genIterations; i++) {
            if (i != selectedGen) {
                if (loop[1] < relativeSim(i, selectedGen)) {

                    loop[1] = relativeSim(i, selectedGen);
                    loop[0] = i;
                }
            }
        }
        int frames = (int) loop[0] - selectedGen;

        //Go to selectedBoard
        for (int i = 0; i < selectedGen; i++) {
            activeBoard.nextGen();
        }

        //Aborts if board is empty or if only one frame will be created.
        if (activeBoard.getBoundingBox()[1] - activeBoard.getBoundingBox()[0] < 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Sorry, but you cant make a gif with no cells alive.");
            alert.showAndWait();
        } else if (frames <= 1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Oops!");
            alert.setHeaderText("Sorry, but the best frame was the next one.\n"
                    + "You may have selected the wrong starting frame? ");
            alert.showAndWait();
        } else {

            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Gif format", "*.gif"),
                    new FileChooser.ExtensionFilter("All Files", "*.*"));
            File selected = fileChooser.showSaveDialog(null);
            if (selected != null) {
                try {
                    GifMaker.makeGif(activeBoard.getBoundingBoxBoard(), new GIFWriter(GIFW, GIFH, selected.toString(),
                            500), GIFW, GIFH, java.awt.Color.WHITE, java.awt.Color.BLACK, frames);

                } catch (IOException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
                    alert.setTitle("Error");
                    alert.setHeaderText("Sorry, something  went wrong during saving");
                    alert.showAndWait();
                }
            }
        }
    }

    private void initMouseListener() {
        lineChart.addEventHandler(MouseEvent.MOUSE_PRESSED,
                (MouseEvent e) -> {
                    double tickSize = chartLength.getEndX() / genIterations;
                    double x = e.getX() - chartLength.getLayoutX() + tickSize / 2;
                    selectedGen = (int) (x / tickSize);
                    if (selectedGen < 0) {
                        selectedGen = 0;
                    } else if (selectedGen > genIterations) {
                        selectedGen = genIterations;
                    }

                    if (!cbShowAll.isSelected()) {
                        newRelSimCalc(selectedGen);
                        txtSim.setText("");
                    } else if (selectedGen == genIterations) {
                        txtSim.setText("");
                    } else {
                        txtSim.setText("Simvalue: " + SIMPERCENT.getData().get(selectedGen).getYValue());
                    }
                    txtAlive.setText("Alive: " + LIVINGCELLS.getData().get(selectedGen).getYValue());
                    txtChange.setText("Change: " + CELLCHANGE.getData().get(selectedGen).getYValue());
                    txtGen.setText("Generation: " + selectedGen);

                });
    }
}
