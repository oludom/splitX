package server;


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
	private static final long serialVersionUID = -17408814607791701L;
	public String TYPE;
	public String ACTION;
	public String[] DATA;
	
	public Packet(String type, String action, String[] data){
		this.ACTION = action;
		this.TYPE = type;
		this.DATA = data;
	}
	
	
}
