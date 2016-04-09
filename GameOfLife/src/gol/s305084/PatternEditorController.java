package gol.s305084;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

/**
 * Controller
 *
 * @author S305084
 */
public class PatternEditorController implements Initializable {

    @FXML
    private Canvas canvas;
    @FXML
    private RadioButton rbRemoveCell;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtAuthor;
    @FXML
    private TextField txtComment;
    
    private Board activeBoard;
    private GraphicsContext gc;
    private Color bgColor;
    private Color cellColor;

    @FXML
    private void handlebtnRLE() {
        try {
            FileChooser fileChooser = new FileChooser();

            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Game of Life Files", "*.rle"),
                    new FileChooser.ExtensionFilter("All Files", "*.*"));

            File selected = fileChooser.showSaveDialog(null);
            if (selected != null) {
                WriteRLE.toRLE(selected.toPath(), activeBoard, txtName.getText(),txtAuthor.getText(),txtComment.getText());
            }
        } catch (IOException ex) {
            Logger.getLogger(PatternEditorController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gc = canvas.getGraphicsContext2D();
        activeBoard = new ArrayBoard(150, 150);

        activeBoard.setCellSize(13);
        activeBoard.setGridSpacing(0.6);
        mouseInit();
    }

    private void mouseInit() {
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED,
                (MouseEvent e) -> {
                    handleMouseClick(e);

                });
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                (MouseEvent e) -> {
                    handleMouseClick(e);

                });
    }

    public void setBGColor(Color bgColor) {
        this.bgColor = bgColor;

        gc.setFill(bgColor);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void setCellColor(Color cellColor) {
        this.cellColor = cellColor;

        gc.setFill(bgColor);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void handleMouseClick(MouseEvent e) {
        double x = e.getX();
        double y = e.getY();

        if (rbRemoveCell.isSelected()) {
            activeBoard.setCellState(y, x, false, 0, 0);
        } else {
            activeBoard.setCellState(y, x, true, 0, 0);
        }
        draw();
    }

    private void draw() {
        gc.setGlobalAlpha(1);
        gc.setFill(bgColor);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(cellColor);

        for (int i = 1; i < activeBoard.getArrayLength(); i++) {
            for (int j = 1; j < activeBoard.getArrayLength(i); j++) {
                if (activeBoard.getCellState(i, j)) {
                    gc.fillRect(j * activeBoard.getCellSize() + j * activeBoard.getGridSpacing(),
                            i * activeBoard.getCellSize() + i * activeBoard.getGridSpacing(),
                            activeBoard.getCellSize(),
                            activeBoard.getCellSize());
                }
            }
        }
    }

}
