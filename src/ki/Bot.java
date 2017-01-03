package ki;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


import game.*;
import game.GameException.*;

public class Bot {
	/**
	 * Diese Klasse beschreibt einen Bot gegen den man Spielen kann.
	 * Allgemeine Funktionen:
	 * Der Bot kennt das Spielfeld und seine eigene Farbe. Ueber diese Eigenschaften kann er sich alle Informationen zum
	 * Spiel beschaffen. Er sammelt alle moeglichen Spielzeuge und entscheidet via Zufall welche er nutzt.
	 * Alle moeglichen Entscheidungen werden durch zwei Modi erstellt.
	 * 1. HardMode
	 * 2. EasyMode
	 * Je nach Mode werden die Moeglichkeiten angepasst bzw. "vorausschauender" gestaltet.
	 * 
	 * Eigenschaften des EasyMode:
	 * - Start: der Startpunkt wird zufaellig bestimmt.
	 * - 1. Wenn 4 oder mehr Steine des Gegners eine Reihe bilden, setzt der Bot nur an diese Reihe(n)
	 * - 2. Wenn 3 oder mehr Steine des Gegners in einer Reihe liegen, werden diese in den moeglichen Zuegen beruecksichtigt
	 *   (greift auch wenn alle 4er Ketten bereits blockiert sind)
	 * - 3. Wenn 4 oder mehr eigene Steine eine Reihe bilden, werden die Reihen bevorzugt vervollstaendigt + moeglichkeiten aus 2.
	 *   (prueft aber nicht ob noch 2 Steine Platz haben)
	 * - 4. Wenn eigenen Reihen zu kurz (<4) werden alle Moeglichkeiten zum Pool hinzugefuegt, auch hier werden die Moeglichkeiten aus 2.
	 *   beruecksichtigt.
	 * - 5. Wenn keine Moeglichkeiten gefunden werden, wird ein zufaelliger Punkt genutzt. 
	 * 
	 * Besonderheiten des HardMode:
	 * - Start: der Bot legt, wenn er weiß ist seine ersten Steine an die des Gegners
	 * - 1. Wenn Gegner mehr als 3 Steine in einer Reihe hat, prueft der Bot ob sich in der Reihe eine Luecke befindet
	 * - 2. Wenn der HardMode keine Moeglichkeiten findet wird der EasyMode genutzt
	 */
	private Board board;//Das Speilfeld
	private boolean color;//die eigene Farbe
	private String[] alphabet = {"A", "B", "C", "D", "E", "F", "G","H","I","J", "K", "L","M", "N","O", "P","Q", "R", "S", "T", "U", "V", "W","X", "Y", "Z"};
	ArrayList<Stone> ownStone = new ArrayList<Stone>();//Liste der eigenen Steine
	ArrayList<Stone> oppStone = new ArrayList<Stone>();//List der gegnerischen Steine
	int ownMaxRow = 0;//Anzahl der eigene Steine in einer Reihe
	int oppMaxRow = 0;//Anzahl der gegnerischen Steine in einer Reihe
	boolean enableHardMode = false;//wenn true HardMode
	boolean enableLog = false;//wenn true Logausgebe ueber die Entscheidungen
	
	
	public Bot(Board board, boolean color){//1. Konstruktor
		this.board = board;
		this.color = color;
		
		update();
	}
	
	public Bot(Board board, boolean color, boolean enableHardMode){//2. Konstruktor
		this.board = board;
		this.color = color;
		this.enableHardMode = enableHardMode;
		update();
	}
	
	public Bot(Board board, boolean color, boolean enableHardMode, boolean enableLog){//3. Konstruktor
		this.board = board;
		this.color = color;
		this.enableHardMode = enableHardMode;
		this.enableLog = enableLog;
		update();
	}
	/**
	 * aktualisiert die Infos aus den aktuellen Spielfeld
	 * der Farbe entsprechend werden die Infos zu gewiesen
	 */
	private void update(){
		if(color){
			oppStone = board.getWhiteStones();
			ownStone = board.getBlackStones();
			ownMaxRow = board.maxRowBlack();
			oppMaxRow = board.maxRowWhite();
		}else{
			ownStone = board.getWhiteStones();
			oppStone = board.getBlackStones();
			ownMaxRow = board.maxRowWhite();
			oppMaxRow = board.maxRowBlack();
		}
		
	}
	
