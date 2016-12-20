package game;


/**
 * 
 */

/**
 * @author Soeren Wirries
 *
 */
public class Stone {
	final BoardPoint point;
	final boolean color;
	
	public Stone(BoardPoint p, boolean color){
		point = p;
		this.color = color;
	}
	
	public BoardPoint getPoint(){
		return point;
	}
	
	public String getColor(){
		if(color){
			return "*";
		}else{
			return "o";
		}
	}
	
	
	
}

interface SimpleTreeNode {
	public void addChild (SimpleTreeNode child);
	public void addParent(SimpleTreeNode parent);
	public int getChildCnt();
	public SimpleTreeNode getChild (int pos);
	public SimpleTreeNode getParent (int pos);
}
