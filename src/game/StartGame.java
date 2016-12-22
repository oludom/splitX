package game;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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
		// TODO Auto-generated method stub
		
		BasicUI ui = new BasicUI();
		
		boolean run = ui.startGame();
		while(run){
			run = ui.startGame();
		}
	}
	
	private void unusedMethods(){
		Board b = new Board(8);
		BufferedReader buffRead = new BufferedReader(new InputStreamReader(System.in));
		
		boolean run = true;
		int i = 0;
		while(run){
			String xPos = "A";
			int yPos = 1;
			
			try {
				System.out.print("::>");
				String in = buffRead.readLine();
				if(in.contains(" ")){
					String[] ar = in.split(" ");
					xPos = ar[0];
					yPos = Integer.parseInt(ar[1]);
					
					Stone s = new Stone(new BoardPoint(xPos,yPos), i%2==0);
					if(!b.addStone(s)) System.out.println("Fehler");
				}else{
					throw new Exception();
				}
			} catch (Exception e) {
				System.out.println("Fehler Input");
				break;
			}finally{
				
				b.draw();
				
				System.out.println("Max White:"+b.maxRowWhite());
				System.out.println("Max Black:"+b.maxRowBlack());
				i++;
			}
			
			
			
		}
		/*
		Stone s = new Stone(new BoardPoint("D",5), false);
		Stone s1 = new Stone(new BoardPoint("D",4), false);
		Stone s2 = new Stone(new BoardPoint("D",3), false);
		Stone s3 = new Stone(new BoardPoint("D",2), false);
		
		if(!b.addStone(s)) System.out.println("Fehler");
		if(!b.addStone(s1)) System.out.println("Fehler");
		if(!b.addStone(s2)) System.out.println("Fehler");
		if(!b.addStone(s3)) System.out.println("Fehler");
		
		b.draw();
		
		System.out.println("Max:"+b.maxRow());*/
	}

}
