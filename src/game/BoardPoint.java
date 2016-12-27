package game;
import java.io.Serializable;
import java.util.*;
/**
 * 
 */

/**
 * @author Soeren Wirries
 *
 */
public class BoardPoint implements Comparable<BoardPoint>, Serializable{
	public String xPos;
	public int yPos;
	private String[] alphabet = {"A", "B", "C", "D", "E", "F", "G","H","I","J", "K", "L","M", "N","O", "P","Q", "R", "S", "T", "U", "V", "W","X", "Y", "Z"};
	
	public BoardPoint(String xPos, int yPos){
		this.xPos = xPos.toUpperCase();
		this.yPos = yPos;
	}
	
	
	@Override
	public int compareTo(BoardPoint point) {
		if(point.xPos.compareToIgnoreCase(this.xPos) > 0 || point.yPos > this.yPos) return 1;
		if(point.xPos.compareToIgnoreCase(this.xPos) < 0 || point.yPos < this.yPos) return -1;
		return 0;
	}
	
	public BoardPoint top(){
		return new BoardPoint(xPos, yPos+1);
	}
	
	public BoardPoint bottom(){
		return new BoardPoint(xPos, yPos-1);
	}
	
	public BoardPoint right(){
		return new BoardPoint(next(), yPos);
	}
	
	public BoardPoint left(){
		return new BoardPoint(prev(), yPos);
	}
	
	public BoardPoint topleft(){
		return new BoardPoint(prev(), yPos+1);
	}
	
	public BoardPoint botleft(){
		return new BoardPoint(prev(), yPos-1);
	}
	
	public BoardPoint topright(){
		return new BoardPoint(next(), yPos+1);
	}
	
	public BoardPoint botright(){
		return new BoardPoint(next(), yPos-1);
	}
	
	public String toString(){
		return xPos + " " + yPos;
	}
	
	private String next(){
		int i = Arrays.asList(alphabet).indexOf(xPos);
		
		return alphabet[i+1];
	}
	
	private String prev(){
		int i = Arrays.asList(alphabet).indexOf(xPos);
		if(i == 0) return "A";
		return alphabet[i-1];
	}

}
