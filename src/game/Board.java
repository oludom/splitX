package game;
import java.util.ArrayList;
import game.GameException.*;

/**
 * 
 */

/**
 * @author Soeren Wirries
 *
 */
public class Board {
	/**
	 * Diese Klasse stellt das Spielbrett des Spiel dar. 
	 */
	private final int dimension; //wird im Konstruktor mit der Groesse geuellt
	private String[][] field;// stellt das Spielfeld dar
	private String[] alphabet = {"A", "B", "C", "D", "E", "F", "G","H","I","J", "K", "L","M", "N","O", "P","Q", "R", "S", "T", "U", "V", "W","X", "Y", "Z"};
	private ArrayList<Stone> allStones = new ArrayList<Stone>(); //Liste mit allen Steinen auf dem Brett
	private ArrayList<Stone> blackStones = new ArrayList<Stone>();//Liste aller schwarzen Steine
	private ArrayList<Stone> whiteStones = new ArrayList<Stone>();//Liste aller weissen Steine
	String trenn = "|";//das Trennzeichen
	private int border = 1; //Gibt die Breite des Randes an (Groesse wird damit erweitert)
	public BoardPoint maxPoint;//Stellt den groesst moeglichen Punkt dar
	
	public Board(int dimension){// Konstruktor der Klasse
		this.dimension = dimension;
		maxPoint = new BoardPoint(alphabet[dimension], dimension);
		generateField();//Erzeugt ein leers Feld mit Beschriftung
	}
	/**
	 * Erzeugt ein leeres Spielfeld mit der Randbeschriftung
	 * es wird ein 2dim. Array erzuegt, mit leeren Strings und Trennzeihen gefüllt
	 * und dem globalen "field" zugewiesen
	 */
	private void generateField(){
		/**  A| B| C| D| E| F| G| H|
		 * 1| |  |  |  |  |  |  |  |
		 * 2| |  |  |  |  |  |  |  |
		 * 3| |  |  |  |  |  |  |  |
		 * 4| |  |  |  |  |  |  |  |
		 * 5| |  |  |  |  |  |  |  |
		 * 6| |  |  |  |  |  |  |  |
		 * 7| |  |  |  |  |  |  |  |
		 * 8| |  |  |  |  |  |  |  |
		 * 
		 */
		
		
		String[][] temp = new String[dimension + border][dimension +border];
		//Fuellt erst alle Arrayplaetze mir den leeren String (sonst steht NULL im Array)
		for(int i = 0; i < temp.length; i++){
			for(int k = 0; k < temp.length; k++){
				temp[i][k] = "";
			}
		}
		
		for(int i = 0; i<temp.length; i++ ){
			//Erstellt die obere Beschriftung mit den Buchstaben
			if(i == 0){
				for(int k = 0; k<temp.length; k++){
					if(k < border){
						temp[i][k] = "   ";
					}else{
						temp[i][k] = alphabet[k - border]+ trenn;
					}
					
				}
			}
			//Fuellt die restlichen Zeilen mit dem leeren String und dem Trennzeichen
			// und die erste Spalte immer mit den Spaltennummern und dem Trennzeichen
			if(i > 0){
				if(i < 10){
					temp[i][0] =" " + String.valueOf(i) + trenn;
				}else{
					temp[i][0] = String.valueOf(i) + trenn;
				}
				
				for(int k = 1; k < temp.length; k++){
					temp[i][k] = " "+trenn;
				}
			}
		}
		field = temp;
	}
	/**
	 * 
	 * @param stone
	 * @return boolean (ob hinzufuegen erfolgreich)
	 * @throws BoardOutOfBoundException
	 * 
	 * prüft ob der Stein auf dem Brett liegt sonst @throws 
	 * Fuegt dem Brett einen Stein hinzu.
	 * fuegt den Stein den ArrayLists hinzu (allStones) und ruft die Methoden fuer die Farben auf.
	 */
	public boolean addStone(Stone stone) throws BoardOutOfBoundException{
		if(checkPoint(stone.getPoint())){
			
			allStones.add(stone);
			
			if(stone.hasColor()){
				addBlack(stone);
			}
			if(!stone.hasColor()){
				addWhite(stone);
			}
			
			return true;
		}else{
			throw new BoardOutOfBoundException();
		}
		
	}
	/**
	 * 
	 * @param stone
	 * prueft die Relation zu allen Steinen der Farbe Weiss und setzt diese (setParancy)
	 * fuegt den Stein der Liste whiteStones hinzu
	 */
	private void addWhite(Stone stone){
		for(Stone white : whiteStones){
			
			setParency(stone, white);
			
		}
		whiteStones.add(stone);
	}
	
	/**
	 * 
	 * @param stone
	 * prueft die Relation zu allen Steinen der Farbe Schwart und setzt diese (setParancy)
	 * fuegt den Stein der Liste blackStones hinzu
	 */
	private void addBlack(Stone stone){
		for(Stone black : blackStones){
			
			setParency(stone, black);
			
		}
		blackStones.add(stone);
	}
	
	/**
	 * 
	 * @return int (max. Anzahl der Stein in einer Reihe)
	 * Prueft alle weissen Steine auf die Laenge der Reihen
	 * Jedoch werden nur dann berechnet, wenn kein Stein ueber diesem liegt (hasParentXX())
	 */
	public int maxRowWhite(){
		int row = 0;
		for(Stone stone : whiteStones){
			if(!stone.hasParentTop()){//Wenn kein Parent da ist...
				int temp = stone.countTopBot();//zaehle alle Steine unter dir
				if(temp > row) row = temp;//wenn groesser als die laengste bisherige Reihe dann ueberschreiben
			}
			if(!stone.hasParentLeft()){
				int temp = stone.countLeftRight();
				if(temp > row) row = temp;
			}
			if(!stone.hasParentTopLeft()){
				int temp = stone.countTopLeftBotRight();
				if(temp > row) row = temp;
			}
			if(!stone.hasParentTopRight()){
				int temp = stone.countTopRightBotLeft();
				if(temp > row) row = temp;
			}
			
		}
		return row;//gibt die laengste Reihe zurueck
	}
	