	/**
	 * setzt den naechsten Stein aufs Feld.
	 * vorher werden die Infos aktualliesiert (update())
	 * je nach Mode wird die Auswahl getroffen (HardMode,EasyMode)
	 */
	public void next(){
		update();
		boolean easy = true;
		if(enableHardMode){
			easy = useHardMode();
		}
		if(easy){
			useEasyMode();
		}
		
	}
	
	/**
	 * @return boolean (ob Hardmode erfolgreich)
	 * sammelt alle Moeglichkeiten fuer einen Stein
	 * Wenn Moeglichkeiten vorhanden, wird per Zufalle eine ausgewaehlt
	 * Wenn keine wird false zurueck gegeben 
	 */
	private boolean useHardMode(){
		HashMap<Integer, BoardPoint> finalMap = new HashMap<>();//Liste aller Moeglichkeiten
		Random random = new Random();//Zufallsgenerator
		if(!color && oppStone.size() ==1){//Wenn Bot weiss und der Gegner einen Stein gesetzt hat (erster Zug)
			finalMap = addToLine(finalMap, oppStone, oppMaxRow,1);//erweitert Liste um waagerechte Punkte
			finalMap = addToLine(finalMap, oppStone, oppMaxRow,2);//erweitert Liste um diagonale Punkte (Links>Rechts)
			finalMap = addToLine(finalMap, oppStone, oppMaxRow,3);//erweitert Liste um senkrechte Punkte
			finalMap = addToLine(finalMap, oppStone, oppMaxRow,4);//erweitert Liste um diagonale Punkte (Rechts>Links)
		}else if(oppMaxRow >= 3){//Wenn der Gegner mehr als 3 Steine in einer Reihe hat
			/*
			 * Im folgendne werden alle Reihen aller Richtungen(waage,diagoanl,senkrecht) geprueft
			 * ob sie mehr als 3 Steine besitzen, wenn ja ob man Steine anfuegen kann. (addToLine)
			 * ob diese Reihe eine Luecke besitzt und danach wieder ein Stein des Gegners liegt (checkSpace)
			 *    wenn negativ dann werden alle Stein geloescht
			 */
			int startMap = finalMap.size();//setzten des Listenanfangs fuer diese Abfragen
			finalMap = addToLine(finalMap, oppStone, 3,1);//Liste aller Moeglichkeiten
			if(enableLog) System.out.println("HardMode vor 1.Map:"+finalMap);//wenn aktiv Ausgabe der gefundenen Moeglichkeiten
			finalMap = checkSpace(finalMap, oppStone, 1, startMap);//pruefen ob Reihe Luecke besitzt
			if(enableLog) System.out.println("HardMode nach 1.Map:"+finalMap);//wenn aktiv Ausgabe der bliebenden Moeglichkeiten
			finalMap = convertToStartHashMap(finalMap);//sortiert die Liste wieder auf StartKeyValue
			
			startMap = finalMap.size();
			finalMap = addToLine(finalMap, oppStone, 3,2);
			if(enableLog) System.out.println("HardMode vor 2.Map:"+finalMap);
			finalMap = checkSpace(finalMap, oppStone, 2, startMap);
			if(enableLog) System.out.println("HardMode nach 2.Map:"+finalMap);
			finalMap = convertToStartHashMap(finalMap);
			
			startMap = finalMap.size();
			finalMap = addToLine(finalMap, oppStone, 3,3);
			if(enableLog) System.out.println("HardMode vor 3.Map:"+finalMap);
			finalMap = checkSpace(finalMap, oppStone, 3, startMap);
			if(enableLog) System.out.println("HardMode nach 3.Map:"+finalMap);
			finalMap = convertToStartHashMap(finalMap);
			
			startMap = finalMap.size();
			finalMap = addToLine(finalMap, oppStone, 3,4);
			if(enableLog) System.out.println("HardMode vor 4.Map:"+finalMap);
			finalMap = checkSpace(finalMap, oppStone, 4, startMap);
			if(enableLog) System.out.println("HardMode nach 4.Map:"+finalMap);
			finalMap = convertToStartHashMap(finalMap);
		}
		
		if(enableLog) System.out.println("HardMode Map:"+finalMap);
		
		if(finalMap.size() != 0){//Wenn Moeglichkeiten gefunden wurden
			
			finalMap = convertToStartHashMap(finalMap);//sortiert die Liste wieder auf StartKeyValue
			int ranInt = random.nextInt(finalMap.size());//Erstellt eine Zufallszahl aus den Moeglichkeiten
			
			if(enableLog) System.out.println("HardMode Map:"+finalMap);
			if(enableLog) System.out.println("Random:"+ranInt);
			
			try{
				board.addStone(new Stone(finalMap.get(ranInt) ,color));//fuegt den Stein hinzu
			}catch (Exception e) {//sollte idR nicht aufgerufen werden, da die Punkte schon geprueft wurden
				System.out.println("HardMode Exception");
				return true;
			}
			return false;
		}else{
			return true;
		}
		// Wenn keine Steine gesetzt werden konnten (keine Moeglichkeiten), wird durch true der EasyMode ausgefuehrt
	}
	
