/**
 * 
 */
package ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import client.Multiplayer;
import client.NetworkException;
import game.*;
import server.Packet;
import ui.UiException.WrongFormatException;


/**
 * @author S�ren Wirries
 *
 */
public class BasicUI {
	
	private BufferedReader bufferedReader;
	private Board board;
	
	public BasicUI(){
		
		bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		
	}
	
	public BasicUI(Board board){
		this.board = board;
		bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		
	}
	
	public void prln(String string){
		this.printlnAndFlush(string);
	}
	
	public void pr(String string){
		this.printAndFlush(string);
	}
	
	private void printlnAndFlush(String string){
		System.out.println(string);
		System.out.flush();
	}
	
	private void printAndFlush(String string){
		System.out.print(string);
		System.out.flush();
	}
	
	private void prUIBuff(){
		prln("");
		prln("------------------------------------------------");
		prln("");
	}
	
	public BoardPoint readBP(){
		this.pr("Bitte Punkt angeben >");
		String read = "";
		BoardPoint bp = new BoardPoint("A", 1);
		int yMax = board.maxPoint.yPos;
		String xMax = board.maxPoint.xPos;
		try{
			read = bufferedReader.readLine();
			if(read.matches("[A-"+xMax.toUpperCase()+"a-"+xMax.toLowerCase()+"][1-"+yMax+"]{1,2}")){
				String first = read.substring(0,1);
				int second = Integer.parseInt(read.substring(1));
				bp = new BoardPoint(first, second);
			}else{
				throw  new WrongFormatException();
			}
		}catch (WrongFormatException e) {
			
			this.prln(e.toString());
			bp = readBP();
			
		}catch (Exception e) {
			
			this.prln("Allgemeiner Fehler bei der Eingabe des Punkts");
			bp = readBP();
		}
		
		return bp;
	}
	
	public int readBoardDim(){
		this.pr("Bitte Spielbrett Groesse angeben >");
		String read = "";
		int outInt = 6;
		try{
			read = bufferedReader.readLine();
			outInt = Integer.parseInt(read);
			if(6 <= outInt && outInt <= 20){
				
			}else{
				throw  new WrongFormatException("Brettgroesse muss zwischen 6 und 20 liegen!");
			}
		}catch (WrongFormatException e) {
			
			this.prln(e.toString());
			outInt = readBoardDim();
			
		}catch(Exception e){
			
			this.prln("Allgemeiner Fehler bei der Eingabe des Punkts");
			outInt = readBoardDim();
		}
		
		return outInt;
	}
	
	public int selectMenue(String[] menu){
		this.prln(menu[0]);
		
		for(int i = 1; i < menu.length; i++){
			this.prln("\t"+i+". "+menu[i]);
		}
		this.pr("Bitte Menu auswaehlen >");
		int auswahl = 0;
		try{
			String read = bufferedReader.readLine();
			auswahl = Integer.parseInt(read.trim());
			if(auswahl < 1 || auswahl > menu.length) throw new WrongFormatException();
			
		}catch (WrongFormatException e) {
			
			this.prln(e.toString());
			auswahl = selectMenue(menu);
		
		}catch (Exception e) {
			this.prln("Allgemeiner Fehler bei der Eingabe des Punkts");
			auswahl = selectMenue(menu);
		}
		return auswahl;
	}
	
	public boolean startGame(){
		this.prln("Willkommen beim Spiel Connect6!");
		prln("");
		//TODO Regeln einfuegen
		int auswahl = selectMenue(new String[]{"Spielmodus auswaehlen:","Singleplayer", "Singleplayer mit Bot (in Arbeit)",
				"Multiplayer (in Arbeit)","Spiel verlassen"});
		
		switch(auswahl){
			case 1:
				this.startSingle();
				break;
			case 2: 
				//TODO Singelplay mit Bot einfuegen
				break;
			case 3:
				this.startMulti();
				break;
			case 4: return false;
			
		}
		return true;
	}

