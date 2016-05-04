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
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gol/view/StartScreen.fxml"));
        Parent root = loader.load();
        MainController mainControll = loader.getController();
        mainControll.setStage(primaryStage);
        
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}
