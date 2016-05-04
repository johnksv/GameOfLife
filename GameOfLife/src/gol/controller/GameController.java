package gol.controller;

import gol.model.UsefullMethods;
import gol.model.Board.*;
import gol.model.FileIO.*;
import gol.model.Logic.*;
import gol.svergja.controller.*;
import gol.other.Configuration;
import gol.vang.model.GIFWriterS305054;
import gol.vang.controller.EditorController;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
    private RadioButton rbAddCell;
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
    private Slider s54timeSliderGif;
    @FXML
    private Label s54timeLabelGif;
    @FXML
    private TextField s54nPicturesGIF;
    @FXML
    private CheckBox cbDrawBox;

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
        cbDrawBox.selectedProperty().addListener(e -> draw());

        cellSizeSlider.setBlockIncrement(0.75);
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
                activeBoard.setRule(new ConwaysRule());
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
            activeBoard.insertArray(boardFromFile);
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
        activeBoard.setRule(newRule);
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
    private void s54handleGIF() {
        timeline.pause();
        String sPictures = s54nPicturesGIF.getText();
        int pictures = Integer.parseInt(sPictures.replaceAll("\\D", ""));

        GIFWriterS305054 gifTrygve = new GIFWriterS305054();
        java.awt.Color bgColor = new java.awt.Color((float) backgroundColor.getRed(), (float) backgroundColor.getGreen(), (float) backgroundColor.getBlue());
        java.awt.Color cColor = new java.awt.Color((float) cellColor.getRed(), (float) cellColor.getGreen(), (float) cellColor.getBlue());

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("GIF", "*.gif"));
        fileChooser.setTitle("Save as GIF");

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            gifTrygve.setCellSize((int) activeBoard.getCellSize());
            gifTrygve.setPictures(pictures);
            gifTrygve.setBoard(activeBoard);
            gifTrygve.setTime(s54timeSliderGif.getValue());
            gifTrygve.setColor(bgColor, cColor);
            gifTrygve.prepareGIF(file.toPath());
            gifTrygve.makeGIF();
        }
    }

    @FXML
    private void s54handleGifSlider() {
        s54timeLabelGif.setText("Time Between Pictures: " + String.format("%.3f %s", s54timeSliderGif.getValue(), "s"));
    }

    @FXML
    private void s54openEditor() {
        timeline.pause();
        try {
            Stage editor = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gol/vang/view/Editor.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            EditorController edController = loader.getController();
            edController.setBoard(activeBoard);

            editor.setTitle("Pattern Editor");
            editor.initModality(Modality.WINDOW_MODAL);
            editor.initOwner(borderpane.getScene().getWindow());
            editor.setScene(scene);

            editor.show();

        } catch (IOException ie) {
            Alert error = new Alert(AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText("Could not open pattern editor. Please try again.");
            error.show();

        }
    }

    @FXML
    private void s89openPatternEditor() throws IOException {
        timeline.pause();

        Stage editor = new Stage();
        FXMLLoader root = new FXMLLoader(getClass().getResource("/gol/svergja/view/PatternEditor.fxml"));

        Scene scene = new Scene((Parent) root.load());
        PatternEditorController editorController = root.<PatternEditorController>getController();
        editorController.setBoard(activeBoard);
        editorController.setGameController(this);

        editor.setTitle("Pattern Editor - Game of Life");
        editor.initModality(Modality.WINDOW_MODAL);
        editor.setMinWidth(800);
        editor.setMinHeight(600);
        editor.initOwner(borderpane.getScene().getWindow());
        editor.setScene(scene);
        editor.showAndWait();
        draw();
    }

    @FXML
    private void s89saveAsGIF() throws IOException {
        timeline.pause();

        Stage gifMaker = new Stage();
        FXMLLoader root = new FXMLLoader(getClass().getResource("/gol/svergja/view/GifMaker.fxml"));

        Scene scene = new Scene((Parent) root.load());

        GifMakerController gifcontroller = root.<GifMakerController>getController();
        gifcontroller.setBoard(activeBoard);

        gifMaker.setScene(scene);
        gifMaker.setTitle("Generate GIF - Game of Life");
        gifMaker.setWidth(215);
        gifMaker.setHeight(535);
        gifMaker.initOwner(borderpane.getScene().getWindow());
        gifMaker.show();
    }

    @FXML
    private void s89showStats() throws IOException {
        timeline.pause();

        Stage golStats = new Stage();
        FXMLLoader root = new FXMLLoader(getClass().getResource("/gol/svergja/view/Stats.fxml"));

        Scene scene = new Scene((Parent) root.load());

        StatsController statsController = root.<StatsController>getController();
        statsController.setBoard(activeBoard);

        golStats.setScene(scene);
        golStats.setTitle("Stats - Game of Life");
        golStats.initOwner(borderpane.getScene().getWindow());
        golStats.show();
    }

    @FXML
    private void s89showSoundController() throws IOException {
        timeline.pause();

        Stage golAudio = new Stage();
        FXMLLoader root = new FXMLLoader(getClass().getResource("/gol/svergja/view/Sound.fxml"));
        Scene scene = new Scene((Parent) root.load());

        SoundController soundController = root.<SoundController>getController();
        soundController.setBoard(activeBoard);

        KeyFrame soundFrame = new KeyFrame(Duration.millis(1000), (event) -> {
            soundController.playSound();
        });
        timeline.getKeyFrames().add(soundFrame);

        golAudio.setScene(scene);
        golAudio.setTitle("Audio control panel - Game of Life");
        golAudio.initOwner(borderpane.getScene().getWindow());
        golAudio.showAndWait();
        timeline.stop();
        timeline.getKeyFrames().remove(soundFrame);
        soundController.disposeMediaPlayers();
    }

    @FXML
    private void rotateBoardFromFile() {
        if (boardFromFile != null) {
            boardFromFile = UsefullMethods.rotateArray90Deg(boardFromFile);
        }
    }

    @FXML
    private void s84handleShowStats() {
        try {
            Stage statistics = new Stage();
            statistics.setResizable(false);
            statistics.getIcons().add(new Image(new File("src/mics/linechart.png").toURI().toString()));

            statistics.initModality(Modality.WINDOW_MODAL);
            statistics.initOwner(canvas.getScene().getWindow());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gol/s305084/view/Statistics.fxml"));

            Parent root = loader.load();
            gol.s305084.StatisticsController statisticsController = loader.getController();
            statisticsController.loadeBoard(activeBoard);
            statisticsController.showStats();
            Scene scene = new Scene(root);
            statistics.setScene(scene);

            statistics.setTitle("Gol: Statistics");
            statistics.show();
        } catch (IOException ex) {
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void s84handleEditor() {
        try {
            if (timeline.getStatus() == Status.RUNNING) {
                handleAnimation();
            }
            Stage editor = new Stage();
            editor.setResizable(false);
            editor.getIcons().add(new Image(new File("src/mics/icon.png").toURI().toString()));

            editor.initModality(Modality.WINDOW_MODAL);
            editor.initOwner(canvas.getScene().getWindow());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gol/s305084/view/Editor.fxml"));

            Parent root = loader.load();
            gol.s305084.PatternEditorController editorController = loader.getController();
            editorController.setBGColor(backgroundColor);
            editorController.setCellColor(cellColor);

            Scene scene = new Scene(root);
            editor.setScene(scene);

            editor.setTitle("Gol: Pattern Editor");
            editor.showAndWait();
            //TODO ask about this code(not 100% my own).

            boardFromFile = editorController.getPattern();
            if (boardFromFile != null) {
                Alert alert = new Alert(AlertType.NONE);
                alert.setTitle("Place pattern");
                alert.initStyle(StageStyle.UTILITY);
                alert.setContentText("How do you want to insert the pattern?");

                ButtonType btnGhostTiles = new ButtonType("Insert with ghost tiles");
                ButtonType btnInsert = new ButtonType("Insert at top-left");
                ButtonType btnCancel = new ButtonType("Cancel");

                alert.getButtonTypes().addAll(btnGhostTiles, btnInsert, btnCancel);

                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == btnInsert) {
                    activeBoard.insertArray(boardFromFile);
                    boardFromFile = null;
                } else if (result.get() == btnGhostTiles) {
                    btnStartPause.setDisable(true);
                    activeBoard.setRule(ReadFile.getParsedRule());
                } else {
                    boardFromFile = null;
                    alert.close();
                }
            }
            draw();

        } catch (IOException ex) {
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Constructs a custom rule with the given parameters.
     *
     * @param cellsToSurvive a array of survive values
     * @param cellsToBeBorn a array of to be born values
     */
    public void constructRule(byte[] cellsToSurvive, byte[] cellsToBeBorn) {
        try {
            activeBoard.setRule(new CustomRule(cellsToSurvive, cellsToBeBorn));
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
        if (cbDrawBox.isSelected()) {
            drawBorder();
        }
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
        double size = activeBoard.getCellSize() + activeBoard.getGridSpacing();
        gc.fillRect(activeBoard.offsetValues[0], activeBoard.offsetValues[1], size * activeBoard.getMaxRowLength(), size);
        gc.fillRect(activeBoard.offsetValues[0], activeBoard.offsetValues[1], size, size * activeBoard.getArrayLength());

        gc.fillRect(activeBoard.offsetValues[0], activeBoard.offsetValues[1] + size * activeBoard.getArrayLength(), size * activeBoard.getMaxRowLength(), size);
        gc.fillRect(activeBoard.offsetValues[0] + size * activeBoard.getMaxRowLength(), activeBoard.offsetValues[1], size, size * (activeBoard.getArrayLength() + 1));
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

    @FXML
    private void handleHowToPlay() {
        try {
            Stage howToPlay = new Stage();
            howToPlay.initOwner(canvas.getScene().getWindow());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gol/view/HowToPlay.fxml"));
            Parent root = loader.load();
            HowToPlayController mainControll = loader.getController();
            mainControll.loadStage(howToPlay);

            howToPlay.setScene(new Scene(root));
            howToPlay.show();
        } catch (IOException ex) {
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void handleKeyEvents(KeyEvent e) {
        btnStartPause.requestFocus();
        String key = e.getText();
        if (e.isShiftDown()) {
            switch (key) {
                case "a":
                    rbRemoveCell.fire();
                    break;
                case "i":
                    handleImportInternet();
                    break;
            }
        } else {
            switch (key) {
                case "a":
                    rbAddCell.fire();
                    break;
                case "b":
                    cbDrawBox.fire();
                    break;
                case "c":
                    handleClearBtn();
                    break;
                case "f":
                    if (boardFromFile != null) {
                        boardFromFile = UsefullMethods.transposeMatrix(boardFromFile);
                    }
                    break;
                case "h":
                    handleHowToPlay();
                    break;
                case "i":
                    handleImportFileBtn();
                    break;
                case "k":
                    handleAnimation();
                    break;
                case "r":
                    if (boardFromFile != null) {
                        boardFromFile = UsefullMethods.rotateArray90Deg(boardFromFile);
                    }
                    break;
            }
            draw();
            drawGhostTiles();
        }
    }

    /**
     * When cellsize is changed, this methods is called. It then calculates a
     * new offset with the new cellsize. The offset is based at the center of
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
