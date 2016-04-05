package gol.controller;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
import gol.model.FileIO.PatternFormatException;
import gol.model.FileIO.ReadFile;
import gol.model.Logic.ConwaysRule;
import gol.model.Logic.CustomRule;
import gol.model.Logic.unsupportedRuleException;
import gol.s305089.controller.EditorController;
import gol.s305089.model.GifMaker;
import gol.s305089.controller.GifMakerController;
import gol.s305089.controller.StatsController;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * @author s305054, s305084, s305089
 */


public class GameController implements Initializable {

    @FXML
    private Canvas canvas;
    @FXML
    private BorderPane borderpane;
    @FXML
    private TabPane tabpane;
    @FXML
    private Slider cellSizeSlider;
    @FXML
    private Slider gridSpacingSlider;
    @FXML
    private Slider animationSpeedSlider;
    @FXML
    private Label animationSpeedLabel;
    @FXML
    private Button startPauseBtn;
    @FXML
    private ColorPicker cellCP;
    @FXML
    private ColorPicker backgroundCP;
    @FXML
    private RadioButton rbRemoveCell;
    @FXML
    private RadioButton rbMoveGrid;
    @FXML
    private ToggleGroup tgGameRules;
    @FXML
    private RadioButton rbCustomGameRules;
    @FXML
    private TextField tfCellsToBeBorn;
    @FXML
    private TextField tfCellsToSurvive;
    @FXML
    private Button btnUseRule;
    @FXML
    private CheckBox cbShowGrid;
    //TODO Show grid is not working yet. Implement it

    private Board activeBoard;
    private final Timeline timeline = new Timeline();
    private GraphicsContext gc;

    private Color cellColor = Color.BLACK;
    private Color backgroundColor = Color.web("#F4F4F4");
    private byte[][] boardFromFile;
    private int mousePositionX;
    private int mousePositionY;
    //Offset x, offset y, old x, old y
    private final double[] moveGridValues = {0, 0, -Double.MAX_VALUE, -Double.MAX_VALUE};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc = canvas.getGraphicsContext2D();

        canvas.widthProperty().bind(borderpane.widthProperty().subtract(tabpane.widthProperty()));
        canvas.heightProperty().bind(borderpane.heightProperty());

