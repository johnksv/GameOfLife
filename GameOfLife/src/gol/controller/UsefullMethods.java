package gol.controller;

/**
 * @author s305054, s305084, s305089
 */
public final class UsefullMethods {

    private UsefullMethods() {
    }

    public static byte[][] transposeMatrix(byte[][] matrix) {
        byte[][] returnMatrix = new byte[matrix[0].length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                returnMatrix[j][i] = matrix[i][j];
            }
        }
        return matrix;
    }

    public static byte[][] rotateArray90Deg(byte[][] arrayToRotate) {
        int longestRow = longestRow(arrayToRotate);

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