	/**
	 * 
	 * @return int (max. Anzahl der Stein in einer Reihe)
	 * Prueft alle schwarzen Steine auf die Laenge der Reihen
	 * Jedoch werden nur dann berechnet, wenn kein Stein ueber diesem liegt (hasParentXX())
	 */
	public int maxRowBlack(){
		int row = 0;
		for(Stone stone : blackStones){
			if(!stone.hasParentTop()){
				int temp = stone.countTopBot();
				if(temp > row) row = temp;
			}
			if(!stone.hasParentLeft()){
				int temp = stone.countLeftRight();
				if(temp > row) row = temp;
			}
			if(!stone.hasParentTopLeft()){
				int temp = stone.countTopLeftBotRight();
				if(temp > row) row = temp;
			}
			if(!stone.hasParentTopRight()){
				int temp = stone.countTopRightBotLeft();
				if(temp > row) row = temp;
			}
			
		}
		return row;
	}
	
	/**
	 * 
	 * @throws GameWonException
	 * prueft beide Farben, ob es eine Reihe mit mehr als 5 Steinen gibt -> ja @throws
	 */
	public void checkWinner() throws GameWonException{
		if(this.maxRowBlack() > 5){
			throw new GameWonException(true);
		}
		if(this.maxRowWhite() > 5){
			throw new GameWonException(false);
		}
	}
	
	/**
	 * 
	 * @param newStone
	 * @param oldStone
	 * pueft und sezt die Reation der Steine zwischen den aktuell gesetzen (oldStone) und dem Neugesetzten (newStone)
	 */
	private void setParency(Stone newStone,Stone oldStone){
		/**
		 * der Neu verschiebt seine Punkt (BoardPoint) an die entsprechenden Stellen und vergleicht diese mit dem Alten
		 * wenn die Stelle deckungsgleich ist wird fuer beide Steine die Relation gesetzt (setChild, setParent)
		 */
		if(newStone.getPoint().top().compareTo(oldStone.getPoint()) == 0){
			oldStone.setChildBot(newStone);
			newStone.setParentTop(oldStone);
		}
		if(newStone.getPoint().bottom().compareTo(oldStone.getPoint()) == 0){
			oldStone.setParentTop(newStone);
			newStone.setChildBot(oldStone);
		}
		
		if(newStone.getPoint().right().compareTo(oldStone.getPoint()) == 0){
			newStone.setChildRight(oldStone);
			oldStone.setParentLeft(newStone);
		}
		if(newStone.getPoint().left().compareTo(oldStone.getPoint()) == 0){
			oldStone.setChildRight(newStone);
			newStone.setParentLeft(oldStone);
		}
		
		if(newStone.getPoint().topright().compareTo(oldStone.getPoint()) == 0){
			newStone.setParentTopRight(oldStone);
			oldStone.setChildBotLeft(newStone);
		}
		if(newStone.getPoint().botleft().compareTo(oldStone.getPoint()) == 0){
			oldStone.setParentTopRight(newStone);
			newStone.setChildBotLeft(oldStone);
		}
		
		if(newStone.getPoint().topleft().compareTo(oldStone.getPoint()) == 0){
			newStone.setParentTopLeft(oldStone);
			oldStone.setChildBotRight(newStone);
		}
		if(newStone.getPoint().botright().compareTo(oldStone.getPoint()) == 0){
			oldStone.setParentTopLeft(newStone);
			newStone.setChildBotRight(oldStone);
		}
	}
	
	/**
	 * fuegt die Steine dem Array hinzu
	 * gibt das Spielfelde (field) aus
	 */
	public void draw(){
		addStoneToField();
		for(String[] elements:field){
			for(String element : elements){
				System.out.print(element);
			}
			System.out.print("\n");
		}
	}
	
	/**
	 * 
	 * @param boardpoint
	 * @return boolean (ob Punkt auf Brett und nicht belegt)
	 * Prueft erst ob der Punkt belegt ist mit allStones
	 * Prueft ob der Punkt auf dem Spielfeld liegt mit maxPoint 
	 */
	public boolean checkPoint(BoardPoint boardpoint){
		for(Stone stone : allStones){
			if(boardpoint.compareTo(stone.getPoint()) == 0) return false;
		}
		if(maxPoint.compareTo(boardpoint) < 1){
			/**
			 * wenn 0 dann ist es der maxPoint
			 * wenn -1 dann ist er kleiner also innerhalb
			 * wenn 1 dann ist er groesser also ausserhalb
			 */
			return true;
		}
		
		return false;
		
	}
	
	/**
	 * setzt die Steine auf das Brett (field)
	 */
	private void addStoneToField(){
		for(Stone stone: allStones){
			BoardPoint bp = stone.getPoint();
			int x = bp.xPos.compareTo("A");//gibt die Position in der Relation zum Buchstaben im Alphabet aus (bei A = 0, bei B =1 usw)
			int y = bp.yPos;
			field[y][x+1] = stone.getColor() + trenn;//erstezt das Feld mit dem Farbsymbol des Steins und dem Trennzeichen
		}
	}

}
