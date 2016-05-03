package gol.controller;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
import gol.model.Board.DynamicBoard;
import gol.model.FileIO.PatternFormatException;
import gol.model.FileIO.ReadFile;
import gol.model.Logic.ConwaysRule;
import gol.model.Logic.CustomRule;
import gol.model.Logic.Rule;
import gol.model.Logic.unsupportedRuleException;
import gol.other.Configuration;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
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
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyEvent;
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
 * FXML controller, controls the main game-screen.
 *
 * @author s305054, s305084, s305089
 */
public class GameController implements Initializable {

    @FXML
    private Canvas canvas;
    @FXML
    private BorderPane borderpane;
    @FXML
    private TabPane tabPane;
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
    private Button btnStartPause;
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

    private Color cellColor;
    private Color backgroundColor;
    private byte[][] boardFromFile;
    private int mousePositionX;
    private int mousePositionY;
    private long gencount = 0;
    private AnimationTimer animationTimer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc = canvas.getGraphicsContext2D();

        canvas.widthProperty().bind(borderpane.widthProperty());
        canvas.heightProperty().bind(borderpane.heightProperty().subtract(toolBarQuickStats.heightProperty()));
        tabPane.prefHeightProperty().bind(borderpane.heightProperty());
        toolBarQuickStats.prefWidthProperty().bind(borderpane.widthProperty());
        borderpane.widthProperty().addListener(e -> draw());
        borderpane.heightProperty().addListener(e -> draw());

        cellSizeSlider.setBlockIncrement(0.75);