	/**
	 * sammlet alle Moeglichkeiten
	 * wenn keien gefunden wurde wird eine per Zufall ermittelt (addRandom)
	 */
	private void useEasyMode(){
		HashMap<Integer, BoardPoint> finalMap = new HashMap<>();//Liste aller Moeglichkeiten
		Random random = new Random();//Zufallsgenerator
		boolean withOwn = true;//wenn eigene Steine mit in die Auswahl sollen
		if(oppMaxRow >= 4){//Wenn der Gegner mehr als 4 Steine hat
			finalMap = addToLine(finalMap, oppStone, 4,1);//erweitert Liste um waagerechte Punkte
			finalMap = addToLine(finalMap, oppStone, 4,2);//erweitert Liste um diagonale Punkte (Links>Rechts)
			finalMap = addToLine(finalMap, oppStone, 4,3);//erweitert Liste um senkrechte Punkte
			finalMap = addToLine(finalMap, oppStone, 4,4);//erweitert Liste um diagonale Punkte (Rechts>Links
			if(enableLog) System.out.println("OppMaxMap:"+finalMap);
			if(finalMap.size() != 0) withOwn = false;//wenn keine Moeglichkeiten gefunden wurde, sollen eigene hinzugefuegt werden
		}
		if(oppMaxRow >=3 && withOwn){//Wenn der Gegner mehr als 3 Steine und Eigene hinzugefuegt werden sollen
			finalMap = addToLine(finalMap, oppStone, 3,1);
			finalMap = addToLine(finalMap, oppStone, 3,2);
			finalMap = addToLine(finalMap, oppStone, 3,3);
			finalMap = addToLine(finalMap, oppStone, 3,4);
			if(enableLog) System.out.println("OppMap:"+finalMap);
		}
		
		
		
		if(withOwn && ownStone.size() > 0){//Wenn man Eigene hinzufuegen soll und schon einen Stein gesetzt hat
			boolean firstrun = true;//ob es der erste durchlauf ist
			int runOverFlow = 0;//Abbruch nach zuvielen duchlaeufen
			do{
				if(ownMaxRow >= 4 && firstrun){//wenn man mehr als 4 Steine in einer Reihe hat und es der erste Lauf ist
					finalMap = addToLine(finalMap, ownStone, ownMaxRow-1,1);
					if(enableLog) System.out.println("MaxRow 1. Map:"+finalMap);
					
					finalMap = addToLine(finalMap, ownStone, ownMaxRow-1,2);
					if(enableLog) System.out.println("MaxRow 2. Map:"+finalMap);
					
					finalMap = addToLine(finalMap, ownStone, ownMaxRow-1,3);
					if(enableLog) System.out.println("MaxRow 3. Map:"+finalMap);
					
					finalMap = addToLine(finalMap, ownStone, ownMaxRow-1,4);
					if(enableLog) System.out.println("MaxRow 4. Map:"+finalMap);
					firstrun = false;
					
				}else{
					int breakpoint = 0;//wert wie lang die Reihen seinsollen um als Moeglichkeit zu gelten (laengere Reihen bevorzugt erweitern)
					
					if(ownStone.size() > 4 && runOverFlow < 5){//Wenn man mehr als 4 Steine auf dem Feld hat und weniger als 5 durchlaufe genutzt wurden
						breakpoint = 2;//es werden nur Reihen mit min 2 Steinen aufgelistet
					}
					finalMap = addToLine(finalMap, ownStone, breakpoint,1);
					finalMap = addToLine(finalMap, ownStone, breakpoint,2);
					finalMap = addToLine(finalMap, ownStone, breakpoint,3);
					finalMap = addToLine(finalMap, ownStone, breakpoint,4);
				}
				runOverFlow++;//pro durchlauf wird er Overflow erhoeht (um Endlos-Schleifen zu unterbinden)
			}while(finalMap.size() == 0);//wenn min eine Moeglichkeit gefunden wurde
		}
		
		if(finalMap.size() != 0){//Wenn  min 1 Moeglichkeit gefunden wurde (trifft immer zu ausser beim ersten Stein)
			int ranInt = random.nextInt(finalMap.size());//Erstellt eine Zufallszahl aus den Moeglichkeiten
			if(enableLog) System.out.println("Map:"+finalMap);
			if(enableLog) System.out.println("Random:"+ranInt);
			try{
				board.addStone(new Stone(finalMap.get(ranInt) ,color));//Fuegt Stein dem Board hinzu
			}catch (Exception e) {
				System.out.println("EasyMode Exception");
			}
		}else{
			addRandom();//Wenn keine Moeglichkeit gefunden wurde, oder es sich um den ersten Stein handelt
		}
	}
	
