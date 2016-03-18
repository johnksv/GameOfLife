/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.s305089.controller.gif;

import gol.model.Board.Board;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

/**
 * FXML Controller class
 *
 * @author s305089
 */
public class GifMakerController implements Initializable {

    @FXML
    private BorderPane borderPane;
    @FXML
    private Label labelCurrentDest;
    @FXML
    private Button btnChooseSaveDest;
    @FXML
    private Slider sliderCellSize;
    @FXML
    private Spinner spinnNumIterations;
    @FXML
    private Spinner spinnTimeBetween;
    @FXML
    private ImageView imgViewPreview;

    protected byte[][] activeBoard;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        imgViewPreview.prefHeight(borderPane.getHeight());
        imgViewPreview.prefWidth(borderPane.getWidth());
        FileChooser filechooser = new FileChooser();
        //TODO set filter for filechooser to .gif
        String saveLocation = filechooser.showSaveDialog(null).toPath().toString();
    }

    @FXML
    private void generateGIF() {
        try {
            BoardToGif.writeBoardtoGIF(activeBoard);
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage());
            alert.setTitle("Error");
            alert.setHeaderText("Pattern to GIF");
            alert.showAndWait();
        }
    }

    public void setByteBoard(Board activeBoard) {
        this.activeBoard = activeBoard.getBoundingBoxBoard();
    }

}
