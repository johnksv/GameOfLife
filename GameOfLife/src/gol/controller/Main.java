package gol.controller;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
import gol.model.FileIO.PatternFormatException;
import gol.model.FileIO.ReadFile;
import gol.other.Configuration;
import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Starting- and endingpoint of application.
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

        // TODO REMOVE TESTING
//        test100NextGen();
//        Platform.exit();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void test100NextGen() {
        byte[][] boardFromFile = {{}};
        try {
            FileChooser fileChooser = new FileChooser();

            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Game of Life Files", "*.rle", "*.lif", "*.cells"),
                    new FileChooser.ExtensionFilter("All Files", "*.*"));

            File selected = fileChooser.showOpenDialog(null);
            if (selected != null) {
                boardFromFile = ReadFile.readFileFromDisk(selected.toPath());

            }

        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "There was an error reading the file");
            alert.setTitle("Error");
            alert.setHeaderText("Reading File Error");
            alert.showAndWait();
        } catch (PatternFormatException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.setTitle("Error");
            alert.setHeaderText("Pattern Error");
            alert.showAndWait();

        }

        Board brett = new ArrayBoard();
        brett.insertArray(boardFromFile, 1, 1);

        int n = 0;
        int antall = 1000;
        long startTime = System.nanoTime();
        while (n < antall) {
            brett.nextGen();
            n++;
        }

        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("Tid totalt (i nanosek): " + estimatedTime);
        System.out.println("Tid for 1 (i nanosek): " + estimatedTime / antall);
    }

}
