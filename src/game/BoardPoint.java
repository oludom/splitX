package game;
import java.io.Serializable;
import java.util.*;
/**
 * 
 */

/**
 * @author Soeren Wirries, Jahn Kuppinger, Micha Heiss
 *
 */
public class BoardPoint implements Comparable<BoardPoint>, Serializable{
	/**
	 * Diese Klasse stellt eine Punkt auf dem Spielfeld dar
	 */
	public String xPos; //Splaten-Position des Punktes (A - Z)
	public int yPos; //Zeilen-Position des Punktes (1-20[unendlich])
	protected static String[] alphabet = {"A", "B", "C", "D", "E", "F", "G","H","I","J", "K", "L","M", "N","O", "P","Q", "R", "S", "T", "U", "V", "W","X", "Y", "Z"};
	
	public BoardPoint(String xPos, int yPos){//Konstruktor
		this.xPos = xPos.toUpperCase();//die xPos wird immer im UpperCase gespeichert
		this.yPos = yPos;
	}
	
	/**
	 * Implementrierung der Compareable Interface zwischen 2 Punkten
	 * wenn -1 Punkt ist vor dem Anderen
	 * wenn 0 Punkt ist deckungsgleich
	 * wenn 1 Punkt ist nach dem Anderen
	 */
	@Override
	public int compareTo(BoardPoint point) {
		if((point.xPos.compareToIgnoreCase(this.xPos) > 0 || point.yPos > this.yPos) && point.yPos > 0) return 1;
		if(point.xPos.compareToIgnoreCase(this.xPos) < 0 || point.yPos < this.yPos) return -1;
		return 0;
	}
	
	/**
	 * @return BoardPoint
	 * gibt einen Punkt zurück, der von sichselbst aus nach OBEN verschoben wurde
	 */
	public BoardPoint top(){
		return new BoardPoint(xPos, prevY());
	}
	
	/**
	 * @return BoardPoint
	 * gibt einen Punkt zurück, der von sichselbst aus nach UNTEN verschoben wurde
	 */
	public BoardPoint bottom(){
		return new BoardPoint(xPos, nextY());
	}
	
	/**
	 * @return BoardPoint
	 * gibt einen Punkt zurück, der von sichselbst aus nach RECHTS verschoben wurde
	 */
	public BoardPoint right(){
		return new BoardPoint(nextX(), yPos);
	}
	
	/**
	 * @return BoardPoint
	 * gibt einen Punkt zurück, der von sichselbst aus nach LINKS verschoben wurde
	 */
	public BoardPoint left(){
		return new BoardPoint(prevX(), yPos);
	}
	
	/**
	 * @return BoardPoint
	 * gibt einen Punkt zurück, der von sichselbst aus nach OBEN-LINKS verschoben wurde
	 */
	public BoardPoint topleft(){
		return new BoardPoint(prevX(), prevY());
	}
	
	/**
	 * @return BoardPoint
	 * gibt einen Punkt zurück, der von sichselbst aus nach UNTEN-LINKS verschoben wurde
	 */
	public BoardPoint botleft(){
		return new BoardPoint(prevX(), nextY());
	}
	
	/**
	 * @return BoardPoint
	 * gibt einen Punkt zurück, der von sichselbst aus nach OBEN-RECHTS verschoben wurde
	 */
	public BoardPoint topright(){
		return new BoardPoint(nextX(), prevY());
	}
	
	/**
	 * @return BoardPoint
	 * gibt einen Punkt zurück, der von sichselbst aus nach UNTEN-RECHTS verschoben wurde
	 */
	public BoardPoint botright(){
		return new BoardPoint(nextX(), nextY());
	}
	
	/**
	 * @return String
	 * gibt den Punkt als String zurueck
	 */
	public String toString(){
		return xPos + "/" + yPos;
	}
	
	/**
	 *
	 * @return String
	 * Prueft im alphabet auf die Nummer des aktuellen Buchstaben
	 * und gibt den naechsten Buchstaben zurueck
	 * wenn Ende erreicht ist dann wird Z zurueckgegeben
	 */

	private String nextX(){

		int i = Arrays.asList(alphabet).indexOf(xPos);
		if(i == 25) return "Z";
		return alphabet[i+1];
	}
	
	/**
	 *
	 * @return String
	 * Prueft im alphabet auf die Nummer des aktuellen Buchstaben
	 * und gibt den vorherigen Buchstaben zurueck
	 * wenn Anfang erreicht ist wurd A zurueckgegeben
	 */

	private String prevX(){

		int i = Arrays.asList(alphabet).indexOf(xPos);
		if(i == 0) return "Z";
		return alphabet[i-1];
	}

	private int nextY(){
		return yPos +1;
	}

	private int prevY(){
//		if(yPos -1 >= 0){
			return yPos -1;
//		}
//		return 0;
	}

	public int getX(){
		return new String(String.join("", alphabet)).indexOf(xPos);
	}

	public static String getX(int pos){
		return alphabet[pos];
	}

}
