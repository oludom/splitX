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
	 * 
	 */
	
	String errorMsg = "";
	private static final long serialVersionUID = 1L;
	
	public GameException(){
		
	}
	
	public String toString(){
		return errorMsg;
	}
	
	static public class BoardOutOfBoundException extends GameException{
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
