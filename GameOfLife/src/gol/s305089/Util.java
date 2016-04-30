package gol.s305089;

import gol.model.Board.ArrayBoard;
import gol.model.Board.Board;
import gol.model.Board.DynamicBoard;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.stage.Screen;

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

    public static int longestRow(byte[][] arrayToSearch) {
        int longestRow = 0;
        for (byte[] row : arrayToSearch) {
            if (row.length > longestRow) {
                longestRow = row.length;
            }
        }
        return longestRow;
    }

}
