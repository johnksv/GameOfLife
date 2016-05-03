package gol.controller;

import gol.other.Configuration;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Starting and ending point of application.
 *
 * @author s305054, s305084, s305089
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Configuration.loadConfig();
        //Passing the stage to main controller for later use.
        //needs to be done first
        MainController.loadStage(primaryStage);

        Parent root = FXMLLoader.load(getClass().getResource("/gol/view/StartScreen.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        MainController.loadStage(primaryStage);

    }

    public static void main(String[] args) {
        launch(args);
    }

}