	private void startMulti() {

		Multiplayer opponent;


		try { // fängt alle NetworkExceptions ab und beendet das Spiel


			opponent = new Multiplayer();


			Packet init = opponent.waitForOpponent();

			System.out.println("Spiel wird gestartet.");

			// initializing normal game

			int dim;
			boolean first = init.DATA[2] == 1; // DATA2 is 1 if this player should move first

			boolean run = true;
			boolean color = false; // ich bin immer weiss
			boolean winner = false;

			if(first){

				dim = readBoardDim();
				opponent.sendBoardDim(dim);
				board = new Board(dim);
				prUIBuff();
				board.draw();
				prUIBuff();

				// erster Zug
				prln("Du bist am Zug!");
				Stone stone = new Stone(readBP(),color);
				board.addStone(stone);
				opponent.sendStone(stone);
				color = !color;

				prUIBuff();
				board.draw();
				prUIBuff();

				// warte auf zwei Züge des Gegners
				for(int i = 0; i<2; i++){
					prln("Dein Gegner ist am Zug! Warte bis er zwei Züge gemacht hat.");
					stone = opponent.recvStone(color);
					board.addStone(stone);
					prUIBuff();
					board.draw();
					prUIBuff();
				}
				color = !color;



			}else{

				prln("Dein Gegner wählt die Spielbrettgröße...");
				dim = opponent.recvBoardDim();
				board = new Board(dim);
				prUIBuff();
				board.draw();
				prUIBuff();
				color = true;

				prln("Dein Gegner ist am Zug! Warte bis er den ersten Zug gemacht hat");
				Stone stone = opponent.recvStone(color);
				board.addStone(stone);
				color = !color;

				prUIBuff();
				board.draw();
				prUIBuff();

			}

			while(run){
				for(int i = 1; i <= 2; i++){
					if(!color){
						prln("Du bist am Zug!");
						Stone stone = new Stone(readBP(),color);
						board.addStone(stone);
						opponent.sendStone(stone);
						if(board.maxRowBlack() > 5){
							run = false;
							winner = color;
							break;
						}
					}else{
						prln("Dein Gegner ist am Zug! Warte bis er zwei Züge gemacht hat.");
						Stone stone = opponent.recvStone(color);
						board.addStone(stone);

						if(board.maxRowWhite() > 5){
							run = false;
							winner = color;
							break;
						}
					}
					prUIBuff();
					board.draw();
					prUIBuff();
				}
				color = !color;
			}
			prUIBuff();
			board.draw();
			prUIBuff();

			if(winner){
				prln("Du hast verloren!");
			}else {
				prln("Du hast gewonnen!");
			}
			prUIBuff();

			prln("Zurück zum Menü in 5 Sekunden.");

		}catch (NetworkException.ConnectionResetException e){
			System.out.println("Ein Verbindungsfehler ist aufgetreten. Zurück zum Hauptmenü in 5 Sekunden...");
		}catch (NetworkException.WrongPacketException e){
			System.out.println("Empfangene Daten Fehlerhaft. Zurück zum Hauptmenü in 5 Sekunden...");
		}catch (Exception e){
			System.out.println("Fehler! ");
		}finally {

			//opponent.die();

			try {
				TimeUnit.SECONDS.sleep(5); // warte 10 Sekunden
			}catch (Exception e){

			}
		}

	}

	public void startSingle(){
		int dim = readBoardDim();
		board = new Board(dim);
		prUIBuff();
		board.draw();
		prUIBuff();
		prln("Schwarz beginnt mit dem ersten Zug.");
		prln("");
		board.addStone(new Stone(readBP(),true));
		boolean run = true;
		boolean color = false;
		board.draw();
		prUIBuff();
		while(run){
			for(int i = 1; i <= 2; i++){
				if(color){
					prln("Schwarz ist am Zug.");
					board.addStone(new Stone(readBP(),color));
					if(board.maxRowBlack() > 5){
						run = false;
						break;
					}
				}else{
					prln("Weiss ist am Zug.");
					board.addStone(new Stone(readBP(),color));
					if(board.maxRowWhite() > 5){
						run = false;
						break;
					}
				}
				prUIBuff();
				board.draw();
				prUIBuff();
			}
			color = !color;
		}
		prUIBuff();
		prln("Ein Spieler hat gewonnen");
		prUIBuff();
		
		int wahl = selectMenue(new String[]{"Moechtest du nochmal Spielen?","Ja","Nein"});
		switch(wahl){
			case 1: startSingle();
				break;
			case 2: 
				break;
		}
	}
}
