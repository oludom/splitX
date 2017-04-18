package rmi_server;


//import game.BoardPoint;

import game.BoardPoint;
import game.Stone;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.sleep;

/**
 * 09.04.2017
 *
 * @author SWirries
 */
public class RmiServer implements RmiServerInterface {

    private ArrayList<String> clientIPs = new ArrayList<>();
    private HashMap<String, String> askedOpponents = new HashMap<>();
    private HashMap<Integer, Integer> runningGames = new HashMap<>();
    private HashMap<Integer, String[]> gameOpponents = new HashMap<>();
    private HashMap<Integer, ArrayList<Stone>> gameStones = new HashMap<>();
    private HashMap<Integer, int[]> gameControl = new HashMap<>();
    private int GAMEID = 1000;
    private boolean runningInfo = true;
    private boolean runningCleanup = true;

    @Override
    public void addClient(String name) throws  RemoteException{


        for(String clientName : clientIPs){
            if(clientName.equalsIgnoreCase(name)) throw new RemoteException("Der Username wird Bereits verwendet. Bitte Neuen wählen");
        }
        clientIPs.add(name);

        for(Object element : clientIPs){
            System.out.println("Clients: "+element);
        }
        if(!infoThread.isAlive()){
            System.out.println("Start InfoThread");
            infoThread.setDaemon(true);
            runningInfo = true;
            infoThread.start();
        }
        if(!cleanupThread.isAlive()){
            System.out.println("Start CleanUpThread");
            runningCleanup = true;
            cleanupThread.setDaemon(true);
            cleanupThread.start();
        }

    }

    @Override
    public void removeClient(String ip) {
        clientIPs.remove(ip);
    }

    @Override
    public ArrayList<String> getOpponentList() throws RemoteException {
        //TODO Clienst die im Spiel sind nicht anzeigen.
        return clientIPs;
    }

    @Override
    public int countStones(int gameID) throws RemoteException, NullPointerException{

        return gameStones.get(gameID).size();
    }

    @Override
    public void setStone(int gameID, Stone stone) throws RemoteException {
        gameStones.get(gameID).add(stone);
    }

    @Override
    public ArrayList<Stone> getStone(int gameID, boolean color) throws RemoteException {
        return gameStones.get(gameID);
    }

    @Override
    public void setGameControl(int gameID, int control, int value) throws RemoteException {
        gameControl.replace(gameID, new int[]{control, value});
    }

    @Override
    public int[] getGameControl(int gameID) throws RemoteException {
        return gameControl.get(gameID);
    }

    /**
     *
     * @param myName
     * @param opponentName
     * @return gibt eine die ID des Spiels zurück
     * @throws RemoteException
     */
    @Override
    public int requestOpponent(String myName, String opponentName) throws RemoteException {
        GAMEID++;
        askedOpponents.put(opponentName,myName);
        runningGames.put(GAMEID, WAITING);
        gameOpponents.put(GAMEID, new String[]{opponentName, myName});
        return GAMEID;
    }

    /**
     *
     * @param name
     * @return den Namen des Gegenspielers
     * @throws RemoteException
     */

    @Override
    public String newRequest(String name) throws RemoteException {
        for(Map.Entry<String, String> entry : askedOpponents.entrySet()){
            if(entry.getKey().equals(name)){
                return entry.getValue();
            }
        }
        return "";
    }

    /**
     *
     * @param name
     * @return ID des Spiels
     * @throws RemoteException
     */
    @Override
    public int getRequest(String name) throws RemoteException {
        for(Map.Entry<Integer, String[]> entry : gameOpponents.entrySet()){
            if(entry.getValue()[0].equals(name)|| entry.getValue()[1].equals(name)) return entry.getKey();
        }
        return -1;
    }

    /**
     *
     * @param id
     * @return gibt den Status zurück
     * @throws RemoteException
     */
    @Override
    public int getRequestState(int id) throws RemoteException {
        for(Map.Entry<Integer, Integer> entry : runningGames.entrySet()){
            if(entry.getKey() == id) return entry.getValue();
        }
        return -1;
    }

    /**
     *
     * @param id
     * @param state
     * @throws RemoteException
     */
    @Override
    public void setRequestState(int id, int state) throws RemoteException {
        System.out.println("ID:"+id+" State:" +state);
        runningGames.replace(id,state);
        if(state == RmiServerInterface.ACCEPT){
            gameControl.put(id, new int[]{0,0});
            gameStones.put(id, new ArrayList<>());
        }

        //TODO REMOVE
        String[] opponentNames = gameOpponents.get(id);
        for(String op : opponentNames){
            System.out.println("OppName:"+op);
        }

        askedOpponents.remove(opponentNames[0]);

    }

    Thread infoThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while(runningInfo){
                System.out.println("\n\naskOpponents:"+askedOpponents);
                System.out.println("runningGames:"+runningGames);
                System.out.println("gameOpponents:"+gameOpponents);
                for(Map.Entry<Integer, int[]> entry : gameControl.entrySet()){
                    int[] control = entry.getValue();
                    System.out.println("gameControl: "+entry.getKey() +" Command "+control[0]+"|"+control[1]);
                }for(Map.Entry<Integer,ArrayList<Stone>> entry : gameStones.entrySet()){
                    ArrayList<Stone> stones = entry.getValue();
                    System.out.println("gameControl: "+entry.getKey());
                    for(Stone s : stones){
                        System.out.print("Stone:"+s.getPoint());
                    }
                }
                try{
                    sleep(5000);
                }catch (Exception e){

                }
            }


        }
    });

    Thread cleanupThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (runningCleanup) {
                for(Map.Entry<Integer, Integer> entry : runningGames.entrySet()){
                    if (entry.getValue() == 5){
                        gameOpponents.remove(entry.getKey());
                        gameControl.remove(entry.getKey());
                        gameStones.remove(entry.getKey());
                        runningGames.remove(entry.getKey());
                    }
                }
                try {
                    sleep(1000 * 60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(clientIPs.size() == 0){
                    System.out.println("Threads werden beendet...");
                    runningCleanup = false;
                    runningInfo = false;
                }
            }
        }

    });


    public static void main(String[] args) {
        try {
            RmiServer server = new RmiServer();
            Registry registry = LocateRegistry.createRegistry(1099);//LocateRegistry.getRegistry();

            RmiServerInterface iconnect6 = (RmiServerInterface) UnicastRemoteObject.exportObject(server,0);
            System.out.println("Server...");

            registry.rebind("Server",iconnect6);

            System.out.println("Server erstellt");

        }catch (Exception e){
            System.out.println("Error: "+e);
        }
    }
}
