package game;
/**
 * 
 */

/**
 * @author Soeren Wirries
 *
 */
public class BoardPoint implements Comparable<BoardPoint> {
	public String xPos;
	public int yPos;
	
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

}
