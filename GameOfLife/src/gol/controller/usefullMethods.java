package gol.controller;

/**
 * @author s305089
 */
public final class usefullMethods {

    private usefullMethods() {
    }

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

}
