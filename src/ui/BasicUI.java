/**
 * 
 */
package ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import game.*;
import ui.UiException.*;
import game.GameException.*;



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
	
	public BoardPoint readBP(){
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
		int auswahl = selectMenue(new String[]{"Spielmodus auswaehlen:","Singleplayer", "Singleplayer mit Bot (in Arbeit)",
				"Multiplayer (in Arbeit)","Spiel verlassen"});
		
		switch(auswahl){
			case 1:
				this.startSingle();
				break;
			case 2: 
				//TODO Bot einfuegen
				break;
			case 3:
				//TODO Multiplay einfuegen
				break;
			case 4: return false;
			
		}
		return true;
	}
	
	public void startSingle(){
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
			case 1: startSingle();
				break;
			case 2: 
				break;
		}
	}
	
	
}
