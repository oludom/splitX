package game;
import java.util.ArrayList;
import game.GameException.*;

/**
 * 
 */

/**
 * @author Soeren Wirries
 *
 */
public class Board {
	private final int dimension;
	private String[][] field;
	private String[] alphabet = {"A", "B", "C", "D", "E", "F", "G","H","I","J", "K", "L","M", "N","O", "P","Q", "R", "S", "T", "U", "V", "W","X", "Y", "Z"};
	private ArrayList<Stone> allStones = new ArrayList<Stone>();
	private ArrayList<Stone> blackStones = new ArrayList<Stone>();
	private ArrayList<Stone> whiteStones = new ArrayList<Stone>();
	
	private int border = 1;
	public BoardPoint maxPoint;
	
	public Board(int dimension){
		this.dimension = dimension;
		maxPoint = new BoardPoint(alphabet[dimension], dimension);
		generateField();
	}
	
	private void generateField(){
		/**  A| B| C| D| E| F| G| H|
		 * 1| |  |  |  |  |  |  |  |
		 * 2| |  |  |  |  |  |  |  |
		 * 3| |  |  |  |  |  |  |  |
		 * 4| |  |  |  |  |  |  |  |
		 * 5| |  |  |  |  |  |  |  |
		 * 6| |  |  |  |  |  |  |  |
		 * 7| |  |  |  |  |  |  |  |
		 * 8| |  |  |  |  |  |  |  |
		 * 
		 */
		
		String[][] temp = new String[dimension + border][dimension +border];
		for(int i = 0; i < temp.length; i++){
			for(int k = 0; k < temp.length; k++){
				temp[i][k] = "";
			}
		}
		
		for(int i = 0; i<temp.length; i++ ){
			if(i == 0){
				for(int k = 0; k<temp.length; k++){
					if(k < border){
						temp[i][k] = "   ";
					}else{
						temp[i][k] = alphabet[k - border]+ "|";
					}
					
				}
			}
			/*if(i == 1){
				for(int k = 0; k<dimension +2; k++){
					temp[i][k] = "-";
				}
			}*/
			if(i > 0){
				if(i < 10){
					temp[i][0] =" " + String.valueOf(i) + "|";
				}else{
					temp[i][0] = String.valueOf(i) + "|";
				}
				
				for(int k = 1; k < temp.length; k++){
					temp[i][k] = " |";
				}
			}
		}
		field = temp;
	}
	
	
	public boolean addStone(Stone stone) throws BoardOutOfBoundException{
		if(checkPoint(stone.getPoint())){
			
			allStones.add(stone);
			
			if(stone.hasColor()){
				addBlack(stone);
			}
			if(!stone.hasColor()){
				addWhite(stone);
			}
			
			return true;
		}else{
			throw new BoardOutOfBoundException();
		}
		
	}
	
	private void addWhite(Stone stone){
		for(Stone white : whiteStones){
			
			setParency(stone, white);
			
		}
		whiteStones.add(stone);
	}
	
	private void addBlack(Stone stone){
		for(Stone black : blackStones){
			
			setParency(stone, black);
			
		}
		blackStones.add(stone);
	}
	
	public int maxRowWhite(){
		int row = 0;
		for(Stone stone : whiteStones){
			if(!stone.hasParentTop()){
				int temp = stone.countTopBot();
				if(temp > row) row = temp;
			}
			if(!stone.hasParentLeft()){
				int temp = stone.countLeftRight();
				if(temp > row) row = temp;
			}
			if(!stone.hasParentTopLeft()){
				int temp = stone.countTopLeftBotRight();
				if(temp > row) row = temp;
			}
			if(!stone.hasParentTopRight()){
				int temp = stone.countTopRightBotLeft();
				if(temp > row) row = temp;
			}
			
		}
		return row;
	}
	
	public int maxRowBlack(){
		int row = 0;
		for(Stone stone : blackStones){
			if(!stone.hasParentTop()){
				int temp = stone.countTopBot();
				if(temp > row) row = temp;
			}
			if(!stone.hasParentLeft()){
				int temp = stone.countLeftRight();
				if(temp > row) row = temp;
			}
			if(!stone.hasParentTopLeft()){
				int temp = stone.countTopLeftBotRight();
				if(temp > row) row = temp;
			}
			if(!stone.hasParentTopRight()){
				int temp = stone.countTopRightBotLeft();
				if(temp > row) row = temp;
			}
			
		}
		return row;
	}
	
	public void checkWinner() throws GameWonException{
		if(this.maxRowBlack() > 5){
			throw new GameWonException(true);
		}
		if(this.maxRowWhite() > 5){
			throw new GameWonException(false);
		}
	}
	
	private void setParency(Stone newStone,Stone oldStone){
		if(newStone.getPoint().top().compareTo(oldStone.getPoint()) == 0){
			oldStone.setChildBot(newStone);
			newStone.setParentTop(oldStone);
		}
		if(newStone.getPoint().bottom().compareTo(oldStone.getPoint()) == 0){
			oldStone.setParentTop(newStone);
			newStone.setChildBot(oldStone);
		}
		
		if(newStone.getPoint().right().compareTo(oldStone.getPoint()) == 0){
			newStone.setChildRight(oldStone);
			oldStone.setParentLeft(newStone);
		}
		if(newStone.getPoint().left().compareTo(oldStone.getPoint()) == 0){
			oldStone.setChildRight(newStone);
			newStone.setParentLeft(oldStone);
		}
		
		if(newStone.getPoint().topright().compareTo(oldStone.getPoint()) == 0){
			newStone.setParentTopRight(oldStone);
			oldStone.setChildBotLeft(newStone);
		}
		if(newStone.getPoint().botleft().compareTo(oldStone.getPoint()) == 0){
			oldStone.setParentTopRight(newStone);
			newStone.setChildBotLeft(oldStone);
		}
		
		if(newStone.getPoint().topleft().compareTo(oldStone.getPoint()) == 0){
			newStone.setParentTopLeft(oldStone);
			oldStone.setChildBotRight(newStone);
		}
		if(newStone.getPoint().botright().compareTo(oldStone.getPoint()) == 0){
			oldStone.setParentTopLeft(newStone);
			newStone.setChildBotRight(oldStone);
		}
	}
	
	public void draw(){
		addStoneToField();
		for(String[] elements:field){
			for(String element : elements){
				System.out.print(element);
			}
			System.out.print("\n");
		}
	}
	
	public boolean checkPoint(BoardPoint p){
		for(Stone stone : allStones){
			if(p.compareTo(stone.getPoint()) == 0) return false;
		}
		if(maxPoint.compareTo(p) < 1){
			return true;
		}
		
		return false;
		
	}
	
	
	private void addStoneToField(){
		for(Stone stone: allStones){
			BoardPoint bp = stone.getPoint();
			int x = bp.xPos.compareTo("A");
			int y = bp.yPos;
			field[y][x+1] = stone.getColor() + "|";
		}
	}
	
	public ArrayList<Stone> getWhiteStones(){
		return whiteStones;
	}
	
	public ArrayList<Stone> getBlackStones(){
		return blackStones;
	}

}
