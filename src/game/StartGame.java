package game;

import javafx.application.Application;
import javafx.stage.Stage;
import ui.BasicUI;
import ui.BasicUIX;
/**
 * 
 */

/**
 * @author Soeren Wirries, Jahn Kuppinger, Micha Heiss
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
