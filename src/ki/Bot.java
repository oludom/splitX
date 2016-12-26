package ki;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


import game.*;
import game.GameException.*;

public class Bot {
	
	private Board board;
	private boolean color;
	private String[] alphabet = {"A", "B", "C", "D", "E", "F", "G","H","I","J", "K", "L","M", "N","O", "P","Q", "R", "S", "T", "U", "V", "W","X", "Y", "Z"};
	ArrayList<Stone> ownStone = new ArrayList<Stone>();
	ArrayList<Stone> oppStone = new ArrayList<Stone>();
	int ownMaxRow = 0;
	int oppMaxRow = 0;
	boolean enableHardMode = false;
	
	public Bot(Board board, boolean color){
		this.board = board;
		this.color = color;
		
		update();
	}
	
	public Bot(Board board, boolean color, boolean enableHardMode){
		this.board = board;
		this.color = color;
		this.enableHardMode = enableHardMode;
		update();
	}
	
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
	
	private boolean useHardMode(){
		HashMap<Integer, BoardPoint> finalMap = new HashMap<>();
		Random random = new Random();
		if(!color && oppStone.size() ==1){
			finalMap = addToLine(finalMap, oppStone, oppMaxRow,1);
			finalMap = addToLine(finalMap, oppStone, oppMaxRow,2);
			finalMap = addToLine(finalMap, oppStone, oppMaxRow,3);
			finalMap = addToLine(finalMap, oppStone, oppMaxRow,4);
		}else if(oppMaxRow >= 3){
			int startMap = finalMap.size();
			finalMap = addToLine(finalMap, oppStone, oppMaxRow,1);
			finalMap = checkSpace(finalMap, oppStone, 1, startMap);
			startMap = finalMap.size();
			finalMap = addToLine(finalMap, oppStone, oppMaxRow,2);
			finalMap = checkSpace(finalMap, oppStone, 1, startMap);
			startMap = finalMap.size();
			finalMap = addToLine(finalMap, oppStone, oppMaxRow,3);
			finalMap = checkSpace(finalMap, oppStone, 1, startMap);
			startMap = finalMap.size();
			finalMap = addToLine(finalMap, oppStone, oppMaxRow,4);
			finalMap = checkSpace(finalMap, oppStone, 1, startMap);
		}
		
		
		if(finalMap.size() != 0){
			int ranInt = random.nextInt(finalMap.size());
			if(finalMap.size() == 1 && finalMap.get(0) == null){
				for(int i = 0; i <= 10; i++){
					if(finalMap.get(i) != null){
						finalMap.put(0, finalMap.get(i));
						i = 11;
					}
				}
				
			}
			System.out.println("HardMode Map:"+finalMap);
			System.out.println("Random:"+ranInt);
			
			try{
				board.addStone(new Stone(finalMap.get(ranInt) ,color));
			}catch (Exception e) {
				System.out.println("HardMode Exception");
				return true;
			}
			return false;
		}else{
			return true;
		}
		
	}
	
	private void useEasyMode(){
		HashMap<Integer, BoardPoint> finalMap = new HashMap<>();
		Random random = new Random();
		boolean withOwn = true;
		if(oppMaxRow >= 4){
			finalMap = addToLine(finalMap, oppStone, oppMaxRow,1);
			finalMap = addToLine(finalMap, oppStone, oppMaxRow,2);
			finalMap = addToLine(finalMap, oppStone, oppMaxRow,3);
			finalMap = addToLine(finalMap, oppStone, oppMaxRow,4);
			System.out.println("OppMaxMap:"+finalMap);
			if(finalMap.size() != 0) withOwn = false;
		}else if(oppMaxRow >=3){
			finalMap = addToLine(finalMap, oppStone, oppMaxRow,1);
			finalMap = addToLine(finalMap, oppStone, oppMaxRow,2);
			finalMap = addToLine(finalMap, oppStone, oppMaxRow,3);
			finalMap = addToLine(finalMap, oppStone, oppMaxRow,4);
		}
		
		
		
		if(withOwn && ownStone.size() > 0){
			boolean firstrun = true;
			int runOverFlow = 0;
			do{
				if(ownMaxRow >= 4 && firstrun){
					finalMap = addToLine(finalMap, ownStone, ownMaxRow,1);
					finalMap = addToLine(finalMap, ownStone, ownMaxRow,2);
					finalMap = addToLine(finalMap, ownStone, ownMaxRow,3);
					finalMap = addToLine(finalMap, ownStone, ownMaxRow,4);
					
					firstrun = false;
					
				}else{
					int breakpoint = 0;
					if(runOverFlow > 3){
						String s = "t";
						s += s;
					}
					if(ownStone.size() > 4 && runOverFlow < 5){
						breakpoint = 2;
					}
					finalMap = addToLine(finalMap, ownStone, breakpoint,1);
					finalMap = addToLine(finalMap, ownStone, breakpoint,2);
					finalMap = addToLine(finalMap, ownStone, breakpoint,3);
					finalMap = addToLine(finalMap, ownStone, breakpoint,4);
				}
				runOverFlow++;
			}while(finalMap.size() == 0);
		}
		
		if(finalMap.size() != 0){
			int ranInt = random.nextInt(finalMap.size());
			System.out.println("Map:"+finalMap);
			System.out.println("Random:"+ranInt);
			try{
				board.addStone(new Stone(finalMap.get(ranInt) ,color));
			}catch (Exception e) {
				System.out.println("exception");
				
			}
		}else{
			addRandom();
		}
	}
	
