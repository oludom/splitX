package game;
import java.util.ArrayList;

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
	private BoardPoint maxPoint;
	
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
						temp[i][k] = "  ";
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
				temp[i][0] = String.valueOf(i) + "|";
				for(int k = 1; k < temp.length; k++){
					temp[i][k] = " |";
				}
			}
		}
		field = temp;
	}
	
	
	public boolean addStone(Stone stone){
		if(checkPoint(stone.getPoint())){
			
			allStones.add(stone);
			
			if(stone.hasColor()){
				blackStones.add(stone);
			}
			if(!stone.hasColor()){
				addWhite(stone);
			}
			
			return true;
		}else{
			return false;
		}
		
	}
	
	private void addWhite(Stone stone){
		for(Stone white : whiteStones){
			//System.out.println("Stone:" + stone.getPoint().left());
			//System.out.println("white:" + white.getPoint());
			
			if(stone.getPoint().top().compareTo(white.getPoint()) == 0){
				white.setChildBot(stone);
				stone.setParentTop(white);
			}
			if(stone.getPoint().bottom().compareTo(white.getPoint()) == 0){
				white.setParentTop(stone);
				stone.setChildBot(white);
			}
			
			if(stone.getPoint().right().compareTo(white.getPoint()) == 0){
				stone.setChildRight(white);
				white.setParentLeft(stone);
			}
			if(stone.getPoint().left().compareTo(white.getPoint()) == 0){
				white.setChildRight(stone);
				stone.setParentLeft(white);
			}
			
			if(stone.getPoint().topright().compareTo(white.getPoint()) == 0){
				stone.setParentTopRight(white);
				white.setChildBotLeft(stone);
			}
			if(stone.getPoint().botleft().compareTo(white.getPoint()) == 0){
				white.setParentTopRight(stone);
				stone.setChildBotLeft(white);
			}
			
			if(stone.getPoint().topleft().compareTo(white.getPoint()) == 0){
				stone.setParentTopLeft(white);
				white.setChildBotRight(stone);
			}
			if(stone.getPoint().botright().compareTo(white.getPoint()) == 0){
				white.setParentTopLeft(stone);
				stone.setChildBotRight(white);
			}
		}
		whiteStones.add(stone);
	}
	
	public int maxRow(){
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

}
