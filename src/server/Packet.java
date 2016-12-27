package server;


import game.BoardPoint;

import java.io.Serializable;

/**
 * 
 */

/**
 * @author Wirries
 *
 */
public class Packet implements Serializable {
	/**
	 * 
	 */

	public BoardPoint point;

	private static final long serialVersionUID = -17408814607791701L;
	public String TYPE; // Type to find new Player: player
	public String ACTION; // Action to find new Player: startGame
	public int[] DATA;
	
	public Packet( String type, String action,  BoardPoint point){
		this.point = point;
		this.ACTION = action;
		this.TYPE = type;
	}
	public Packet (String type, String action, int[] data){
		this.ACTION = action;
		this.TYPE = type;
		this.DATA = data;
	}

	
}
