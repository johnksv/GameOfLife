package gol.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * //TODO Javadoc
 *
 *
 * @author s305054, s305084, s305089
 */
public class HowToPlayController implements Initializable {

    @FXML
    Button btnBack;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    /**
     * Loads StartScreen.fxml and set {@link gol.controller.MainController} as
     * its controller.
     *
     * @throws IOException
     */
    @FXML
    private void handleBackBtn() throws IOException {
        Stage stage = (Stage) btnBack.getScene().getWindow();
        stage.close();
    }

    /**
     * Loads the stage, and sets stage values.
     *
     * @param stage
     */
    public void loadStage(Stage stage) {

        //TODO ICON HowToPlay
        stage.setResizable(false);
        stage.getIcons().add(new Image(new File("src\\mics\\gen1.PNG").toURI().toString()));
        stage.setTitle("How to play");
    }

}
