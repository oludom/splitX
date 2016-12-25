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
	 * Diese Klasse stellt einen Stein und die damit verbundene Logik dar
	 * 
	 */
	String colorWhite = "o";//Zeichen fuer die Farbe Weiss
	String colorBlack = "*";//Zeichen fuer die Farbe Schwarz
	final BoardPoint point;// Punkt auf den Spielbrett
	final boolean color;//Farbe des Steins
	/*
	 * Nachfolgende Parameter geben die anderen Stein der gleichen Farbe an
	 * zu denen dieser Stein eine Relation hat.
	 */
	Stone parentTopLeft;
	Stone parentTop;
	Stone parentTopRight;
	Stone parentLeft;
	
	Stone childRight;
	Stone childBotLeft;
	Stone childBot;
	Stone childBotRight;
	
	
	public Stone(BoardPoint p, boolean color){//Konstruktor
		point = p;
		this.color = color;
	}
	
	/**
	 * @return BoardPoint
	 * gibt den Punkt des Steines zurueck
	 */
	public BoardPoint getPoint(){
		return point;
	}
	
	/**
	 * @return String
	 * gibt das Farbzeichen (colorBlack /colorWhite) des Steins zurueck
	 */
	public String getColor(){
		if(color){
			return colorBlack;
		}else{
			return colorWhite;
		}
	}
	
	/**
	 * @return boolean
	 * gibt die Farbe des Steins als boolean zurueck
	 * true = schwarz
	 * false = weiss
	 */
	public boolean hasColor(){
		return color;
	}

	/**
	 * @return boolean (ob Parent vorhanden)
	 * gibt einen boolean zurueck, ob es einen Parent in dieser Position gibt
	 */
	public boolean hasParentTopLeft(){
		if(parentTopLeft == null) return false;
		return true;
	}
	
	/**
	 * @param stone
	 * Stetzt den uebergebenen Steine stone an die Parent Position
	 */
	public void setParentTopLeft(Stone stone){
		parentTopLeft = stone;
	}
	
	/**
	 * @return boolean (ob Parent vorhanden)
	 * gibt einen boolean zurueck, ob es einen Parent in dieser Position gibt
	 */
	public boolean hasParentTop(){
		if(parentTop == null) return false;
		return true;
	}
	
	/**
	 * @param stone
	 * Stetzt den uebergebenen Steine stone an die Parent Position
	 */
	public void setParentTop(Stone stone){
		parentTop = stone;
	}
	
	/**
	 * @return boolean (ob Parent vorhanden)
	 * gibt einen boolean zurueck, ob es einen Parent in dieser Position gibt
	 */
	public boolean hasParentTopRight(){
		if(parentTopRight == null) return false;
		return true;
	}
	
	/**
	 * @param stone
	 * Stetzt den uebergebenen Steine stone an die Parent Position
	 */
	public void setParentTopRight(Stone stone){
		parentTopRight = stone;
	}
	
	/**
	 * @return boolean (ob Parent vorhanden)
	 * gibt einen boolean zurueck, ob es einen Parent in dieser Position gibt
	 */
	public boolean hasParentLeft(){
		if(parentLeft == null) return false;
		return true;
	}
	
	/**
	 * @param stone
	 * Stetzt den uebergebenen Steine stone an die Parent Position
	 */
	public void setParentLeft(Stone stone){
		parentLeft = stone;
	}
	
	/**
	 * @return boolean (ob Child vorhanden)
	 * gibt einen boolean zurueck, ob es einen Child in dieser Position hat
	 */
	public boolean hasChildBot(){
		if(childBot == null) return false;
		return true;
	}
	
	/**
	 * @param stone
	 * Stetzt den uebergebenen Steine stone an die Child Position
	 */
	public void setChildBot(Stone stone){
		childBot = stone;
	}
	

	public Stone getChildBot(){
		return childBot;
	}
	

	/**
	 * @return boolean (ob Child vorhanden)
	 * gibt einen boolean zurueck, ob es einen Child in dieser Position hat
	 */
	public boolean hasChildRight(){
		if(childRight == null) return false;
		return true;
	}
	
	/**
	 * @param stone
	 * Stetzt den uebergebenen Steine stone an die Child Position
	 */
	public void setChildRight(Stone stone){
		childRight = stone;
	}
	
	public Stone getChildRight(){
		return childRight;
	}

	/**
	 * @return boolean (ob Child vorhanden)
	 * gibt einen boolean zurueck, ob es einen Child in dieser Position hat
	 */
	public boolean hasChildBotRight(){
		if(childBotRight == null) return false;
		return true;
	}
	
	/**
	 * @param stone
	 * Stetzt den uebergebenen Steine stone an die Child Position
	 */
	public void setChildBotRight(Stone stone){
		childBotRight = stone;
	}
	

	public Stone getChildBotRight(){
		return childBotRight;
	}

	/**
	 * @return boolean (ob Child vorhanden)
	 * gibt einen boolean zurueck, ob es einen Child in dieser Position hat
	 */
	public boolean hasChildBotLeft(){
		if(childBotLeft == null) return false;
		return true;
	}
	
	/**
	 * @param stone
	 * Stetzt den uebergebenen Steine stone an die Child Position
	 */
	public void setChildBotLeft(Stone stone){
		childBotLeft = stone;
	}
	
	public Stone getChildBotLeft(){
		return childBotLeft;
	}

	/**
	 * @return int (Anzahl der Children)
	 * prueft ob der Stein ein Child an der Position UNTEN hat
	 * JA -> aufruf diese Funktion beim Child auf
	 * NEIN -> gibt 1 zurueck
	 * Die Methode ruft recursiv alle Children in der senkrechten Kette von oben nach unten auf.
	 */
	public int countTopBot(){
		return this.hasChildBot() ? 1 + childBot.countTopBot() : 1;
	}
	
	/**
	 * @return int (Anzahl der Children)
	 * prueft ob der Stein ein Child an der Position RECHTS hat
	 * JA -> aufruf diese Funktion beim Child auf
	 * NEIN -> gibt 1 zurueck
	 * Die Methode ruft recursiv alle Children in der horizontalen Kette von link nach rechts auf.
	 */
	public int countLeftRight(){
		return this.hasChildRight() ? 1 + childRight.countLeftRight() : 1;
	}
	
	/**
	 * @return int (Anzahl der Children)
	 * prueft ob der Stein ein Child an der Position UNTEN-RECHTS hat
	 * JA -> aufruf diese Funktion beim Child auf
	 * NEIN -> gibt 1 zurueck
	 * Die Methode ruft recursiv alle Children in der diagonalen Kette von oben-links nach unten-rechts auf.
	 */
	public int countTopLeftBotRight(){
		return this.hasChildBotRight() ? 1 + childBotRight.countTopLeftBotRight() : 1;
	}
	
	/**
	 * @return int (Anzahl der Children)
	 * prueft ob der Stein ein Child an der Position UNTEN-LINKS hat
	 * JA -> aufruf diese Funktion beim Child auf
	 * NEIN -> gibt 1 zurueck
	 * Die Methode ruft recursiv alle Children in der diagonalen Kette von oben-rechts nach unten-links auf.
	 */
	public int countTopRightBotLeft(){
		return this.hasChildBotLeft() ? 1 + childBotLeft.countTopRightBotLeft() : 1;
	}

	
}


