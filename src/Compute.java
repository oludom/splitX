/**
 * @author Jahn Kuppinger on 22.12.2016.
 */
public class Compute {

    public boolean checkAvailableFields(String[][] gameBoard, int n, String fieldText) {
        boolean fieldsAvailable = false;
        for (int i = 0; i < n; i++) {
            for (int k = 0; k < n; k++) {
                if (gameBoard[i][k] == fieldText) {
                    fieldsAvailable = true;
                }
            }
        }
        return fieldsAvailable;
    }

    public int checkRow(String[][] gameBoard, int n, String sign) {
        int longestRow = 0;
        int tmp = 0;
        String zeichen = sign + "|";
        for (int i = 0; i < n; i++) {
            for (int k = 0; k < n; k++) {
                if (gameBoard[i][k].equals(zeichen)) {
                    /** vertikale linien suchen*/
                    tmp = 1 + checkVertical(gameBoard, i, k, zeichen);
                    if (longestRow < tmp) {
                        longestRow = tmp;
                    }
                    /** horizontale linien suchen*/
                    tmp = 1 + checkHorizontal(gameBoard, i, k, zeichen);
                    if (longestRow < tmp) {
                        longestRow = tmp;
                    }
                    /** diagonale absteigende linien suchen*/
                    tmp = 1 + checkDiagonalDown(gameBoard, i, k, zeichen);
                    if (longestRow < tmp) {
                        longestRow = tmp;
                    }
                    /** diagonale ansteigende linien suchen*/
                    tmp = 1 + checkDiagonalUp(gameBoard, n, i, k, zeichen);
                    if (longestRow < tmp) {
                        longestRow = tmp;
                        /*System.out.println("Temp: " + tmp);*/
                    }
                }
            }
        }
        return longestRow;
    }

    private int checkDiagonalDown(String[][] gameBoard, int i, int k, String sign) {
        int diagonalStones = 0;
        if (i > 0 && k > 0) {
            for (int j = 1; j <= i && j <= k; j++) {
                if (gameBoard[i-j][k-j].equals(sign)) {
                    diagonalStones += 1;
                }
                else {
                    break;
                }
            }
        }
        return diagonalStones;
    }

    private int checkDiagonalUp(String[][] gameBoard, int n, int i, int k, String sign) {
        int diagonalStones = 0;
        if (i > 0 && k < n) {
            for (int j = 1; j <= i && j < (n-k); j++) {
                if (gameBoard[i-j][k+j].equals(sign)) {
                    diagonalStones += 1;
                } else {
                    break;
                }
            }
        }
        return diagonalStones;
    }

    /*private int checkDiagonalUp(String[][] gameBoard, int n, int i, int k, String sign) {
        int diagonalStones = 0;
        if (i > 0 && k < n) {
            for (int j = 1; j < i || j < k; j++) {
                System.out.println("i-j: " + (i-j));
                System.out.println("k+j: " + (k+j));
                if (gameBoard[i-j][k+j].equals(sign)) {
                    diagonalStones += 1;
                }
                else {
                    break;
                }
            }
        }
        return diagonalStones;
    }*/

    private int checkVertical(String[][] gameBoard, int i, int k, String sign) {
        int verticalStones = 0;
        if (i > 0) {
            for (int j = 1; j <= i; j++) {
                if (gameBoard[i-j][k].equals(sign)) {
                    verticalStones += 1;
                }
                else {
                    break;
                }
            }
        }
        return verticalStones;
    }

    private int checkHorizontal(String[][] gameBoard, int i, int k, String sign) {
        int horizontalStones = 0;
        if (k > 0) {
            for (int j = 1; j <= k; j++) {
                if (gameBoard[i][k-j].equals(sign)) {
                    horizontalStones += 1;
                }
                else {
                    break;
                }
            }
        }
        return horizontalStones;
    }
}