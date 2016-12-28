/**
 * 
 */
package ki;

import game.Board;
import game.GameException.BoardFullException;
import game.GameException.BoardOutOfBoundException;
import game.GameException.GameWonException;
import game.Stone;
import ui.BasicUI;
import ui.UiException.StopGameException;

/**
 * @author Sören Wirries
 *
 */
public class TestBot {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		/**
		 * Dieser Klasse dient nur zum Testen des Bots, sie wird nicht fuer die Funktion benoetigt
		 */
		
		Board board = new Board(20);
		BasicUI ui = new BasicUI(board);
		Bot bot = new Bot(board, false);
		boolean run = true;
		String win = "";
		
		do{
			for(int i = 0; i < 2; i++){
				try {
					
					board.addStone(new Stone(ui.readBP(),true));
				} catch (BoardOutOfBoundException | StopGameException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					ui.prln(e.toString());
					//run = false;
				}
				try {
					board.checkWinner();
				} catch (GameWonException e) {
					win = e.toString();
					run = false;
					break;
				}catch (BoardFullException e) {
					win = e.toString();
					run = false;
					break;
				}
				board.draw();
			}
			for(int i = 0; i < 2; i++){
				ui.prln("Bot ist drann");
				bot.next();
				try {
					board.checkWinner();
				} catch (GameWonException e) {
					win = e.toString();
					run = false;
					break;
				} catch (BoardFullException e) {
					win = e.toString();
					run = false;
					break;
				}
			}
			board.draw();
		}while(run);
		ui.prln(win);
		
	}

}
