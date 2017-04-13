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

    @Override
    public void addClient(String ip) {

        clientIPs.add(ip);

        for(Object element : clientIPs){
            System.out.println("Clients: "+element);
        }
        if(!infoThread.isAlive()) infoThread.start();

    }

    @Override
    public void removeClient(String ip) {
        clientIPs.remove(ip);
    }

    @Override
    public ArrayList<String> getOpponentList() throws RemoteException {
        return clientIPs;
    }

    @Override
    public int countStones(int gameID) throws RemoteException {

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
            if(entry.getKey().equals(name)) return entry.getValue();
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

        String[] opponentNames = gameOpponents.get(id);
        for(String op : opponentNames){
            System.out.println("OppName:"+op);
        }
        askedOpponents.remove(opponentNames[0]);

    }

    Thread infoThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while(true){
                System.out.println("\n\naskOpponents:"+askedOpponents);
                System.out.println("runningGames:"+runningGames);
                System.out.println("gameOpponents:"+gameOpponents);

                try{
                    sleep(5000);
                }catch (Exception e){

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
