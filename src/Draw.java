/**
 * @author Jahn Kuppinger on 22.12.2016.
 */
public class Draw {

    private String[] buchstaben = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t"};

    /** zeichne das Spielfeld in der Konsole*/
    public String updateGameBoard (String[][] gameBoard, int n) {
        String ausgabe = "  |";
        for (int a = 0; a < n; a++) {
            ausgabe += buchstaben[a] + "|";
        }
        ausgabe += "\n" + " 1|";
        for (int i = 0; i < n; i++) {
            for (int k = 0; k < n; k++) {
                ausgabe += gameBoard[i][k];
            }
            if (i < (n-1)) {
                if (i < 8) {
                    ausgabe += "\n" + " " + (i+2) + "|";
                }
                else {
                    ausgabe += "\n" + (i+2) + "|";
                }
            }
        }
        System.out.println(ausgabe);
        return ausgabe;
    }
}
