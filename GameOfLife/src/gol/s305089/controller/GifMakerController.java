/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package gol.s305089.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.image.ImageView;

/**
 * FXML Controller class
 *
 * @author s305089
 */
public class GifMakerController implements Initializable {

    @FXML
    private Label labelCurrentDest;
    @FXML
    private Button btnChooseSaveDest ;
    @FXML
    private Slider sliderCellSize ;
    @FXML
    private Spinner spinnNumIterations ;
    @FXML
    private Spinner spinnTimeBetween ;
    @FXML
    private ImageView imgViewPreview;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        imgViewPreview.prefHeight())
    }
    
    

    @FXML
    private void generateGIF() {

    }

}
