package gol.svergja;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.util.Duration;
import javafx.util.StringConverter;

/**
 * @author s305089 - John Kasper Svergja
 */
public class Util {

    private Util() {
    }

    /**
     * This method is copied from {@link javafx.scene.control.Spinner }. THis is
     * because it is private, and no other easy way is provided to easily commit
     * a changed value. From Spinners Javadoc: "To make sure the model has the
     * same value as the editor, the user must commit the edit using the Enter
     * key."
     * <a href="https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/Spinner.html">https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/Spinner.html</a>
     *
     * <p>
     * Added modification: Only spinners with IntegerSpinnerValueFactory is
     * calling this method. I therefor added handling for when not typing a
     * number.
     * </p>
     *
     * @param <T> The type of the spinner
     * @param spinner The spinner to set the value on.
     * @see javafx.scene.control.Spinner
     */
    public static <T> void commitEditorText(Spinner<T> spinner) {
        String text = spinner.getEditor().getText();
        text = text.replaceAll("\\D", "");
        if (text.equals("")) {
            spinner.getEditor().setText("100");
            return;
        }
        spinner.getEditor().setText(text);
        SpinnerValueFactory<T> valueFactory = spinner.getValueFactory();
        if (valueFactory != null) {
            StringConverter<T> converter = valueFactory.getConverter();
            if (converter != null) {
                T value = converter.fromString(text);
                valueFactory.setValue(value);
            }
        }
    }

    /**
     * Display a tooltip right beside to the mouse position given by the mouse
     * event. This method is natural to use on Mouse_Entered or another mouse
     * event when you want to show a tooltip. The method does NOT hide the
     * tooltip when the mouse is moved.
     * <p>
     * Example usage:</p>
     * <code>
     * Tooltip tooltip = new Tooltip("This tooltip should be desplayed on mouse entered.");
     * Node.setOnMouseEntered(mouseEvent -&gt; Util.showTooltip(mouseEvent, Node, tooltip));
     * Node.setOnMouseExited(mouseEvent -&gt; tooltip.hide());
     * </code>
     *
     * @param e the mouse event that will trigger the tooltip to show.
     * @param ownerNode the owner of this tooltip.
     * @param tip the tooltip that should be displayed.
     */
    public static void showTooltip(MouseEvent e, Node ownerNode, Tooltip tip) {
        double anchorX = ownerNode.getScene().getWindow().getX() + 10;
        double anchorY = ownerNode.getScene().getWindow().getY() + 40;
        tip.setFont(new Font(12));
        tip.show(ownerNode, anchorX + e.getSceneX(), anchorY + e.getSceneY());
    }

    /**
     * Retrieves the users screens width and height in pixels.
     *
     * @return Array with screen width at index 0, and screen height at index 1
     */
    public static double[] getScreenSize() {
        double[] result = new double[2];
        result[0] = Screen.getPrimary().getVisualBounds().getWidth();
        result[1] = Screen.getPrimary().getVisualBounds().getHeight();
        return result;
    }

    /**
     * Updates an label meant to display time, as the media player plays.
     *
     * @param mediaPlayer The mediaplayer that plays the media that should be
     * tracked.
     * @param timeLabel The label that should display the time.
     */
    public static void setTimeLabel(MediaPlayer mediaPlayer, Label timeLabel) {
        mediaPlayer.currentTimeProperty().addListener((ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) -> {
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
            int totalDur = (int) mediaPlayer.getTotalDuration().toSeconds();
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
