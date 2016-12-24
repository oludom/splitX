package game;
import java.util.*;
/**
 * 
 */

/**
 * @author Soeren Wirries
 *
 */

public class BoardPoint implements Comparable<BoardPoint> {
	/**
	 * Diese Klasse stellt eine Punkt auf dem Spielfeld dar
	 */
	public String xPos; //Splaten-Position des Punktes (A - Z)
	public int yPos; //Zeilen-Position des Punktes (1-20[unendlich])
	private String[] alphabet = {"A", "B", "C", "D", "E", "F", "G","H","I","J", "K", "L","M", "N","O", "P","Q", "R", "S", "T", "U", "V", "W","X", "Y", "Z"};
	
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
		if(point.xPos.compareToIgnoreCase(this.xPos) > 0 || point.yPos > this.yPos) return 1;
		if(point.xPos.compareToIgnoreCase(this.xPos) < 0 || point.yPos < this.yPos) return -1;
		return 0;
	}
	
	/**
	 * @return BoardPoint
	 * gibt einen Punkt zurück, der von sichselbst aus nach OBEN verschoben wurde
	 */
	public BoardPoint top(){
		return new BoardPoint(xPos, yPos+1);
	}
	
	/**
	 * @return BoardPoint
	 * gibt einen Punkt zurück, der von sichselbst aus nach UNTEN verschoben wurde
	 */
	public BoardPoint bottom(){
		return new BoardPoint(xPos, yPos-1);
	}
	
	/**
	 * @return BoardPoint
	 * gibt einen Punkt zurück, der von sichselbst aus nach RECHTS verschoben wurde
	 */
	public BoardPoint right(){
		return new BoardPoint(next(), yPos);
	}
	
	/**
	 * @return BoardPoint
	 * gibt einen Punkt zurück, der von sichselbst aus nach LINKS verschoben wurde
	 */
	public BoardPoint left(){
		return new BoardPoint(prev(), yPos);
	}
	
	/**
	 * @return BoardPoint
	 * gibt einen Punkt zurück, der von sichselbst aus nach OBEN-LINKS verschoben wurde
	 */
	public BoardPoint topleft(){
		return new BoardPoint(prev(), yPos+1);
	}
	
	/**
	 * @return BoardPoint
	 * gibt einen Punkt zurück, der von sichselbst aus nach UNTEN-LINKS verschoben wurde
	 */
	public BoardPoint botleft(){
		return new BoardPoint(prev(), yPos-1);
	}
	
	/**
	 * @return BoardPoint
	 * gibt einen Punkt zurück, der von sichselbst aus nach OBEN-RECHTS verschoben wurde
	 */
	public BoardPoint topright(){
		return new BoardPoint(next(), yPos+1);
	}
	
	/**
	 * @return BoardPoint
	 * gibt einen Punkt zurück, der von sichselbst aus nach UNTEN-RECHTS verschoben wurde
	 */
	public BoardPoint botright(){
		return new BoardPoint(next(), yPos-1);
	}
	
	/**
	 * @return String
	 * gibt den Punkt als String zurueck
	 */
	public String toString(){
		return xPos + " " + yPos;
	}
	
	/**
	 * 
	 * @return String
	 * Prueft im alphabet auf die Nummer des aktuellen Buchstaben
	 * und gibt den naechsten Buchstaben zurueck
	 * wenn Ende erreicht ist dann wird Z zurueckgegeben
	 */
	private String next(){
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
	private String prev(){
		int i = Arrays.asList(alphabet).indexOf(xPos);
		if(i == 0) return "A";
		return alphabet[i-1];
	}

}
