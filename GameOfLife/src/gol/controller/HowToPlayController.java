/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * FXML Controller, controls the how to play screen.
 *
 *
 * @author s305054, s305084, s305089
 */
public class HowToPlayController implements Initializable {

    private static Stage stage;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        stage.getIcons().add(new Image(new File("src\\mics\\gen1.PNG").toURI().toString()));
        stage.setTitle(" How to play");
    }

    /**
     * Loads StartScreen.fxml and set {@link gol.controller.MainController} as
     * its controller.
     *
     * @throws IOException
     */
    @FXML
    private void handleMainMenuBtn() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/gol/view/StartScreen.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(" Game of Life");
    }

    /**
     * Loads the primaryStage to be stored. The stage is stored as a static
     * private variable. Needs to be done first in {@link Main#start }. This is
     * because of {@link #initialize}.
     *
     * @param stage
     */
    public static void loadStage(Stage stage) {
        HowToPlayController.stage = stage;
    }

}
