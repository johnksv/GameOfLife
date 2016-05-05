package gol.controller;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
import gol.model.Board.DynamicBoard;
import gol.model.FileIO.PatternFormatException;
import gol.model.FileIO.ReadFile;
import gol.model.UsefullMethods;
import gol.other.Configuration;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Controls the splash screen. This class stores the stage.
 *
 * @author s305054, s305084, s305089
 */
public class MainController {

    private static List<String> input = new ArrayList<String>();
    private Stage primaryStage;

    /**
     * Sets the primaryStage that is shown. This method should only be called on
     * startup when javaFX start is run.
     *
     * @param stage The stage that is being displayed.
     */
    public void setStage(Stage stage) {
        primaryStage = stage;
        primaryStage.getIcons().add(new Image(new File("src/misc/gen1.PNG").toURI().toString()));
        primaryStage.setTitle("Game of Life");
        primaryStage.setResizable(false);
    }

    /**
     * Loads Game.fxml which sets {@link gol.controller.GameController} as its
     * controller. Will also
     * call{@link GameController#handleKeyEvents(javafx.scene.input.KeyEvent) setKeyEvents.}
     *
     */
    @FXML
    public void startGame() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gol/view/Game.fxml"));
            Parent root = loader.load();

            GameController gameController = loader.getController();
            Scene scene = new Scene(root);
            scene.setOnKeyPressed((KeyEvent e) -> {
                gameController.handleKeyEvents(e);
            });

            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.setMinWidth(850);
            primaryStage.setMinHeight(650);
        } catch (IOException ex) {
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void startGamePattern() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gol/view/Game.fxml"));
            Parent root = loader.load();

            GameController gameController = loader.getController();
            Scene scene = new Scene(root);
            scene.setOnKeyPressed((KeyEvent e) -> {
                gameController.handleKeyEvents(e);
            });

            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.setMinWidth(850);
            primaryStage.setMinHeight(650);

            //Insert starting pattern
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Game of Life Files", "*.rle", "*.cells"),
                    new FileChooser.ExtensionFilter("All Files", "*.*"));

            File selected = fileChooser.showOpenDialog(null);
            if (selected != null) {
                //init board from config.
                Board activeBoard;
                if (Configuration.getProp("dynamicBoard").equals("true")) {
                    activeBoard = new DynamicBoard();
                } else {
                    activeBoard = new ArrayBoard();
                }

                activeBoard.insertArray(ReadFile.readFileFromDisk(selected.toPath()));
                gameController.setActiveBoard(activeBoard);
            }

        } catch (PatternFormatException ex) {
            UsefullMethods.showErrorAlert("Pattern Error", ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Loads HowToPlay.fxml which sets
     * {@link gol.controller.HowToPlayController} as its controller.
     *
     * @throws IOException Reads a FXML file
     */
    @FXML
    private void howToPlay() throws IOException {
        try {
            Stage howToPlay = new Stage();
            howToPlay.initOwner(primaryStage.getScene().getWindow());

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

}