	/**
	 * 
	 * @param finalMap Liste aller gefundenen Moeglichkeiten (wird erweitert)
	 * @param stoneList Liste der Steine einer Farbe (je nach Pruefung entweder eigene oder gegnerische)
	 * @param breakpoint Zahl die bestimmt wie lang die Reihe sein muss um aufgenommen zu werden.
	 * @param line Zahl die bestimmt welche Richtung genutzt werden muss 
	 *         (1=waagerecht, 2=diagonal von Links nach Rechts, 3=senkrecht, 4=diagonal von Rechts nach Linsk)
	 * @return HashMap Liste vorherigen Moeglichkeit und den neuen Moeglichkeiten
	 */
	private HashMap<Integer, BoardPoint> addToLine(HashMap<Integer, BoardPoint> finalMap, ArrayList<Stone> stoneList, int breakpoint, int line){

		Map<Integer, Stone> intstoneMap = new HashMap<Integer, Stone>();
		/*
		 * prueft jeden Stein der Liste ob er der erste seiner Reihe (und der Richtung) ist
		 * wenn ja speichert ihn und die laenge seiner Reihe ab.
		 */
		for(Stone stone : stoneList){
			switch(line){
				case 1:
					if(!stone.hasParentLeft()){//prueft ob erster
						
						intstoneMap.put(stone.countLeftRight(),stone);//speichert in Liste
					}
					break;
				case 2:
					if(!stone.hasParentTopLeft()){
						
						intstoneMap.put(stone.countTopLeftBotRight(),stone);
					}
					break;
				case 3:
					if(!stone.hasParentTop()){
						
						intstoneMap.put(stone.countTopBot(),stone);
					}
					break;
				case 4:
					if(!stone.hasParentTopRight()){
						
						intstoneMap.put(stone.countTopRightBotLeft(),stone);
					}
					break;
			}
		}
		
		boolean run = true;
		int max = 6;//maximale moegliche Laenge (uber 6 nicht moeglich da Spiel bereits gewonnen ist)
		int keyVal = finalMap.size();//bestimmt die aktuelle Laenge der Liste
		do{
			Stone stone = intstoneMap.get(max);//versuch Stein aus Liste mit Reihenlaenge max zu bekommen 
			if(stone == null && max > 0){//wenn kein Stein gefunden, dann wird max verringert
				max--;
			}else if(max > 0){//wenn max groesser als 0 ist
				if(max < breakpoint) {//wenn max den breakpoint unterschreitet wird schleife abgebrochen
					run = false;
					break;
				}
				switch(line){//fuer die jewailiege Richtung der Reihen
					case 1:
						if(board.checkPoint(intstoneMap.get(max).getPoint().left())){//prueft ob der naechste Punkt am Anfang in der Reihe noch frei ist
							finalMap.put(keyVal++, intstoneMap.get(max).getPoint().left());//wenn ja wird er zu den Moeglichkeiten hinzugefuegt
						}
						if(board.checkPoint(lastChildRight(intstoneMap.get(max)).right())){//prueft ob der naechste Punkt am Ende in der Reihe oberhalb noch frei ist
							finalMap.put(keyVal++, lastChildRight(intstoneMap.get(max)).right());//wenn ja wird er zu den Moeglichkeiten hinzugefuegt
						}
						break;
					case 2:
						if(board.checkPoint(intstoneMap.get(max).getPoint().topleft())){
							finalMap.put(keyVal++, intstoneMap.get(max).getPoint().topleft());
						}
						if(board.checkPoint(lastChildBotRight(intstoneMap.get(max)).botright())){
							finalMap.put(keyVal++, lastChildBotRight(intstoneMap.get(max)).botright());
						}
						break;
					case 3:
						if(board.checkPoint(intstoneMap.get(max).getPoint().top())){
							finalMap.put(keyVal++, intstoneMap.get(max).getPoint().top());
						}
						if(board.checkPoint(lastChildBot(intstoneMap.get(max)).bottom())){
							finalMap.put(keyVal++, lastChildBot(intstoneMap.get(max)).bottom());
						}
						break;
					case 4:
						if(board.checkPoint(intstoneMap.get(max).getPoint().topright())){
							finalMap.put(keyVal++, intstoneMap.get(max).getPoint().topright());
						}
						if(board.checkPoint(lastChildBotLeft(intstoneMap.get(max)).botleft())){
							finalMap.put(keyVal++, lastChildBotLeft(intstoneMap.get(max)).botleft());
						}
						break;
				}
				
				max--;
			}else{
				run = false;//schleife wird abgebrochen
			}
		}while(run);
		
		return finalMap;//gibt Liste aller bisherigen und neugefundenen Moeglichkeiten zurueck
	}
	
