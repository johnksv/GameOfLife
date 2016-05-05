package gol.stensli;

import gol.model.UsefullMethods;
import gol.model.Board.Board;
import gol.model.Board.DynamicBoard;
import gol.other.Configuration;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import lieng.GIFWriter;

/**
 * Shows statistics for a Board through a number of generations. <br>
 *
 * <b>Notable static functions:</b>
 * <ul><li>{@link #countLivingCells(gol.model.Board.Board) countLivingCells}</li>
 * <li>{@link #calcChangeCells(int, int) countLivingCells}</li>
 * <li>{@link #simValue(byte[][], int, int) calculate similarity value}</li>
 * </ul>
 *
 * @author s305084 - Stian H. Stensli
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
    @FXML
    Line chartLength;

    Board statBoard = new DynamicBoard();

    private final XYChart.Series<Integer, Integer> LIVINGCELLS = new XYChart.Series();
    private final XYChart.Series<Integer, Integer> CELLCHANGE = new XYChart.Series();
    private final XYChart.Series<Integer, Integer> SIMPERCENT = new XYChart.Series();

    private final int GIFW;
    private final int GIFH;
    private final int GIFSPEED;

    private int selectedGen = 0;

    private final static double ALPHA = 0.5;
    private final static double BETA = 3.0;
    private final static double GAMMA = 0.25;

    private final int genIterations = 20;

    private double[] simValue = new double[genIterations + 1];

    /**
     * Retrieves all configuration values when created.
     */
    public StatisticsController() {
        //Should never return -1
        GIFW = Configuration.getPropInt("gifWidth");

        GIFH = Configuration.getPropInt("gifHeight");

        GIFSPEED = Configuration.getPropInt("gifSpeed");
    }

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

    /**
     * Inserts board and rule.
     * Statistics are all calculated from this pattern and rule.
     *
     * @param activeBoard Inserted pattern with rule
     */
    public void loadBoard(Board activeBoard) {
        statBoard.insertArray(activeBoard.getBoundingBoxBoard());
        statBoard.setRule(activeBoard.getRule());
    }

    /**
     * <p>
     * Calculates and visualise statistics. Types of data: Living cells, change
     * in cells, and similarity value. Similarity value is by default defined by
     * how similar each generation is to generation 0.
     * </p>
     * <b>Note:</b> Mouse-click on the lineChart will change which generation
     * the similarity value will be compared to.
     */
    public void showStats() {
        LIVINGCELLS.getData().clear();
        SIMPERCENT.getData().clear();
        CELLCHANGE.getData().clear();

        Board copyBoard = new DynamicBoard();
        copyBoard.insertArray(statBoard.getBoundingBoxBoard());
        copyBoard.setRule(statBoard.getRule());
        for (int i = 0; i <= genIterations; i++) {
            byte[][] pattern = copyBoard.getBoundingBoxBoard();

            //Counting
            int living = countLivingCells(copyBoard);
            LIVINGCELLS.getData().add(new XYChart.Data(i, living));

            //Next gen
            copyBoard.nextGen();

            //Life Change
            int change = calcChangeCells(living, countLivingCells(copyBoard));
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

    /**
     * Returns number of living cells for given pattern.
     *
     * @param pattern Board object
     * @return Living cells
     */
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

    /**
     * Returns the change of living cells between a pattern and its next
     * generation.
     *
     * @param cells alive
     * @param cellsNextgen alive Next generation
     * @return Change
     */
    public static int calcChangeCells(int cells, int cellsNextgen) {
        return cellsNextgen - cells;
    }

    /**
     * Returns similarity value for given pattern. Does not return a similarity
     * to another pattern, but a value defined as:
     * <p>
     * <b>ALPHA * aliveCount + BETA * aliveChange + GAMMA * geoSum</b>
     * </p>
     *
     * @param pattern Board object
     * @param aliveCount number of living cells
     * @param aliveChange change of living cells
     * @return Similarity value
     */
    public static double simValue(byte[][] pattern, int aliveCount, int aliveChange) {
        int geoSum = 0;
        for (int i = 0; i < pattern.length; i++) {
            for (int j = 0; j < pattern[i].length; j++) {
                //Algorithm change to prevent 0,0 position to be negligible.
                if (pattern[i][j] == 64) {
                    geoSum += (i + 1) + (j + 1);
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

    /**
     * If clicked, all generations similarity values will show there best future
     * match. This is to create a easy view for finding the best generation to
     * loop in the future.
     */
    @FXML
    private void handleShowAll() {
        if (cbShowAll.isSelected()) {
            SIMPERCENT.getData().clear();
            //Last gen always 0, therefore not included.
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
            txtGen.setText("Generation: 0");
            txtAlive.setText("Alive: " + LIVINGCELLS.getData().get(0).getYValue());
            txtChange.setText("Change: " + CELLCHANGE.getData().get(0).getYValue());
            txtSim.setText("Simvalue: " + SIMPERCENT.getData().get(0).getYValue());
        } else {
            showStats();
        }

    }

    @FXML
    private void handleGifBtn() {
        Board activeBoard = new DynamicBoard();
        activeBoard.insertArray(statBoard.getBoundingBoxBoard());
        activeBoard.setRule(statBoard.getRule());
        //Finding best loop
        //loop array contains {generationIndex}, {value}
        double[] loop = new double[2];
        for (int i = selectedGen + 1; i <= genIterations; i++) {
            if (loop[1] < relativeSim(i, selectedGen)) {

                loop[1] = relativeSim(i, selectedGen);
                loop[0] = i;
            }
        }
        int frames = (int) loop[0] - selectedGen;

        System.out.println(loop[0]);
        //Go to selectedBoard
        for (int i = 0; i < selectedGen; i++) {
            activeBoard.nextGen();
        }

        //Aborts if board is empty or if only one frame will be created.
        if (activeBoard.getBoundingBox()[1] - activeBoard.getBoundingBox()[0] < 0) {
            UsefullMethods.showErrorAlert("Board is empty.", "Sorry, but you cant make a gif with no living cells.");
            return;

        } else if (frames <= 1) {
            if (selectedGen > genIterations - 2) {
                UsefullMethods.showErrorAlert("Warning.", "Sorry, but you canot make a gif from this generation.");
                return;
            } else {
                UsefullMethods.showErrorAlert("Warning.", "Sorry, but the best frame was the next one.\n"
                        + "The gif will now loop beetween the second best. ");
                loop[1] = 0;
                for (int i = selectedGen + 2; i <= genIterations; i++) {
                    if (loop[1] < relativeSim(i, selectedGen)) {

                        loop[1] = relativeSim(i, selectedGen);
                        loop[0] = i;
                    }

                }
                System.out.println(loop[0]);
                frames = (int) loop[0] - selectedGen;

            }
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Gif format", "*.gif"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selected = fileChooser.showSaveDialog(null);
        if (selected != null) {
            try {
                GifMaker.makeGif(activeBoard, new GIFWriter(GIFW, GIFH, selected.toString(),
                        GIFSPEED), GIFW, GIFH, java.awt.Color.WHITE, java.awt.Color.BLACK, frames);

            } catch (IOException ex) {
                UsefullMethods.showErrorAlert("Sorry!", "Something went wrong during saving \n please try again.");
            }
        }

    }

    private void initMouseListener() {
        lineChart.addEventHandler(MouseEvent.MOUSE_PRESSED,
                (MouseEvent e) -> {
                    double tickSize = LIVINGCELLS.getData().get(1).getNode().getLayoutX() - LIVINGCELLS.getData().get(0).getNode().getLayoutX();
                    //Not optimal but, I did not find a method to get  
                    //the pixel value of a node relative to the scene.
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

                }
        );
    }
}