        //TODO Valg for Array eller dynamisk brett
        if (Configuration.getProp("dynamicBoard").equals("true")) {
            activeBoard = new DynamicBoard();
        } else {
            activeBoard = new ArrayBoard();
        }
        cellCP.setValue(Color.web("#000000"));
        backgroundCP.setValue(Color.web("#9CB5D3"));

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
            if (Configuration.getProp("useThreads").equals("true") && activeBoard instanceof DynamicBoard) {
                activeBoard.nextGenConcurrent();
            } else {
                activeBoard.nextGen();
            }
            gencount++;
            labelGenCount.setText("Generation: " + gencount);
        });
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.getKeyFrames().add(keyframe);

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                draw();
            }
        };

    }

    @FXML
    private void handleAnimation() {
        if (timeline.getStatus() == Status.RUNNING) {
            timeline.pause();
            animationTimer.stop();
            btnStartPause.setText("Start game");
        } else {
            timeline.play();
            animationTimer.start();
            btnStartPause.setText("Pause game");
        }
    }

    @FXML
    private void handleAnimationSpeedSlider() {
        double animationSpeed = animationSpeedSlider.getValue();
        timeline.setRate(animationSpeed);
        animationSpeedLabel.setText(String.format("%.2f %s", animationSpeed, " "));
    }

    @FXML
    private void handleRuleBtn() {
        byte[] toBeBorn;
        byte[] toSurvive;

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
     * Known issue on array board: If you decrease gridspacing while zoomed out.
     */
    @FXML
    private void handleZoom() {
        double x = cellSizeSlider.getValue();
        //Formula for smooth zoom. Found with geogebra.
        double newValue = 0.2 * Math.exp(0.05 * x);

        if (((newValue) * activeBoard.getArrayLength() > canvas.getHeight()
                && (newValue) * activeBoard.getMaxRowLength() > canvas.getWidth())
                || activeBoard instanceof DynamicBoard) {
            handleGridSpacingSlider();

            if (cellSizeSlider.isFocused()) {
                //Zoom on center
                calcNewOffset(activeBoard.getCellSize(), newValue);
            } else {
                //Zoom on mouse
                calcNewOffsetMouse(activeBoard.getCellSize(), newValue);
            }
            activeBoard.setCellSize(newValue);
        } else {
            //Calculates to max slider value
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
        activeBoard.offsetValues[0] = 0;
        activeBoard.offsetValues[1] = 0;

        timeline.pause();
        btnStartPause.setText("Start game");
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
                showInsertDialog();

            }

        } catch (IOException ex) {
            UsefullMethods.showErrorAlert("Reading File Error", "There was an error reading the file");
        } catch (PatternFormatException ex) {
            UsefullMethods.showErrorAlert("Pattern Error", ex.getMessage());
        }
    }

    @FXML
    private void handleImportInternet() {
        Alert mainDialog = new Alert(AlertType.NONE);
        mainDialog.setTitle("Import from internet");
        mainDialog.initStyle(StageStyle.UTILITY);
        mainDialog.setContentText("Type the url of the pattern you want to import");

        VBox container = new VBox();
        TextField input = new TextField("http://");
        container.getChildren().add(input);

        mainDialog.getDialogPane().setExpandableContent(container);
        mainDialog.getDialogPane().setExpanded(true);

        ButtonType btnInsert = new ButtonType("Insert");
        ButtonType btnCancel = new ButtonType("Cancel");
        mainDialog.getButtonTypes().addAll(btnInsert, btnCancel);

        Optional<ButtonType> result = mainDialog.showAndWait();

        if (result.get() == btnInsert) {
            try {
                boardFromFile = ReadFile.readFromURL(input.getText());
                showInsertDialog();
            } catch (IOException ex) {
                UsefullMethods.showErrorAlert("Reading File Error", "There was an error reading your file, please check your internet connection.");
            } catch (PatternFormatException ex) {
                UsefullMethods.showErrorAlert("Pattern Error", ex.getMessage());
            }
        }

    }

    private void showInsertDialog() throws PatternFormatException, IOException {

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
            updateRules();
        } else if (result.get() == btnGhostTiles) {
            btnStartPause.setDisable(true);
            updateRules();
        } else {
            boardFromFile = null;
            alert.close();
        }
        draw();
    }

    /**
     * Do not call if a new pattern has not been parsed. If no rule was parsed
     * Conway's rule will be set as new rule.
     */
    private void updateRules() {
        Rule newRule = ReadFile.getParsedRule();
        activeBoard.setGameRule(newRule);
        rbCustomGameRules.fire();

        String born = "";
        String surv = "";

        for (byte b : newRule.getToBorn()) {
            born += b + " ";
        }

        for (byte s : newRule.getSurvive()) {
            surv += s + " ";
        }
        tfCellsToBeBorn.setText(born);
        tfCellsToSurvive.setText(surv);
    }

    @FXML
    private void rotateBoardFromFile() {
        if (boardFromFile != null) {
            boardFromFile = UsefullMethods.rotateArray90Deg(boardFromFile);
        }
    }

    private void constructRule(byte[] cellsToSurvive, byte[] cellsToBeBorn) {
        try {
            activeBoard.setGameRule(new CustomRule(cellsToSurvive, cellsToBeBorn));
        } catch (unsupportedRuleException ex) {
            UsefullMethods.showErrorAlert("Oops!", "Your given rule is not supported. \n Try again.");
        }
    }

    /**
     * Sets current board as the given board.
     *
     * @param activeBoard Board to set.
     */
    public void setActiveBoard(Board activeBoard) {
        this.activeBoard = activeBoard;
    }

    /**
     * Initiates all relevant MouseEvents.
     */
    private void mouseInit() {

        //Registers clicks on scene
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
                (MouseEvent e) -> {
                    if (boardFromFile != null) {
                        btnStartPause.setDisable(false);
                        activeBoard.insertArray(boardFromFile, (int) ((mousePositionY - activeBoard.offsetValues[1]) / (activeBoard.getGridSpacing() + activeBoard.getCellSize())),
                                (int) ((mousePositionX - activeBoard.offsetValues[0]) / (activeBoard.getGridSpacing() + activeBoard.getCellSize())));
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
                    //Indicates that mouse has been released
                    activeBoard.offsetValues[2] = -Double.MAX_VALUE;
                    activeBoard.offsetValues[3] = -Double.MAX_VALUE;
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
        double cellSize = activeBoard.getCellSize();
        double gridSpacing = activeBoard.getGridSpacing();
        int startRow;
        int startCol;

        if (activeBoard.offsetValues[1] > cellSize) {
            startRow = 1;
        } else {
            startRow = -(int) (activeBoard.offsetValues[1] / (cellSize + gridSpacing));
        }

        if (activeBoard.offsetValues[0] > cellSize) {
            startCol = 1;
        } else {
            startCol = -(int) (activeBoard.offsetValues[0] / (cellSize + gridSpacing));
        }

        int endRow = (int) Math.ceil(canvas.getHeight() / (cellSize + gridSpacing)) + startRow;
        int endCol = (int) Math.ceil(canvas.getWidth() / (cellSize + gridSpacing)) + startCol;
        endRow = (endRow < activeBoard.getArrayLength()) ? endRow : activeBoard.getArrayLength();

        gc.setGlobalAlpha(1);
        gc.setFill(backgroundColor);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(cellColor);
        drawBorder();
        for (int i = startRow; i < endRow; i++) {
            for (int j = startCol; j < endCol && j < activeBoard.getArrayLength(i); j++) {
                if (activeBoard.getCellState(i, j)) {
                    gc.fillRect(j * cellSize + j * gridSpacing + activeBoard.offsetValues[0],
                            i * cellSize + i * gridSpacing + activeBoard.offsetValues[1],
                            cellSize, cellSize);
                }
            }
        }

    }

    private void drawBorder() {
        gc.fillRect(activeBoard.offsetValues[0], activeBoard.offsetValues[1], activeBoard.getCellSize() * activeBoard.getMaxRowLength(), activeBoard.getCellSize());
        gc.fillRect(activeBoard.offsetValues[0], activeBoard.offsetValues[1], activeBoard.getCellSize(), activeBoard.getCellSize() * activeBoard.getArrayLength());

        gc.fillRect(activeBoard.offsetValues[0], activeBoard.offsetValues[1] + activeBoard.getCellSize() * activeBoard.getArrayLength(), activeBoard.getCellSize() * activeBoard.getMaxRowLength(), activeBoard.getCellSize());
        gc.fillRect(activeBoard.offsetValues[0] + activeBoard.getCellSize() * activeBoard.getMaxRowLength(), activeBoard.offsetValues[1], activeBoard.getCellSize(), activeBoard.getCellSize() * activeBoard.getArrayLength());
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

    /**
     * Calculates new offset based on mouse movement, then draws the board
     * again.
     */
    private void moveGrid(MouseEvent e) {
        if (activeBoard.offsetValues[2] == -Double.MAX_VALUE && activeBoard.offsetValues[3] == -Double.MAX_VALUE) {
            //If mouse was just pressed, set old values to current X and Y.
            activeBoard.offsetValues[2] = e.getX();
            activeBoard.offsetValues[3] = e.getY();
        } else {

            double newXoffset = activeBoard.offsetValues[0] + e.getX() - activeBoard.offsetValues[2];
            double newYoffset = activeBoard.offsetValues[1] + e.getY() - activeBoard.offsetValues[3];

            //If dynamic, it should not restrict movement outside of board.
            if (activeBoard instanceof DynamicBoard) {
                activeBoard.offsetValues[0] = newXoffset;
                activeBoard.offsetValues[1] = newYoffset;
            } else {
                //Negative beacuse offset on arrayboard always is negative
                //max values is for right and bottom sides
                double maxValueX = -((activeBoard.getCellSize() + activeBoard.getGridSpacing()) * activeBoard.getArrayLength() - canvas.getWidth());
                double maxValueY = -((activeBoard.getCellSize() + activeBoard.getGridSpacing()) * activeBoard.getMaxRowLength() - canvas.getHeight());

                //If positive, board should not move
                if (newXoffset < 0) {
                    if (newXoffset > maxValueX) {
                        activeBoard.offsetValues[0] = newXoffset;
                    } else {
                        activeBoard.offsetValues[0] = maxValueX;
                    }

                } else {
                    activeBoard.offsetValues[0] = 0;
                }
                if (newYoffset < 0) {
                    if (newYoffset > maxValueY) {
                        activeBoard.offsetValues[1] = newYoffset;
                    } else {
                        activeBoard.offsetValues[1] = maxValueY;
                    }

                } else {
                    activeBoard.offsetValues[1] = 0;
                }
            }

            activeBoard.offsetValues[2] = e.getX();
            activeBoard.offsetValues[3] = e.getY();
        }
        draw();
    }

    /**
     * Updates the screen according to event and radio-button selected.
     */
    private void handleMouseClick(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        if (rbMoveGrid.isSelected() || e.isMiddleButtonDown()) {
            moveGrid(e);
        } else if (rbRemoveCell.isSelected() ^ e.isSecondaryButtonDown()) {
            activeBoard.setCellState(y, x, false, activeBoard.offsetValues[0], activeBoard.offsetValues[1]);
        } else {
            activeBoard.setCellState(y, x, true, activeBoard.offsetValues[0], activeBoard.offsetValues[1]);
        }

        draw();
    }

    public void handleKeyEvents(KeyEvent e) {
        btnStartPause.requestFocus();
        String key = e.getText();
        switch (key) {
            case "f":
                if (boardFromFile != null) {
                    boardFromFile = UsefullMethods.transposeMatrix(boardFromFile);
                }
                break;

            case "r":
                if (boardFromFile != null) {
                    boardFromFile = UsefullMethods.rotateArray90Deg(boardFromFile);
                }
                break;

            case "c":
                handleClearBtn();
                break;

            case "k":
                handleAnimation();
                break;
            case "q":
                rbRemoveCell.fire();
                break;
            case "a":
                //TODO set addcells
                break;
            case "i":
                handleImportFileBtn();
                break;

        }
        draw();
        drawGhostTiles();
    }

    /**
     * When cellsize is changed, this methods is called. It then calculates a new
     * offset with the new cellsize. The offset is based at the center of
     * canvas.
     *
     * NB: Does not support gridspacing yet.
     */
    private void calcNewOffset(double cellSize, double newCellSize) {
        if (cellSize != 0) {

            double oldx = (canvas.getWidth() / 2 - activeBoard.offsetValues[0]) / (cellSize);
            double oldy = (canvas.getHeight() / 2 - activeBoard.offsetValues[1]) / (cellSize);

            activeBoard.offsetValues[0] = -(oldx * (newCellSize) - canvas.getWidth() / 2);
            activeBoard.offsetValues[1] = -(oldy * (newCellSize) - canvas.getHeight() / 2);

            updateOffsetValues(newCellSize);
        }
    }

    /**
     *
     * Only differs from {@link #calcNewOffset(double, double) } where center is
     * defined by mouse position. -> Calculates the center as mouse position.
     */
    private void calcNewOffsetMouse(double cellSize, double newCellSize) {
        if (cellSize != 0) {
            double oldx = (mousePositionX - activeBoard.offsetValues[0]) / (cellSize);
            double oldy = (mousePositionY - activeBoard.offsetValues[1]) / (cellSize);

            activeBoard.offsetValues[0] = -(oldx * (newCellSize) - mousePositionX);
            activeBoard.offsetValues[1] = -(oldy * (newCellSize) - mousePositionY);

            updateOffsetValues(newCellSize);
        }
    }

    /**
     * Should only be called from calcNewOffset or calcNewOffsetMouse.
     */
    private void updateOffsetValues(double newCellSize) {
        if (!(activeBoard instanceof DynamicBoard)) {
            double maxvalueX = -(newCellSize * activeBoard.getArrayLength() - canvas.getWidth());
            double maxvalueY = -(newCellSize * activeBoard.getMaxRowLength() - canvas.getHeight());

            activeBoard.offsetValues[0] = (activeBoard.offsetValues[0] > 0) ? 0 : activeBoard.offsetValues[0];
            activeBoard.offsetValues[1] = (activeBoard.offsetValues[1] > 0) ? 0 : activeBoard.offsetValues[1];

            activeBoard.offsetValues[0] = (activeBoard.offsetValues[0] < maxvalueX) ? maxvalueX : activeBoard.offsetValues[0];
            activeBoard.offsetValues[1] = (activeBoard.offsetValues[1] < maxvalueY) ? maxvalueY : activeBoard.offsetValues[1];
        }
    }
}
