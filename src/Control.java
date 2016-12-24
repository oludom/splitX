public class Control {

    public static void main(String[] args) throws Exception {
        Control ctrl = new Control();
    }

    IO io = new IO();
    Draw draw = new Draw();
    Compute compute = new Compute();

    boolean newGame = true;
    boolean fielsAvailable = true;

    int matrixSize;
    int player1 = 1;
    int longestRowP1 = 0;
    int player2 = 2;
    int longestRowP2 = 0;

    String sign1 = "x";
    String sign2 = "o";
    String emptyField = " |";
    String[][] gameBoard;

    /** Testprogramm, das das spielfeld zufällig mit werten füllt
    public String[][] randomize(){
        for(int i = 0; i<gameBoard.length;i++){
            for(int j = 0; j<gameBoard.length; j++){
                int tmp = (int)(Math.random() *10) %3;

                if(tmp == 1){
                    gameBoard[i][j] = sign1+"|";
                }else if(tmp == 2){
                    gameBoard[i][j] = sign2+"|";
                }else{
                    gameBoard[i][j] = emptyField;
                }
            }
        }
        return gameBoard;
    }*/

    public Control() throws Exception {
        System.out.println("Herzlich willkommen bei Connect6!");

        while (newGame == true) {
            /** erfrage spielfeldgröße*/
            matrixSize = io.readMatrixSize();

            /** erstmaliges befüllen der spielfeldmatrix und zeichnen des Spielfeldes*/
            gameBoard = io.initializeMatrix(matrixSize, emptyField);
            draw.updateGameBoard(gameBoard, matrixSize);
            fielsAvailable = true;
            longestRowP1 = 0;
            longestRowP2 = 0;

            /** Spieler 1 setzt den ersten stein*/
            gameBoard = io.addValue(gameBoard, matrixSize, sign1, player1);
            draw.updateGameBoard(gameBoard, matrixSize);

            /**spieler1 und spieler2 setzen so lange abwechselnd zwei ihrer steine,
             * bis das spielbrett voll mit steinen besetzt ist oder einer der spieler
             * eine 6er kette legen konnte*/
            while (fielsAvailable == true && longestRowP1 < 6 && longestRowP2 < 6) {
                for (int i = 1; i <= 2; i++) {
                    if (fielsAvailable == true && longestRowP1 < 6 && longestRowP2 < 6) {
                        gameBoard = io.addValue(gameBoard, matrixSize, sign2, player2);
                        /*gameBoard = randomize();*/
                        draw.updateGameBoard(gameBoard, matrixSize);
                        longestRowP2 = compute.checkRow(gameBoard, matrixSize, sign2);
                        System.out.println("Längste Kette Spieler 1: " + longestRowP1);
                        System.out.println("Längste Kette Spieler 2: " + longestRowP2);
                        fielsAvailable = compute.checkAvailableFields(gameBoard, matrixSize, emptyField);

                    }
                    else {
                        break;
                    }
                }
                for (int i = 1; i <= 2; i++) {
                    if (fielsAvailable == true && longestRowP1 < 6 && longestRowP2 < 6) {
                        gameBoard = io.addValue(gameBoard, matrixSize, sign1, player1);
                        draw.updateGameBoard(gameBoard, matrixSize);
                        longestRowP1 = compute.checkRow(gameBoard, matrixSize, sign1);
                        System.out.println("Längste Kette Spieler 1: " + longestRowP1);
                        System.out.println("Längste Kette Spieler 2: " + longestRowP2);
                        fielsAvailable = compute.checkAvailableFields(gameBoard, matrixSize, emptyField);
                    }
                    else {
                        break;
                    }
                }
            }

            if (longestRowP1 >= 6) {
                System.out.println("Spieler 1 hat gewonnen!");
            }
            else if (longestRowP2 >= 6) {
                System.out.println("Spieler 2 hat gewonnen!");
            }
            else {
                System.out.println("Dieses Spiel ist beendet.");
            }

            /** frage, ob ein neues spiel gestartet werden soll*/
            newGame = io.askNewGame();
        }
    }
}

