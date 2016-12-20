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
	private int border = 1;
	private BoardPoint maxPoint;
	
	public Board(int dimension){
		this.dimension = dimension;
		maxPoint = new BoardPoint(alphabet[dimension], dimension);
		generateField();
	}
	
	private void generateField(){
		/**  A B C D E F G H
		 *   - - - - - - - -
		 * 1|  
		 * 2|
		 * 3|
		 * 4|
		 * 5|
		 * 6|
		 * 7|
		 * 8|
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
			return true;
		}else{
			return false;
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

}
