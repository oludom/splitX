package server;


import game.BoardPoint;

import java.io.Serializable;

/**
 * 
 */

/**
 * @author Micha Heiss
 *
 */
public class Packet implements Serializable {
	/**
	 * 
	 */

	public BoardPoint POINT;

	private static final long serialVersionUID = -17408814607791701L;
	public String TYPE; // Type to find new Player: player
	public String ACTION; // Action to find new Player: startGame
	public int[] DATA;
	
	public Packet( String type, String action,  BoardPoint point){
		this.POINT = point;
		this.ACTION = action;
		this.TYPE = type;
	}
	public Packet (String type, String action, int[] data){
		this.ACTION = action;
		this.TYPE = type;
		this.DATA = data;
	}
	public Packet (String type, String action){
		this.ACTION = action;
		this.TYPE = type;
	}


	@Override
	public String toString(){
		String data = "";
		String point = "";
		if(this.DATA != null) {
			for (int e : this.DATA) {
				data.concat("" + e + "  ");
			}
		}
		if(this.POINT != null){
			point = this.POINT.toString();
		}
		return "TYPE: " + this.TYPE + " ACTION: " + this.ACTION + " DATA: " + data + " POINT: " + point;
	}
	
}
