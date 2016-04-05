package gol.controller;

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
 * FXML Controller, controls the splash screen. 
 * This class is where the stage is stored.
 *  
 * @author s305054, s305084, s305089
 */
public class MainController implements Initializable {

    private static Stage primaryStage;
    private static List<String> input = new ArrayList<String>();
    
    /**
     * Sets title and game icon, whenever the program starts.
     * @see javafx.fxml.Initializable
     * @param location
     * @param resources 
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //klarte ikke med path :(
        //primaryStage.getIcons().add(new Image("file:gen1.PNG"));
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("gen1.PNG")));
        primaryStage.setTitle(" Game of Life");
    }

    /**
     * Loads  Game.fxml witch sets {@link gol.controller.GameController} as its controller.
     * This method will also call {@link #setKeyEvents(Scene) }. 
     * @throws IOException Reads an FXML file
     */
    @FXML
    public void startGame() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/gol/view/Game.fxml"));
        Scene scene = new Scene(root);
        setKeyEvents(scene);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);

    }

    /**
     * Checks if the  the parameter is stored in the input list.
     * 
     * @see #setKeyEvents 
     * @param keyInput  String representation of a keyboard button 
     * @return Boolean
     */
    public static boolean inputContains(String keyInput) {
        return input.contains(keyInput);
    }

    /**
     * Loads the primaryStage to be stored.
     * The stage is stored as a static private variable.
     * Needs to be done first in {@link Main#start  }.
     * This is because of {@link #initialize}. 
     * @param stage
     */
    public static void loadStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     *  Listens and stores key events at the chosen scene. 
     *  This will store key presses as a string representation when pressed.
     *  It will also delete them when the key is released.
     *  Stores the Strings in a String list.
     *  
     * @see #inputContains(String) 
     * @param scene
     */
    public void setKeyEvents(Scene scene) {
        scene.setOnKeyPressed((KeyEvent e) -> {
            if (!input.contains(e.getText())) {
                input.add(e.getText());
            }
        });

        scene.setOnKeyReleased((KeyEvent e) -> {
            input.remove(e.getText());
        });
    }

}
