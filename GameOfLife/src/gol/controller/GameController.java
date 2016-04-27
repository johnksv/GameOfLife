package gol.controller;

import gol.model.Board.Board;
import gol.model.Board.DynamicBoard;
import gol.model.FileIO.PatternFormatException;
import gol.model.FileIO.ReadFile;
import gol.model.Logic.ConwaysRule;
import gol.model.Logic.CustomRule;
import gol.model.Logic.unsupportedRuleException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
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
    private ToolBar toolBarQuickStats;
    @FXML
    private Slider cellSizeSlider;
    @FXML
    private Slider gridSpacingSlider;
    @FXML
    private Slider animationSpeedSlider;
    @FXML
    private Label animationSpeedLabel;
    @FXML
    private Label labelGenCount;
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

    private Board activeBoard;
    private final Timeline timeline = new Timeline();
    private GraphicsContext gc;

    private Color cellColor = Color.BLACK;
    private Color backgroundColor = Color.web("#F4F4F4");
    private byte[][] boardFromFile;
    private int mousePositionX;
    private int mousePositionY;
    private double[] moveGridValues;
    private long gencount = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc = canvas.getGraphicsContext2D();

        canvas.widthProperty().bind(borderpane.widthProperty());
        canvas.heightProperty().bind(borderpane.heightProperty().subtract(toolBarQuickStats.heightProperty()));
        toolBarQuickStats.prefWidthProperty().bind(borderpane.widthProperty());

        cellSizeSlider.setBlockIncrement(0.75);

        //TODO Valg for Array eller dynamisk brett
        //activeBoard = new ArrayBoard();
        activeBoard = new DynamicBoard();
        cellCP.setValue(Color.BLACK);
        backgroundCP.setValue(Color.web("#F4F4F4"));
        moveGridValues = activeBoard.getMoveGridValues();

        mouseInit();
        handleZoom();
        handleGridSpacingSlider();
        handleColor();
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
            activeBoard.nextGenConcurrent();
            gencount++;
            labelGenCount.setText("Generation: " + gencount);
            draw();
        });
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.getKeyFrames().add(keyframe);

    }

    @FXML
    private void handleAnimation() {

        if (timeline.getStatus() == Status.RUNNING) {
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
        if (((newValue) * activeBoard.getArrayLength() > canvas.getHeight()
                && (newValue) * activeBoard.getArrayLength(0) > canvas.getWidth()) || activeBoard instanceof DynamicBoard) {
            handleGridSpacingSlider();

            if (cellSizeSlider.isFocused()) {

                calcNewOffset(activeBoard.getCellSize(), newValue);
            } else {
                calcNewOffsetMouse(activeBoard.getCellSize(), newValue);
            }
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
        cellColor = cellCP.getValue();
        backgroundColor = backgroundCP.getValue();
        draw();
    }

    @FXML
    private void handleClearBtn() {
        gencount = 0;
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
                    new ExtensionFilter("Game of Life Files", "*.rle", "*.cells"),
                    new ExtensionFilter("All Files", "*.*"));

            timeline.pause();
            File selected = fileChooser.showOpenDialog(null);
            if (selected != null) {
                boardFromFile = ReadFile.readFileFromDisk(selected.toPath());
                Alert alert = new Alert(AlertType.NONE);
                alert.setTitle("Place pattern");
                alert.initStyle(StageStyle.UTILITY);
                alert.setContentText("How do you want to insert the pattern?");

                VBox container = new VBox();
                for (String line : ReadFile.getMetadata()) {
                    container.getChildren().addAll(new Label(line));
                }

                if (!container.getChildren().isEmpty()) {
                    alert.getDialogPane().setExpandableContent(container);
                    alert.getDialogPane().setExpanded(true);
                }

                ButtonType btnGhostTiles = new ButtonType("Insert with ghost tiles");
                ButtonType btnInsert = new ButtonType("Insert at top-left");
                ButtonType btnCancel = new ButtonType("Cancel");

                alert.getButtonTypes().addAll(btnGhostTiles, btnInsert, btnCancel);

                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == btnInsert) {
                    activeBoard.insertArray(boardFromFile, 1, 1);
                    boardFromFile = null;
                } else if (result.get() == btnGhostTiles) {
                    startPauseBtn.setDisable(true);
                    activeBoard.setGameRule(ReadFile.getParsedRule());
                } else {
                    boardFromFile = null;
                    alert.close();
                }
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
    private void rotateBoardFromFile() {
        if (boardFromFile != null) {
            boardFromFile = UsefullMethods.rotateArray90Deg(boardFromFile);
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
                        startPauseBtn.setDisable(false);
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
                    handleMouseClick(e);
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
                    mousePositionX = (int) e.getX();
                    mousePositionY = (int) e.getY();
                    if (boardFromFile != null) {

                        draw();
                        drawGhostTiles();
                    }
                });

        canvas.setOnScroll((ScrollEvent event) -> {
            canvas.requestFocus();
            if (event.getDeltaY() > 0) {
                cellSizeSlider.increment();
            } else {
                cellSizeSlider.decrement();
            }
        });
    }

    private void draw() {
        gc.setGlobalAlpha(1);
        gc.setFill(backgroundColor);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(cellColor);
        try {
            gc.fillRect(moveGridValues[0], moveGridValues[1], activeBoard.getCellSize() * activeBoard.getMaxRowLength(), activeBoard.getCellSize());
            gc.fillRect(moveGridValues[0], moveGridValues[1], activeBoard.getCellSize(), activeBoard.getCellSize() * activeBoard.getArrayLength());

            gc.fillRect(moveGridValues[0], moveGridValues[1] + activeBoard.getCellSize() * activeBoard.getArrayLength(), activeBoard.getCellSize() * activeBoard.getMaxRowLength(), activeBoard.getCellSize());
            gc.fillRect(moveGridValues[0] + activeBoard.getCellSize() * activeBoard.getMaxRowLength(), moveGridValues[1], activeBoard.getCellSize(), activeBoard.getCellSize() * activeBoard.getArrayLength());

        } catch (Exception e) {
        }
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

            double newXoffset = moveGridValues[0] + e.getX() - moveGridValues[2];
            double newYoffset = moveGridValues[1] + e.getY() - moveGridValues[3];
            if (activeBoard instanceof DynamicBoard) {
                moveGridValues[0] = newXoffset;
                moveGridValues[1] = newYoffset;

            } else {
                double maxValueX = -((activeBoard.getCellSize() + activeBoard.getGridSpacing()) * activeBoard.getArrayLength() - canvas.getWidth());
                double maxValueY = -((activeBoard.getCellSize() + activeBoard.getGridSpacing()) * activeBoard.getArrayLength(0) - canvas.getHeight());

                if (newXoffset < 0) {
                    if (newXoffset > maxValueX) {
                        moveGridValues[0] = newXoffset;
                    } else {
                        moveGridValues[0] = maxValueX;
                    }

                } else {
                    moveGridValues[0] = 0;
                }
                if (newYoffset < 0) {
                    if (newYoffset > maxValueY) {
                        moveGridValues[1] = newYoffset;
                    } else {
                        moveGridValues[1] = maxValueY;
                    }

                } else {
                    moveGridValues[1] = 0;
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

        if (rbMoveGrid.isSelected() || e.isMiddleButtonDown()) {
            moveGrid(e);
        } else if (rbRemoveCell.isSelected() ^ e.isSecondaryButtonDown()) {
            activeBoard.setCellState(y, x, false, moveGridValues[0], moveGridValues[1]);
        } else {
            activeBoard.setCellState(y, x, true, moveGridValues[0], moveGridValues[1]);
        }

        draw();
    }

    //Does not calc gridspacing yet.
    private void calcNewOffset(double cellSize, double newCellSize) {
        if (cellSize != 0) {

            double oldx = (canvas.getWidth() / 2 - moveGridValues[0]) / (cellSize);
            double oldy = (canvas.getHeight() / 2 - moveGridValues[1]) / (cellSize);

            moveGridValues[0] = -(oldx * (newCellSize) - canvas.getWidth() / 2);
            moveGridValues[1] = -(oldy * (newCellSize) - canvas.getHeight() / 2);

            if (!(activeBoard instanceof DynamicBoard)) {
                double maxvalueX = -(newCellSize * activeBoard.getArrayLength() - canvas.getWidth());
                double maxvalueY = -(newCellSize * activeBoard.getArrayLength(0) - canvas.getHeight());

                moveGridValues[0] = (moveGridValues[0] > 0) ? 0 : moveGridValues[0];
                moveGridValues[1] = (moveGridValues[1] > 0) ? 0 : moveGridValues[1];

                moveGridValues[0] = (moveGridValues[0] < maxvalueX) ? maxvalueX : moveGridValues[0];
                moveGridValues[1] = (moveGridValues[1] < maxvalueY) ? maxvalueY : moveGridValues[1];
            }
        }

    }

    private void calcNewOffsetMouse(double cellSize, double newCellSize) {
        if (cellSize != 0) {
            double oldx = (mousePositionX - moveGridValues[0]) / (cellSize);
            double oldy = (mousePositionY - moveGridValues[1]) / (cellSize);

            moveGridValues[0] = -(oldx * (newCellSize) - mousePositionX);
            moveGridValues[1] = -(oldy * (newCellSize) - mousePositionY);

            if (!(activeBoard instanceof DynamicBoard)) {
                double maxvalueX = -(newCellSize * activeBoard.getArrayLength() - canvas.getWidth());
                double maxvalueY = -(newCellSize * activeBoard.getArrayLength(0) - canvas.getHeight());

                moveGridValues[0] = (moveGridValues[0] > 0) ? 0 : moveGridValues[0];
                moveGridValues[1] = (moveGridValues[1] > 0) ? 0 : moveGridValues[1];

                moveGridValues[0] = (moveGridValues[0] < maxvalueX) ? maxvalueX : moveGridValues[0];
                moveGridValues[1] = (moveGridValues[1] < maxvalueY) ? maxvalueY : moveGridValues[1];
            }
        }

    }
}
