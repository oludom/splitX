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
		/**
		 * diese Klasse dient nur dem starten des Spiels
		 */
		BasicUI ui = new BasicUI();//iniziallisiert das Spiel
		
		boolean run = ui.startGame();//startet die erste Runde
		while(run){
			run = ui.startGame();//solange das Spiel nicht beendet wurde, wird das Spiel (Menu) erneut angezeigt
		}
		ui.prln("Spiel wurde beendet!");
	}
	
}
