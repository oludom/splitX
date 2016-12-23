package ki;

import java.util.ArrayList;

import game.*;
import game.GameException.*;

public class Bot {
	
	private Board board;
	private boolean color;
	ArrayList<Stone> ownStone = new ArrayList<Stone>();
	ArrayList<Stone> oppStone = new ArrayList<Stone>();
	
	public Bot(Board board, boolean color){
		this.board = board;
		this.color = color;
		
		update();
	}
	
	private void update(){
		if(color){
			oppStone = board.getWhiteStones();
			ownStone = board.getBlackStones();
		}else{
			ownStone = board.getWhiteStones();
			oppStone = board.getBlackStones();
		}
		
	}
	
	public void next(){
		//TODO add new Stone
	}

}
