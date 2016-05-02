package gol.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * FXML Controller, controls the splash screen. This class is where the stage is
 * stored.
 *
 * @author s305054, s305084, s305089
 */
public class MainController implements Initializable {

    private static Stage primaryStage;
    private static List<String> input = new ArrayList<String>();

    /**
     * Sets title and game icon, whenever the program starts.
     *
     * @see javafx.fxml.Initializable
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        primaryStage.getIcons().add(new Image(new File("src\\mics\\gen1.PNG").toURI().toString()));
        primaryStage.setTitle(" Game of Life");
        primaryStage.setResizable(false);
    }

    /**
     * Loads Game.fxml witch sets {@link gol.controller.GameController} as its
     * controller. This method will also call {@link GameController#handleKeyEvents(javafx.scene.input.KeyEvent) setKeyEvents}.
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
     * Loads HowToPlay.fxml witch sets {@link gol.controller.HowToPlayController} 
     * as its controller.
     * 
     * @throws IOException Reads a FXML file
     */
    @FXML
    public void howToPlay() throws IOException {
        HowToPlayController.loadStage(primaryStage);
        Parent root = FXMLLoader.load(getClass().getResource("/gol/view/HowToPlay.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
    }

    /**
     * Loads the primaryStage to be stored. The stage is stored as a static
     * private variable. Needs to be done first in {@link Main#start }. This is
     * because of {@link #initialize}.
     *
     * @param stage
     */
    public static void loadStage(Stage stage) {
        primaryStage = stage;
    }
    
}
