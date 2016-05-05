package gol.controller;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Controls "how to play" screen.
 *
 *
 * @author s305054, s305084, s305089
 */
public class HowToPlayController  {

    @FXML
    Button btnBack;
    

    /**
     * <p>
     * Loads the stage, and set stage values
     * </p>
     * <b>Stage values: </b> <br>
     * Resizable = false <br>
     * Title = How to play <br>
     * Icon = gen1.PNG <br>
     *
     * @param stage how to play stage
     */
    public void loadStage(Stage stage) {

        stage.setResizable(false);
        stage.getIcons().add(new Image(new File("src/misc/gen1.PNG").toURI().toString()));
        stage.setTitle("How to play");
    }

    @FXML
    private void handleBackBtn() {
        Stage stage = (Stage) btnBack.getScene().getWindow();
        stage.close();
    }
}
