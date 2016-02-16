/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gol.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author s305054, s305084, s305089
 */
public class MainController implements Initializable {

    private static Stage primaryStage;
    private static List<String> input = new ArrayList<String>();

    /**
     *
     * @param stage
     */
    public static void loadStage(Stage stage) {
        primaryStage = stage;
    }

    public static boolean inputContains(String keyInput) {
        return input.contains(keyInput);
    }

    /**
     *
     * @throws IOException
     */
    @FXML
    public void startGame() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/gol/view/Game.fxml"));
        Scene scene = new Scene(root);

        setKeyEvents(scene);
        primaryStage.setScene(scene);

    }

    public void setKeyEvents(Scene scene) {
        scene.setOnKeyPressed(
                new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                if (!input.contains(e.getText())) {
                    input.add(e.getText());
                }

            }

        });

        scene.setOnKeyReleased(
                new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent e) {
                input.remove(e.getText());
            }

        });
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //TODO
    }

}
