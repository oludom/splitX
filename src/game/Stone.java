package game;


/**
 * 
 */

/**
 * @author Soeren Wirries
 *
 */
public class Stone {
	/***
	 * 
	 * 
	 */
	String colorWhite = "o";
	String colorBlack = "*";
	final BoardPoint point;
	final boolean color;
	
	Stone parentTopLeft;
	Stone parentTop;
	Stone parentTopRight;
	Stone parentLeft;
	
	Stone childRight;
	Stone childBotLeft;
	Stone childBot;
	Stone childBotRight;
	
	
	public Stone(BoardPoint p, boolean color){
		point = p;
		this.color = color;
	}
	
	public BoardPoint getPoint(){
		return point;
	}
	
	public String getColor(){
		if(color){
			return colorBlack;
		}else{
			return colorWhite;
		}
	}
	
	public boolean hasColor(){
		return color;
	}

	public boolean hasParentTopLeft(){
		if(parentTopLeft == null) return false;
		return true;
	}
	
	public void setParentTopLeft(Stone stone){
		parentTopLeft = stone;
	}
	
	public boolean hasParentTop(){
		if(parentTop == null) return false;
		return true;
	}
	
	public void setParentTop(Stone stone){
		parentTop = stone;
	}
	
	public boolean hasParentTopRight(){
		if(parentTopRight == null) return false;
		return true;
	}
	
	public void setParentTopRight(Stone stone){
		parentTopRight = stone;
	}
	
	public boolean hasParentLeft(){
		if(parentLeft == null) return false;
		return true;
	}
	
	public void setParentLeft(Stone stone){
		parentLeft = stone;
	}
	
	public boolean hasChildBot(){
		if(childBot == null) return false;
		return true;
	}
	
	public void setChildBot(Stone stone){
		childBot = stone;
	}
	
	public boolean hasChildRight(){
		if(childRight == null) return false;
		return true;
	}
	
	public void setChildRight(Stone stone){
		childRight = stone;
	}
	
	public boolean hasChildBotRight(){
		if(childBotRight == null) return false;
		return true;
	}
	
	public void setChildBotRight(Stone stone){
		childBotRight = stone;
	}
	
	public boolean hasChildBotLeft(){
		if(childBotLeft == null) return false;
		return true;
	}
	
	public void setChildBotLeft(Stone stone){
		childBotLeft = stone;
	}
	
	
	
	
	
	public int countTopBot(){
		return this.hasChildBot() ? 1 + childBot.countTopBot() : 1;
	}
	
	public int countLeftRight(){
		return this.hasChildRight() ? 1 + childRight.countLeftRight() : 1;
	}
	
	public int countTopLeftBotRight(){
		return this.hasChildBotRight() ? 1 + childBotRight.countTopLeftBotRight() : 1;
	}
	
	public int countTopRightBotLeft(){
		return this.hasChildBotLeft() ? 1 + childBotLeft.countTopRightBotLeft() : 1;
	}

	
}


