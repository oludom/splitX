/**
 * 
 */
package game;

/**
 * @author Soeren Wirries
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
			errorMsg = "Das Ziel befindet sich auserhalb des Spielfeldes!";
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

}
