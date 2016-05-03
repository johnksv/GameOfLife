package gol.s305089;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
import gol.model.Board.DynamicBoard;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.util.Duration;

/**
 * @author John Kasper
 */
public class Util {

    private Util() {
    }

    public static void showTooltip(MouseEvent e, Node ownerNode, Tooltip tip) {
        double anchorX = ownerNode.getScene().getWindow().getX() + 20;
        double anchorY = ownerNode.getScene().getWindow().getY() + 20;
        tip.setFont(new Font(12));
        tip.show(ownerNode, anchorX + e.getSceneX(), anchorY + e.getSceneY());
    }

    /**
     * Retrives the users screens width and height in pixels.
     *
     * @return Array with screen width at index 0, and screen height at index 1
     */
    public static double[] getScreenSize() {
        double[] result = new double[2];
        result[0] = Screen.getPrimary().getVisualBounds().getWidth();
        result[1] = Screen.getPrimary().getVisualBounds().getHeight();
        return result;
    }

    public static void setTimeLabel(MediaPlayer current, Label timeLabel) {
        current.currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
            //Current time elapsed
            int secondsElapsed = (int) newValue.toSeconds();
            int minuts = secondsElapsed / 60;
            int seconds = secondsElapsed - minuts * 60;
            String elapsedTime = minuts + ":";
            if (seconds < 10) {
                elapsedTime += "0" + seconds;
            } else {
                elapsedTime += seconds;
            }
            //Total time of whole mediafile
            int totalDur = (int) current.getTotalDuration().toSeconds();
            int totalMin = totalDur / 60;
            int totalSec = totalDur - totalMin * 60;
            String totalTime = totalMin + ":";
            if (totalSec < 10) {
                totalTime += "0" + totalSec;
            } else {
                totalTime += totalSec;
            }
            timeLabel.setText(elapsedTime + "/" + totalTime);
        });
    }
}
