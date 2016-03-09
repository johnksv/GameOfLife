/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gol.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author s305054, s305084, s305089
 */
public class MainController {

    private static Stage primaryStage;
    private static List<String> input = new ArrayList<String>();

    @FXML
    public void startGame() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/gol/view/Game.fxml"));
        Scene scene = new Scene(root);

        setKeyEvents(scene);
        primaryStage.setScene(scene);

    }

    public static boolean inputContains(String keyInput) {
        return input.contains(keyInput);
    }

    public static void loadStage(Stage stage) {
        primaryStage = stage;
    }

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
