package gol.controller;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author s305054, s305084, s305089
 */
public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/gol/view/StartScreen.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        //Passing the stage to main controller for later use.
        MainController.loadStage(primaryStage);
        
    }

    
    public static void main(String[] args) {
        launch(args);
    }
    
    public static void testNextGen(){
        
        
        
    }
    
    
}
