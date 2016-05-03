package gol.controller;

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
import javafx.stage.Stage;

/**
 * FXML Controller, controls the splash screen. 
 * This class stores the stage.
 *
 * @author s305054, s305084, s305089
 */
public class MainController {

    private Stage primaryStage;
    private static List<String> input = new ArrayList<String>();

    /**
     * Loads Game.fxml witch sets {@link gol.controller.GameController} as its
     * controller. Will also call{@link GameController#handleKeyEvents(javafx.scene.input.KeyEvent) setKeyEvents.}
     *
     * @throws IOException Reads a FXML file
     */
    @FXML
    public void startGame() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gol/view/Game.fxml"));
        Parent root = loader.load();

        GameController gameController = loader.getController();
        Scene scene = new Scene(root);
        scene.setOnKeyPressed((KeyEvent e) -> {
            gameController.handleKeyEvents(e);
        });

        primaryStage.setScene(scene);
        primaryStage.setMinWidth(850);
        primaryStage.setMinHeight(650);

    }

    /**
     * Loads HowToPlay.fxml witch sets
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

    /**
     * Loads the primaryStage to be stored. The stage is stored as a private
     * variable. Sets icon and title.
     *
     * @param stage
     */
    public void loadStage(Stage stage) {
        primaryStage = stage;
        primaryStage.getIcons().add(new Image(new File("src/mics/gen1.PNG").toURI().toString()));
        primaryStage.setTitle("Game of Life");
    }

}
