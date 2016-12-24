/**
 * @author Jahn Kuppinger on 22.12.2016.
 */

import java.io.*;

public class IO {

        private static BufferedReader br;

        /** Konstruktor*/
        public IO() {
            br = new BufferedReader (new InputStreamReader (System.in));
        }

        /** lese beliebigen Text ein und returne ihn als string*/
        public static String read (String s) throws Exception {
            writeAndFlush(s);
            String eingabe = br.readLine();
            if (eingabe.length() == 0) {
                read("Bitte geben sie etwas ein");
            }
            return eingabe;
        }

        /** lese einen integer ein und returne ihn als int*/
        public static int readInt (String s) throws Exception {
            writeAndFlush(s);
            int x = 0;
            while (x == 0) {
                String eingabe = br.readLine();
                if (eingabe.length() > 0) {
                    x = Integer.parseInt(eingabe);
                }
                else {
                    System.out.println("Bitte einen int Zahlenwert angeben");
                }
            }
            return x;
        }

        /** lese ein, wie hoch und breit das Spielfeld werden soll. die eingegebene Zahl
         * wird sowohl für die Breite als auch die Höhe des Spielfelds benutzt*/
        public static int readMatrixSize () throws Exception {
            int n = readInt("Wie groß soll das Spielfeld sein? (Zahl zwischen 6 und 20)");
            if (n < 6 || n > 20) {
                System.out.println("Ungültige Eingabe, Bitte erneut eingeben!");
                n = readMatrixSize();
            }
            return n;
        }

        /** fülle jedes element der Matrix mit dem default-wert: `Leerzeile+|` */
        public static String[][] initializeMatrix (int n, String fieldText) throws Exception {
            String[][] newMatrix = new String[n][n];
            for (int i = 0; i < n; i++) {
                for (int k = 0; k < n; k++) {
                    newMatrix[i][k] = fieldText;
                }
            }
                return newMatrix;
        }

        /** frage nach, ob ein weiteres spiel gestartet werden soll*/
        public static boolean askNewGame () throws Exception {
            boolean newGame = false;
            String cmd = read("Wollen sie ein neues Spiel beginnen?: ");
            if (cmd.contains("ja")) {
                newGame = true;
            }
            else if (cmd.contains("nein")){
                newGame = false;
            }
            else {
                System.out.println("Bitte geben sie `ja` oder `nein` ein.");
                askNewGame();
            }
            return newGame;
        }

        /** ändere einen bestimmten wert der matrix ab*/
        public static String[][] addValue (String[][] old, int n, String zeichen, int spielernummer) throws Exception {
            String[][] newGameBoard = old;
            System.out.println("Wo wollen sie ihre Steine platzieren, Spieler " + spielernummer + "?");
            while (true) {
                int x = readSpalte(n);
                int y = readInt("Zeile (Nummer von 1 bis " + n + ")")-1;
                if (x>=0 && x<n && y>=0 && y<n) {
                    if (newGameBoard[y][x] == " |") {
                        newGameBoard[y][x] = zeichen + "|";
                        break;
                    }
                    else if (newGameBoard[y][x] != " |"){
                        System.out.println("Dieses Feld ist schon besetzt , bitte ein anderes Feld füllen.");
                    }
                }
                else if (x<0 || x>=n || y<0 || y>=n){
                    System.out.println("Dieses Feld existiert nicht , bitte ein anderes Feld füllen.");
                }
                else {
                    System.out.println("Ungültige Eingabe, bitte erneut eingeben.");
                }
            }
            return newGameBoard;
        }

        private static int readSpalte(int n) throws Exception {
            String[] buchstaben = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t"};
            int val = -1;
            while (val == -1) {
                String spalte = read("Spalte (Buchstabe von " + buchstaben[0] + " bis " + buchstaben[(n-1)] + "):");
                for (int i = 0; i < n; i++) {
                    if (spalte.equals(buchstaben[i])) {
                        val = i;
                    }
                }
                if (val == -1) {
                    System.out.println("Ungültige Eingabe, bitte geben sie einen passenden Buchstaben ein.");
                }
            }
            return val;
        }

        private static void writeAndFlush(String s){
            System.out.println(s);
            System.out.flush();
        }
}