	private HashMap<Integer, BoardPoint> addToLine(HashMap<Integer, BoardPoint> finalMap, ArrayList<Stone> stoneList, int breakpoint, int line){

		Map<Integer, Stone> intstoneMap = new HashMap<Integer, Stone>();
		for(Stone stone : stoneList){
			switch(line){
				case 1:
					if(!stone.hasParentLeft()){
						
						intstoneMap.put(stone.countLeftRight(),stone);
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
		int max = 6;
		int keyVal = finalMap.size();
		do{
			Stone stone = intstoneMap.get(max);
			if(stone == null && max > 0){
				max--;
			}else if(max > 0){
				if(max < breakpoint) {
					run = false;
					break;
				}
				switch(line){
					case 1:
						if(board.checkPoint(intstoneMap.get(max).getPoint().left())){
							finalMap.put(keyVal++, intstoneMap.get(max).getPoint().left());
						}
						if(board.checkPoint(lastChildRight(intstoneMap.get(max)).right())){
							finalMap.put(keyVal++, lastChildRight(intstoneMap.get(max)).right());
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
				run = false;
			}
		}while(run);
		
		return finalMap;
	}
	
	private HashMap<Integer, BoardPoint> checkSpace(HashMap<Integer, BoardPoint> finalMap, ArrayList<Stone> stoneList, int lineCase, int startMap){
		for(int i = startMap; i < finalMap.size(); i++){
			
				switch(lineCase){
				case 1:
					boolean onlistLeft = false;
					boolean onlistRight = false;
					if(!board.checkPoint(finalMap.get(i).left())){
						onlistLeft = checkOnList(stoneList, finalMap.get(i).left());
						
					}
					
					if(!board.checkPoint(finalMap.get(i).right())){
						onlistRight = checkOnList(stoneList, finalMap.get(i).right());
						
					}
					if(!onlistLeft && !onlistRight){
						finalMap.remove(i);
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
					if(!onlistTopLeft && !onlistBotRight){
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
					if(!onlistTop && !onlistBot){
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
					if(!onlistTopRight && !onlistBotLeft){
						finalMap.remove(i);
					}
					break;
				}
			
			
		}
		return finalMap;
	}
	
	private boolean checkOnList(ArrayList<Stone> stoneList, BoardPoint boardpoint){
		for(Stone stone : stoneList){
			if(stone.getPoint().compareTo(boardpoint) == 0){
				return true;
			}
		}
		return false;
	}

 	private void addRandom(){
		
		int maxPos = board.maxPoint.yPos;
		int xPos = 0;
		int yPos = 0;
		boolean run = false;
		Random random = new Random();
		do{
			xPos = random.nextInt(maxPos);
			yPos = random.nextInt(maxPos);
			
			System.out.println("RandomPunkt:"+alphabet[xPos]+"/"+yPos);
			try {
				run = board.addStone(new Stone(new BoardPoint(alphabet[xPos],yPos+1), color));
			} catch (BoardOutOfBoundException e) {
				System.out.println("Bot Punkt exception");
				
			}
		}while(!run);
		
		
	}
	
	private BoardPoint lastChildBot(Stone stone){
		return stone.hasChildBot() ? lastChildBot(stone.getChildBot()) : stone.getPoint();
	}
	
	private BoardPoint lastChildRight(Stone stone){
		return stone.hasChildRight() ? lastChildRight(stone.getChildRight()) : stone.getPoint();
	}
	
	private BoardPoint lastChildBotRight(Stone stone){
		return stone.hasChildBotRight() ? lastChildBotRight(stone.getChildBotRight()) : stone.getPoint();
	}
	
	private BoardPoint lastChildBotLeft(Stone stone){
		return stone.hasChildBotLeft() ? lastChildBotLeft(stone.getChildBotLeft()) : stone.getPoint();
	}

}