	/**
	 * 
	 * @param finalMap Liste aller gefunden Moeglichkeiten
	 * @param stoneList Liste aller Steine einer Farbe (idR des Gegners)
	 * @param lineCase Richtung der Reihen (1=waagerecht, 2=diagonal von Links nach Rechts, 3=senkrecht, 4=diagonal von Rechts nach Linsk)
	 * @param startMap abwelchem Wert geprueft werden soll (um andere nicht zu ueberschreiben bzw loeschen)
	 * @return HashMap auf gefundene Punkte mit Luecke reduziert
	 */
	private HashMap<Integer, BoardPoint> checkSpace(HashMap<Integer, BoardPoint> finalMap, ArrayList<Stone> stoneList, int lineCase, int startMap){
		/*
		 * prueft jedes Elemnet auf der Liste ob Luecke in der Reihe
		 * Bsp: x_xx , xx_x (auch diagonal und senkrecht)
		 * Nicht x_o , x__x oder x__o
		 */
		for(int i = startMap; i < finalMap.size(); i++){
			
				switch(lineCase){
				case 1:
					boolean onlistLeft = false;//ob Punkt links einen Stein in der Liste hat
					boolean onlistRight = false;//ob Punkt rechts einen Stein in der Liste hat
					if(!board.checkPoint(finalMap.get(i).left())){//prueft ob Punkt links frei (wenn ja kann er keinen in der Liste haben)
						onlistLeft = checkOnList(stoneList, finalMap.get(i).left());//prueft ob Punkt links als Stein in Liste
						
					}
					
					if(!board.checkPoint(finalMap.get(i).right())){//prueft ob Punkt rechts frei (wenn ja kann er keinen in der Liste haben)
						onlistRight = checkOnList(stoneList, finalMap.get(i).right());//prueft ob Punkt rechts als Stein in Liste
						
					}
					if(!(onlistLeft && onlistRight)){//wenn punkt nicht in beiden Listen enthalten
						finalMap.remove(i);//Punkt wurd aus Liste der Moeglichkeiten entfernt
					}
					
					break;
				case 2:
					boolean onlistTopLeft = false;
					boolean onlistBotRight = false;
					if(!board.checkPoint(finalMap.get(i).topleft())){
						onlistTopLeft = checkOnList(stoneList, finalMap.get(i).topleft());
						
					}
					
					if(!board.checkPoint(finalMap.get(i).botright())){
						onlistBotRight = checkOnList(stoneList, finalMap.get(i).botright());
						
					}
					if(!(onlistTopLeft && onlistBotRight)){
						finalMap.remove(i);
					}
					
					break;
				case 3:
					boolean onlistTop = false;
					boolean onlistBot = false;
					if(!board.checkPoint(finalMap.get(i).top())){
						onlistTop = checkOnList(stoneList, finalMap.get(i).top());
						
					}
					
					if(!board.checkPoint(finalMap.get(i).bottom())){
						onlistBot = checkOnList(stoneList, finalMap.get(i).bottom());
						
					}
					if(!(onlistTop && onlistBot)){
						finalMap.remove(i);
					}
					break;
				case 4:
					boolean onlistTopRight = false;
					boolean onlistBotLeft = false;
					if(!board.checkPoint(finalMap.get(i).topright())){
						onlistTopRight = checkOnList(stoneList, finalMap.get(i).topright());
						
					}
					
					if(!board.checkPoint(finalMap.get(i).botleft())){
						onlistBotLeft = checkOnList(stoneList, finalMap.get(i).botleft());
						
					}
					if(!(onlistTopRight && onlistBotLeft)){
						finalMap.remove(i);
					}
					break;
				}
			
			
		}
		return finalMap;
	}
	
