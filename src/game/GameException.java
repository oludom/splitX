/**
 * 
 */
package game;

/**
 * @author Soeren Wirries, Jahn Kuppinger, Micha Heiss
 *
 */
public class GameException extends Exception{

	/**
	 * In dieser Klasser werden alle Exceptions aufgeführt die das Spiel (Board-Klasse)
	 * werfen kann. Die Exceptions muessen nicht immer negertive Folgen haben (vgl GameWonException)
	 */
	
	String errorMsg = "";
	private static final long serialVersionUID = 1L;
	
	public GameException(){
		
	}
	
	public String toString(){
		return errorMsg;
	}
	
	static public class BoardOutOfBoundException extends GameException{
		/**
		 * wird idR geworfen wenn der Stein(Punkt) nicht auf dem Spielfeld liegt.
		 */
		private static final long serialVersionUID = 1L;
		String errorMsg = "";
		
		public BoardOutOfBoundException(){
			errorMsg = "Dieses Feld ist bereits belegt!";
		}
		
		public String toString(){
			return errorMsg;
		}
	}

	static public class GameWonException extends GameException{
		/**
		 * wird geworfen wenn der Spieler 6 (oder mehr) Steine als Kette auf dem Feld hat
		 * unterscheidet aufgrund der Farbe (color/boolean) welcher Spieler gewonnen hat.
		 */
		private static final long serialVersionUID = 1L;
		String errorMsg = "";

		public GameWonException(boolean color) {
			if(color){
				errorMsg = "Spieler Schwarz hat gewonnen!";
			}else {
				errorMsg = "Spieler Weiss hat gewonnen!";
			}
		}

		public String toString(){
			return errorMsg;
		}
	}

	static public class ClientWonException extends GameException{
		/**
		 * wird auf dem Server geworfen, wenn ein Client gewinnt und damit das Spiel beendet wird.
		 */
		private static final long serialVersionUID = 1L;
		String errorMsg = "";

		public ClientWonException() {
			errorMsg = "Finished Game!";
		}

		public String toString(){
			return errorMsg;
		}
	}

	static public class BoardFullException extends GameException{
		private static final long serialVersionUID = 1L;
		String errorMsg = "";

		public BoardFullException(){
			errorMsg = "Alle Felder auf dem Spielfeld wurden belegt. Das Spiel endet unentschieden.";
		}

		public String toString(){
			return errorMsg;
		}
	}


}
