package game;

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
		// TODO Auto-generated method stub
		
		Board b = new Board(8);
		
		//
		
		Stone s = new Stone(new BoardPoint("A",1), false);
		Stone s1 = new Stone(new BoardPoint("A",1), true);
		
		if(!b.addStone(s)) System.out.println("Fehler");
		if(!b.addStone(s1)) System.out.println("Fehler");
		
		b.draw();
		
		//System.out.println();
	}

}