	/**
	 * 
	 * @param stoneList Liste der zufruefenden Steine
	 * @param boardpoint Punkt gegen den zu pruefen ist
	 * @return
	 */
	private boolean checkOnList(ArrayList<Stone> stoneList, BoardPoint boardpoint){
		for(Stone stone : stoneList){
			if(stone.getPoint().compareTo(boardpoint) == 0){//Wenn Punkte deckungsgleich
				return true;
			}
		}
		return false;
	}
	
	/**
	 * fuegt einen Stein per Zufall dem Feld hinzu
	 */
 	private void addRandom(){
		
		int maxPos = board.maxPoint.yPos; //bestimmt Grenze des Zufallgenerators
		int xPos = 0;
		int yPos = 0;
		boolean run = false;
		Random random = new Random();
		do{
			xPos = random.nextInt(maxPos);//erzeugt Zufallszahl von 0 bis Grenze fuer x
			yPos = random.nextInt(maxPos);//erzeugt Zufallszahl von 0 bis Grenze fuer y
			
			if(enableLog) System.out.println("RandomPunkt:"+alphabet[xPos]+"/"+yPos);
			try {
				/*
				 * versucht den Punkt hinzuzufuegen, wenn erfolgreich dann wird die schleife beendet
				 * wenn nicht erfolgreich, weil Punkt z.B. belegt wird ein neuer Punkt bestimmt
				 */
				run = board.addStone(new Stone(new BoardPoint(alphabet[xPos],yPos+1), color));
			} catch (BoardOutOfBoundException e) {
				System.out.println("Randompoint Exception");
				
			}
		}while(!run);
		
		
	}
 	
 	/**
 	 * 
 	 * @param hashmap zusortierende Liste
 	 * @return HashMap sortierte Liste
 	 * durch das Loeschen in den HashMaps kann es passieren, dass die Zahlenliste nicht durchgaenig (also von 0-n)
 	 * wenn die Liste nicht bereinigt wird bzw neu erstellt gibt es beim weiteren Loeschen und bin Zufallsgenerator Fehler
 	 * diese Funktion bereinigt dies und sortiert die Liste wieder von 0-n
 	 */
 	private HashMap<Integer, BoardPoint> convertToStartHashMap(HashMap<Integer, BoardPoint> hashmap){
 		HashMap<Integer, BoardPoint> convMap = new HashMap<Integer, BoardPoint>();
 		int start = 0;
 		for(Map.Entry<Integer, BoardPoint> entry : hashmap.entrySet()){//foreach in HashMaps
 			convMap.put(start, entry.getValue());
 			start++;
 		}
 		return convMap;
 	}
	
 	/**
 	 * 
 	 * @param stone zu ueberpruefenden Stein
 	 * @return Punkt in der Reihe
 	 * gibt den letzten Punkt in der Reihe von OBEN nach UNTEN aus
 	 */
	private BoardPoint lastChildBot(Stone stone){
		return stone.hasChildBot() ? lastChildBot(stone.getChildBot()) : stone.getPoint();
	}
	
	/**
 	 * 
 	 * @param stone zu ueberpruefenden Stein
 	 * @return Punkt in der Reihe
 	 * gibt den letzten Punkt in der Reihe von LINKS nach RECHTS aus
 	 */
	private BoardPoint lastChildRight(Stone stone){
		return stone.hasChildRight() ? lastChildRight(stone.getChildRight()) : stone.getPoint();
	}
	
	/**
 	 * 
 	 * @param stone zu ueberpruefenden Stein
 	 * @return Punkt in der Reihe
 	 * gibt den letzten Punkt in der Reihe von LINKS-OBEN nach RECHTS-UNTEN aus
 	 */
	private BoardPoint lastChildBotRight(Stone stone){
		return stone.hasChildBotRight() ? lastChildBotRight(stone.getChildBotRight()) : stone.getPoint();
	}
	
	/**
 	 * 
 	 * @param stone zu ueberpruefenden Stein
 	 * @return Punkt in der Reihe
 	 * gibt den letzten Punkt in der Reihe von RECHTS-OBEN nach LINKS-UNTEN aus
 	 */
	private BoardPoint lastChildBotLeft(Stone stone){
		return stone.hasChildBotLeft() ? lastChildBotLeft(stone.getChildBotLeft()) : stone.getPoint();
	}

}
