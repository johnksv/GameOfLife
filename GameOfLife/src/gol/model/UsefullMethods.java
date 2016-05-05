package gol.model;

import javafx.scene.control.Alert;

/**
 * @author s305054, s305084, s305089
 */
public final class UsefullMethods {

    private UsefullMethods() {
    }

    /**
     * Rotates this pattern 90 degrees clockwise.
     *
     * @param arrayToRotate pattern to be rotated
     * @return The rotated matrix
     */
    public static byte[][] rotateArray90Deg(byte[][] arrayToRotate) {
        int longestRow = 0;
        for (byte[] row : arrayToRotate) {
            if (row.length > longestRow) {
                longestRow = row.length;
            }
        }

        byte[][] rotatedArray = new byte[longestRow][arrayToRotate.length];

        for (int i = 0; i < arrayToRotate.length; i++) {
            byte[] row = new byte[arrayToRotate[i].length];
            for (int j = 0; j < arrayToRotate[i].length; j++) {
                row[j] = arrayToRotate[i][j];
                rotatedArray[j][arrayToRotate.length - 1 - i] = row[j];
            }
        }

        return rotatedArray;
    }

    /**
     * Show an error dialogue box.
     *
     * @param headerText specifies what the header text will be
     * @param contentText specifies what the content text will be
     */
    public static void showErrorAlert(String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR, contentText);
        alert.setTitle("Error");
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }
    
    /**
     * Transposes this given pattern.
     *
     * @param matrix pattern that will be transposed
     * @return a transposed matrix
     */
    public static byte[][] transposeMatrix(byte[][] matrix) {
        byte[][] returnMatrix = new byte[matrix[0].length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                returnMatrix[j][i] = matrix[i][j];
            }
        }
        return returnMatrix;
    }
}