        activeBoard = new ArrayBoard();
        cellCP.setValue(Color.BLACK);
        backgroundCP.setValue(Color.web("#F4F4F4"));
        //TODO bug if used like this with new zoom!
        mouseInit();
        handleZoom();
        handleGridSpacingSlider();
        handleColor();
        handleZoom();
        handleAnimationSpeedSlider();
        initAnimation();
        initGameRulesListner();

    }

    private void initGameRulesListner() {
        tgGameRules.selectedToggleProperty().addListener((ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) -> {
            if (newValue == rbCustomGameRules) {
                tfCellsToSurvive.setDisable(false);
                tfCellsToBeBorn.setDisable(false);
                btnUseRule.setDisable(false);
                handleRuleBtn();
            } else {
                tfCellsToSurvive.setDisable(true);
                tfCellsToBeBorn.setDisable(true);
                btnUseRule.setDisable(true);
                activeBoard.setGameRule(new ConwaysRule());
            }
        });
    }

    private void initAnimation() {
        Duration duration = Duration.millis(1000);
        KeyFrame keyframe = new KeyFrame(duration, (ActionEvent e) -> {
            activeBoard.nextGen();
            draw();
        });
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.getKeyFrames().add(keyframe);

    }

    @FXML
    private void handleAnimation() {

        if (timeline.getStatus() == Animation.Status.RUNNING) {
            timeline.pause();
            startPauseBtn.setText("Start game");
        } else {
            timeline.play();
            startPauseBtn.setText("Pause game");
        }
    }

    @FXML
    private void handleAnimationSpeedSlider() {
        double animationSpeed = animationSpeedSlider.getValue();
        timeline.setRate(animationSpeed);
        animationSpeedLabel.setText(String.format("%.2f %s", animationSpeed, " "));
    }

    @FXML
    public void handleRuleText() {
        //TODO

    }

    @FXML
    private void handleRuleBtn() {
        byte[] toBeBorn;
        byte[] toSurvive;
        System.out.println(tfCellsToSurvive.getText());
        if (tfCellsToSurvive.getText().replaceAll("\\D", "").equals("")) {
            tfCellsToSurvive.setText("");
            toSurvive = new byte[]{-1};
        } else {
            String[] toSurviveString = tfCellsToSurvive.getText().replaceAll("\\D", "").split("");

            toSurvive = new byte[toSurviveString.length];
            for (int i = 0; i < toSurvive.length; i++) {
                if (Character.isDigit(toSurviveString[i].charAt(0)) && toSurviveString[i].length() == 1) {
                    toSurvive[i] = (byte) Integer.parseInt(toSurviveString[i]);
                }
            }

        }
        if (tfCellsToBeBorn.getText().replaceAll("\\D", "").equals("")) {
            tfCellsToBeBorn.setText("");
            toBeBorn = new byte[]{-1};
        } else {
            String[] toBeBornString = tfCellsToBeBorn.getText().replaceAll("\\D", "").split("");

            toBeBorn = new byte[toBeBornString.length];
            for (int i = 0; i < toBeBorn.length; i++) {
                if (Character.isDigit(toBeBornString[i].charAt(0)) && toBeBornString[i].length() == 1) {
                    toBeBorn[i] = (byte) Integer.parseInt(toBeBornString[i]);
                }
            }
        }

        constructRule(toSurvive, toBeBorn);
    }

    /**
     * //TODO bug?
     *
     * @Bug You can cheat this method if you zoom out max with max spacing, then
     * remove the spacing, but this is the only issue.
     */
    @FXML
    private void handleZoom() {
        double x = cellSizeSlider.getValue();
        double newValue = 0.2 * Math.exp(0.05 * x);
        if ((newValue) * activeBoard.getArrayLength() > canvas.getHeight()
                && (newValue) * activeBoard.getArrayLength(0) > canvas.getWidth()) {

            handleGridSpacingSlider();

            calcNewOffset(activeBoard.getCellSize(), newValue);
            activeBoard.setCellSize(newValue);
        } else {
            cellSizeSlider.setValue(20 * Math.log(5 * activeBoard.getCellSize()));
        }

        draw();
    }

    @FXML
    private void handleGridSpacingSlider() {
        double x = cellSizeSlider.getValue();
        activeBoard.setGridSpacing(0.2 * Math.exp(0.05 * x) * gridSpacingSlider.getValue() / 100);
        draw();
    }

    @FXML
    private void handleColor() {
        //TODO
        cellColor = cellCP.getValue();
        backgroundColor = backgroundCP.getValue();
        draw();
    }

    @FXML
    private void handleClearBtn() {
        activeBoard.clearBoard();
        timeline.pause();
        startPauseBtn.setText("Start game");
        draw();
    }

    @FXML
    private void handleImportFileBtn() {
        try {
            FileChooser fileChooser = new FileChooser();

            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Game of Life Files", "*.rle", "*.lif", "*.cells"),
                    new FileChooser.ExtensionFilter("All Files", "*.*"));

            timeline.pause();
            File selected = fileChooser.showOpenDialog(null);
            if (selected != null) {
                boardFromFile = ReadFile.readFileFromDisk(selected.toPath());
                Alert alert = new Alert(AlertType.NONE);
                alert.setTitle("Place pattern");
                alert.initStyle(StageStyle.UTILITY);
                alert.setContentText("How do you want to insert the pattern?");
                ButtonType btnGhostTiles = new ButtonType("Insert with ghost tiles");
                ButtonType btnInsert = new ButtonType("Insert at top-left");

                alert.getButtonTypes().addAll(btnGhostTiles, btnInsert);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == btnInsert) {
                    KeyFrame keyframe = new KeyFrame(Duration.millis(1000), (ActionEvent event) -> {
                        drawGhostTiles();
                    });
                    timeline.getKeyFrames().add(keyframe);
                    activeBoard.insertArray(boardFromFile, 1, 1);
                    boardFromFile = null;
                    timeline.getKeyFrames().remove(keyframe);
                }

                activeBoard.setGameRule(ReadFile.getParsedRule());
                draw();
            }

        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "There was an error reading the file");
            alert.setTitle("Error");
            alert.setHeaderText("Reading File Error");
            alert.showAndWait();
        } catch (PatternFormatException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.setTitle("Error");
            alert.setHeaderText("Pattern Error");
            alert.showAndWait();

        }
    }

    @FXML
    public void openPatternEditor() throws IOException {
        timeline.pause();

        Stage editor = new Stage();
        FXMLLoader root = new FXMLLoader(getClass().getResource("/gol/s305089/view/PatternEditor.fxml"));

        Scene scene = new Scene((Parent) root.load());
        EditorController editorController = root.<EditorController>getController();
        editorController.setActiveBoard(activeBoard);

        editor.setScene(scene);
        editor.setTitle("Pattern Editor");
        editor.initModality(Modality.APPLICATION_MODAL);
        editor.show();
    }

    public void currentBoardToGIF() throws IOException {
        timeline.pause();

        Stage gifMaker = new Stage();
        FXMLLoader root = new FXMLLoader(getClass().getResource("/gol/s305089/view/GifMaker.fxml"));

        Scene scene = new Scene((Parent) root.load());

        GifMakerController gifcontroller = root.<GifMakerController>getController();
        gifcontroller.setByteBoard(activeBoard);

        gifMaker.setScene(scene);
        gifMaker.setTitle("Generate GIF - Game of Life");
        gifMaker.initModality(Modality.APPLICATION_MODAL);
        gifMaker.setMaxHeight(600.00);
        gifMaker.show();
    }

    @FXML
    public void showStats() throws IOException {
        timeline.pause();

        Stage golStats = new Stage();
        FXMLLoader root = new FXMLLoader(getClass().getResource("/gol/s305089/view/Stats.fxml"));

        Scene scene = new Scene((Parent) root.load());

        StatsController statsController = root.<StatsController>getController();
        statsController.setByteBoard(activeBoard);

        golStats.setScene(scene);
        golStats.setTitle("Stats - Game of Life");

        golStats.show();
    }

    private void rotateBoardFromFile() {
        if (boardFromFile != null) {
            boardFromFile = usefullMethods.rotateArray90Deg(boardFromFile);
        }
    }

    public void constructRule(byte[] cellsToSurvive, byte[] cellsToBeBorn) {
        try {
            activeBoard.setGameRule(new CustomRule(cellsToSurvive, cellsToBeBorn));
        } catch (unsupportedRuleException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.setTitle("Error");
            alert.setHeaderText("Your given rule is not supported. \n Try again.");
            alert.showAndWait();
        }
    }

    public void setActiveBoard(Board activeBoard) {
        this.activeBoard = activeBoard;
    }

    public Board getActiveBoard() {
        return activeBoard;
    }

    /**
     * Initiates all relevant MouseEvents.
     */
    private void mouseInit() {

        //Registers clicks on scene
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
                (MouseEvent e) -> {
                    if (boardFromFile != null) {
                        activeBoard.insertArray(boardFromFile, (int) ((mousePositionY - moveGridValues[1]) / (activeBoard.getGridSpacing() + activeBoard.getCellSize())),
                                (int) ((mousePositionX - moveGridValues[0]) / (activeBoard.getGridSpacing() + activeBoard.getCellSize())));
                        boardFromFile = null;

                        draw();
                    } else {
                        handleMouseClick(e);
                    }
                });
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                (MouseEvent e) -> {
                    if (rbMoveGrid.isSelected()) {
                        moveGrid(e);
                    } else {
                        handleMouseClick(e);
                    }
                });
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED,
                (MouseEvent e) -> {
                    if (rbMoveGrid.isSelected()) {
                        moveGridValues[2] = -Double.MAX_VALUE;
                        moveGridValues[3] = -Double.MAX_VALUE;
                    }
                });

        canvas.addEventHandler(MouseEvent.MOUSE_MOVED,
                (MouseEvent e) -> {
                    if (boardFromFile != null) {

                        mousePositionX = (int) e.getX();
                        mousePositionY = (int) e.getY();
                        draw();
                        //TODO SUPPORT FOR OFFSET++
                        drawGhostTiles();
                    }
                });

        //TODO Scroll in at mouse position
        canvas.setOnScroll((ScrollEvent event) -> {

            if (event.getDeltaY() > 0) {
                cellSizeSlider.increment();
            } else {
                cellSizeSlider.decrement();
            }
        });
    }

    /**
     * //TODO Fix Comments QUICK NOTE: Draws the grid. First decide where to
     * draw based on size and gridspacing, then calculates to draw in the middle
     * of gridspcaing (see - halfGridSpace) after this is done it adds the
     * offset
     */
    private void drawGrid() {
        gc.setFill(Color.BLUE);
        //TODO Så den ikke tegner det som er utenfor det vi ser
        double sizeAndSpacing = activeBoard.getCellSize() + activeBoard.getGridSpacing();
        double halfGridSpace = activeBoard.getGridSpacing() / 2;
        for (int i = 0; i <= activeBoard.getArrayLength(); i++) {
            gc.strokeLine((i * sizeAndSpacing - halfGridSpace) + moveGridValues[0], 0,
                    (i * sizeAndSpacing - halfGridSpace) + moveGridValues[0], canvas.getHeight());

            for (int j = 0; j <= activeBoard.getArrayLength(i); j++) {

                gc.strokeLine(0, (j * sizeAndSpacing - halfGridSpace) + moveGridValues[1],
                        canvas.getWidth(), (j * sizeAndSpacing - halfGridSpace) + moveGridValues[1]);

            }
        }

    }

    private void draw() {
        gc.setGlobalAlpha(1);
        gc.setFill(backgroundColor);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(cellColor);
        for (int i = 1; i < activeBoard.getArrayLength(); i++) {
            if (canvas.getHeight() < i * activeBoard.getCellSize() + i * activeBoard.getGridSpacing()) {
                //TODO Så den ikke tegner det som er utenfor
            }
            for (int j = 1; j < activeBoard.getArrayLength(i); j++) {
                if (activeBoard.getCellState(i, j)) {
                    if (canvas.getWidth() < j * activeBoard.getCellSize() + j * activeBoard.getGridSpacing()) {
                        //TODO Så den ikke tegner det som er utenfor
                    }
                    gc.fillRect(j * activeBoard.getCellSize() + j * activeBoard.getGridSpacing() + moveGridValues[0],
                            i * activeBoard.getCellSize() + i * activeBoard.getGridSpacing() + moveGridValues[1],
                            activeBoard.getCellSize(),
                            activeBoard.getCellSize());
                }
            }
        }

    }

    private void drawGhostTiles() {
        if (boardFromFile != null) {
            gc.setFill(cellColor);
            for (int j = 0; j < boardFromFile.length; j++) {
                for (int i = 0; i < boardFromFile[j].length; i++) {
                    if (boardFromFile[j][i] == 64) {

                        gc.setGlobalAlpha(1);
                        gc.setFill(backgroundColor);
                        gc.fillRect(mousePositionX + i * activeBoard.getCellSize() + i * activeBoard.getGridSpacing(),
                                mousePositionY + j * activeBoard.getCellSize() + j * activeBoard.getGridSpacing(),
                                activeBoard.getCellSize(),
                                activeBoard.getCellSize());
                        gc.setFill(cellColor);

                        gc.setGlobalAlpha(0.5);
                        gc.fillRect(mousePositionX + i * activeBoard.getCellSize() + i * activeBoard.getGridSpacing(),
                                mousePositionY + j * activeBoard.getCellSize() + j * activeBoard.getGridSpacing(),
                                activeBoard.getCellSize(),
                                activeBoard.getCellSize());
                    }
                }
            }
        }

    }

    //Over complicated for the sake of smoothness, this code may have huge potensial for improvement. 
    private void moveGrid(MouseEvent e) {
        if (moveGridValues[2] == -Double.MAX_VALUE && moveGridValues[3] == -Double.MAX_VALUE) {
            moveGridValues[2] = e.getX();
            moveGridValues[3] = e.getY();
        } else {
            if (moveGridValues[0] + e.getX() - moveGridValues[2] < 0) {
                if (moveGridValues[1] + e.getY() - moveGridValues[3] < 0) {

                    moveGridValues[0] += e.getX() - moveGridValues[2]; //Offset x = x position - old y
                    moveGridValues[1] += e.getY() - moveGridValues[3]; //Offset y = y position - old y
                } else {
                    moveGridValues[1] = 0;
                    moveGridValues[0] += e.getX() - moveGridValues[2]; //Offset x = x position - old y
                }
            } else {
                moveGridValues[0] = 0;
                if (moveGridValues[1] + e.getY() - moveGridValues[3] < 0) {
                    moveGridValues[1] += e.getY() - moveGridValues[3]; //Offset y = y position - old y
                }

            }
            moveGridValues[2] = e.getX();
            moveGridValues[3] = e.getY();
        }
        draw();
    }

    /**
     *
     * @param e
     */
    private void handleMouseClick(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        if (rbRemoveCell.isSelected()) {
            activeBoard.setCellState(y, x, false, moveGridValues[0], moveGridValues[1]);
        } else if (rbMoveGrid.isSelected()) {
        } else {
            activeBoard.setCellState(y, x, true, moveGridValues[0], moveGridValues[1]);

        }
        draw();
    }

    //Does not calc gridspacing yet.
    private void calcNewOffset(double cellSize, double newCellSize) {
        double gridSpace = activeBoard.getGridSpacing();
        if (cellSize != 0) {

            double oldx = (canvas.getWidth() / 2 - moveGridValues[0]) / (cellSize);
            double oldy = (canvas.getHeight() / 2 - moveGridValues[1]) / (cellSize);

            moveGridValues[0] = -(oldx * (newCellSize) - canvas.getWidth() / 2);
            moveGridValues[1] = -(oldy * (newCellSize) - canvas.getHeight() / 2);

            moveGridValues[0] = (moveGridValues[0] > 0) ? 0 : moveGridValues[0];
            moveGridValues[1] = (moveGridValues[1] > 0) ? 0 : moveGridValues[1];

        }

    }
}
