/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gol.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author s305054, s305089, s305084
 */
public class MainController implements Initializable {

    private static Stage primaryStage;

    /**
     *
     * @param stage
     */
    public static void loadStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     *
     * @throws IOException
     */
    @FXML
    public void startGame() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/gol/view/Game.fxml"));
        Scene scene = new Scene(root);
        
        primaryStage.setScene(scene);

    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //TODO
    }

}
