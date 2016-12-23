package game;

import ui.BasicUI;
/**
 * 
 */

/**
 * @author Soeren Wirries
 *
 */
public class StartGame {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		BasicUI ui = new BasicUI();
		
		boolean run = ui.startGame();
		while(run){
			run = ui.startGame();
		}
		ui.prln("Spiel wurde beendet!");
	}
	
}
