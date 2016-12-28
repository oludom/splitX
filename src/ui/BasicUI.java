/**
 * 
 */
package ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import game.*;
import ui.UiException.*;
import game.GameException.*;
import ki.Bot;


/**
 * @author Soeren Wirries
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
	
	public BoardPoint readBP() throws StopGameException {
		this.pr("Bitte Punkt angeben >");
		String read = "";
		BoardPoint bp = new BoardPoint("A", 1);
		int yStart = 1;
		int yMax = board.maxPoint.yPos;
		String xMax = board.maxPoint.xPos;
		if(yMax > 9){
			yStart = 0;
			yMax = 9;
		}
		try{
			read = bufferedReader.readLine();
			if(read.equals("exit") || read.equals("quit")){
				
				throw new StopGameException();
			}
			if(read.matches("[A-Z,a-z][0-9]{1,2}")){
			
				if(read.matches("[A-"+xMax.toUpperCase()+"a-"+xMax.toLowerCase()+"]["+yStart+"-"+yMax+"]{1,2}")){
					String first = read.substring(0,1);
					int second = Integer.parseInt(read.substring(1));
					bp = new BoardPoint(first, second);
				}else{
					throw  new WrongEntryException();
				}
			}else{
				throw new WrongFormatException();
			}
		}catch (WrongFormatException e) {
			
			this.prln(e.toString());
			bp = readBP();
			
		}catch (WrongEntryException e) {

			this.prln(e.toString());
			bp = readBP();

		}catch (StopGameException e) {

			//this.prln(e.toString());
			throw new StopGameException();

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
		prln("");
		this.prln("Willkommen beim Spiel Connect6!");
		prln("");
		//TODO Regeln einfuegen
		int auswahl = selectMenue(new String[]{"Spielmodus auswaehlen:","Singleplayer", "Singleplayer mit Bot",
				"Multiplayer (in Arbeit)", "Bot vs Bot","Spiel verlassen"});
		
		switch(auswahl){
			case 1:
				this.startSingle();
				break;
			case 2: 
				this.startSingleBot();
				break;
			case 3:
				//TODO Multiplay einfuegen
				
				break;
			case 4: startBot();
			case 5: return false;
			
		}
		return true;
	}
	
	public void startSingle(){

		try { // player can stop game
			prUIBuff();
			int dim = readBoardDim();
			board = new Board(dim);
			prUIBuff();
			board.draw();
			prUIBuff();

			prln("Schwarz beginnt mit dem ersten Zug.");
			prln("");

			boolean run = true;

			do{
				try {
					run = board.addStone(new Stone(readBP(),true));
				} catch (BoardOutOfBoundException e) {
					prln(e.toString());
				}

			}while(!run);

			boolean color = false;

			board.draw();
			prUIBuff();
			String winningPhrase = "";
			String errorPhrase = "";
			while(run){
				for(int i = 1; i <= 2; i++){
					prln(errorPhrase);
					errorPhrase = "";
					try{
						if(color){
							prln("Schwarz ist am Zug.");

							board.addStone(new Stone(readBP(),color));

						}else{
							prln("Weiss ist am Zug.");

							board.addStone(new Stone(readBP(),color));

						}
						board.checkWinner();

					}catch (GameWonException e) {
						winningPhrase = e.toString();
						run = false;
						break;
					}catch (BoardOutOfBoundException e) {

						errorPhrase = e.toString();
						i--;

					}catch (BoardFullException e) {
						winningPhrase = e.toString();
						run = false;
						break;
					}catch (StopGameException e) {
						winningPhrase = e.toString();
						run = false;
						break;
					}catch (Exception e) {

						errorPhrase = e.toString();
						i--;

					}finally {
						prUIBuff();
						board.draw();
						prUIBuff();
					}

				}
				color = !color;
			}
			prUIBuff();
			prln(winningPhrase);
			prUIBuff();

		}catch (StopGameException e){
			this.prln(e.toString());
		}
		
		int wahl = selectMenue(new String[]{"Moechtest du nochmal Spielen?","Ja","Nein"});
		switch(wahl){
			case 1: startSingle();
				break;
			case 2: 
				break;
		}
	}
	
	public void startSingleBot(){
		prUIBuff();
		int dim = readBoardDim();
		board = new Board(dim);
		prUIBuff();
		boolean enableHardMode = false;
		boolean enableLog = false;
		int hardBot = selectMenue(new String[]{"Welche Stufe soll der dein Gegner haben?","Einfach","Schwer"});
		switch (hardBot) {
		case 1:
			enableHardMode = false;
			break;
		case 2:
			enableHardMode = true;
			break;
		}
		prUIBuff();
		int logBot = selectMenue(new String[]{"Sollen die Entscheidung des Bots angezeigt werden?","Nein","Ja"});
		switch (logBot) {
		case 1:
			enableLog = false;
			break;
		case 2:
			enableLog = true;
			break;
		}
		prUIBuff();
		int colorWahl = selectMenue(new String[]{"Welche Farbe moechtest du sein?","Schwarz","Weiss"});
		prUIBuff();
		board.draw();
		prUIBuff();
		prln("Schwarz beginnt mit dem ersten Zug.");
		prln("");
		boolean run = true;
		boolean exception = false;
		Bot bot = null;
		boolean myColor = true;
		switch(colorWahl){
		case 1: 
			do{
				try {
					run = board.addStone(new Stone(readBP(),true));
				} catch (BoardOutOfBoundException e) {
					prln(e.toString());
				}catch (StopGameException e) {
					prln(e.toString());
					exception = true;
				}
				
			}while(!run || exception);
			myColor = true;
			bot = new Bot(board, false,enableHardMode,enableLog);
			break;
		case 2:
			myColor = false;
			bot = new Bot(board, true, enableHardMode,enableLog);
			bot.next();
			break;
		}
		
		boolean color = false;
		
		if(!exception){
			board.draw();
			prUIBuff();
		}
		
		String winningPhrase = "";
		String errorPhrase = "";
		while(run){
			for(int i = 1; i <= 2; i++){
				prln(errorPhrase);
				errorPhrase = "";
				try{
					if(color){
						prln("Schwarz ist am Zug.");
						if(myColor){
							board.addStone(new Stone(readBP(),color));
						}else{
							bot.next();
						}
						
					}else{
						prln("Weiss ist am Zug.");
						
						if(!myColor){
							board.addStone(new Stone(readBP(),color));
						}else{
							bot.next();
						}
					
					}
					board.checkWinner();
					
				}catch (GameWonException e) {
					winningPhrase = e.toString();
					run = false;
					break;
				}catch (BoardOutOfBoundException e) {
					
					errorPhrase = e.toString();
					i--;
					
				}catch(StopGameException e){
					
					winningPhrase = e.toString();
					run = false;
					break;
				}catch (Exception e) {
					
					errorPhrase = e.toString();
					i--;
					
				}finally {
					prUIBuff();
					board.draw();
					prUIBuff();
				}
				
			}
			color = !color;
		}
		prUIBuff();
		prln(winningPhrase);
		prUIBuff();
		
		int wahl = selectMenue(new String[]{"Moechtest du nochmal Spielen?","Ja","Nein"});
		switch(wahl){
			case 1: startSingleBot();
				break;
			case 2: 
				break;
		}
	}
	
	public void startBot(){
		prUIBuff();
		int dim = readBoardDim();
		prUIBuff();
		board = new Board(dim);
		boolean enableHardMode1 = false;
		boolean enableLog1 = false;
		
		int hardBot1 = selectMenue(new String[]{"Welche Stufe soll der 1. Bot (schwarz) haben?","Einfach","Schwer"});
		switch (hardBot1) {
		case 1:
			enableHardMode1 = false;
			break;
		case 2:
			enableHardMode1 = true;
			break;
		}
		prUIBuff();
		int logBot1 = selectMenue(new String[]{"Sollen die Entscheidungen des 1. Bots (schwarz) angezeigt werden?","Nein","Ja"});
		switch (logBot1) {
		case 1:
			enableLog1 = false;
			break;
		case 2:
			enableLog1 = true;
			break;
		}
		prUIBuff();
		boolean enableHardMode2 = false;
		boolean enableLog2 = false;
		int hardBot2 = selectMenue(new String[]{"Welche Stufe soll der 2. Bot (weiss) haben?","Einfach","Schwer"});
		switch (hardBot2) {
		case 1:
			enableHardMode2 = false;
			break;
		case 2:
			enableHardMode2 = true;
			break;
		}
		prUIBuff();
		int logBot2 = selectMenue(new String[]{"Sollen die Entscheidungen des 2. Bots (weiss) angezeigt werden?","Nein","Ja"});
		switch (logBot2) {
		case 1:
			enableLog2 = false;
			break;
		case 2:
			enableLog2 = true;
			break;
		}
		prUIBuff();
		boolean color = false;
		
		Bot blackBot = new Bot(board, true, enableHardMode1,enableLog1);
		Bot whiteBot = new Bot(board, false, enableHardMode2,enableLog2);
		
		boolean run = true;
		String winningPhrase = "";
		String errorPhrase = "";
		blackBot.next();
		int rounds = 1;
		int move = 1;
		while(run){
			for(int i = 1; i <= 2; i++){
				prln(errorPhrase);
				errorPhrase = "";
				try{
					if(color){
						prln("Black:"+rounds+"/"+move);
						blackBot.next();
					}else{
						prln("White:"+rounds+"/"+move);
						whiteBot.next();
					
					}
					board.checkWinner();
					
				}catch (GameWonException e) {
					winningPhrase = e.toString();
					run = false;
					break;
				
				}catch (Exception e) {
					
					errorPhrase = e.toString();
					i--;
					
				}finally {
					prUIBuff();
					board.draw();
					prUIBuff();
					move++;
				}
				
			}
			color = !color;
			rounds++;
		}
		prUIBuff();
		board.draw();
		prln(winningPhrase);
		prUIBuff();
		
		int wahl = selectMenue(new String[]{"Soll das Match wiederholt werden?","Ja","Nein"});
		switch(wahl){
			case 1: startBot();
				break;
			case 2: 
				break;
		}
	}
}
