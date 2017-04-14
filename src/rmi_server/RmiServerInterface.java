package rmi_server;

import game.BoardPoint;
import game.Stone;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


/**
 * 09.04.2017
 *
 * @author SWirries
 */
public interface RmiServerInterface extends Remote, Serializable {

    int ACCEPT = 0;
    int DECLINED  =1;
    int WAITING = 3;
    int RUNNING = 4;
    int FINISH = 5;

    int CNOTHING = 0;
    int CBORDDIM = 1;
    int CGAMEWINNER = 2;
    int CGAMESTATE = 3;
    int CBOARDFULL = 4;

    void addClient(String ip) throws RemoteException;
    void removeClient(String ip) throws RemoteException;
    ArrayList<String> getOpponentList() throws RemoteException;

    int countStones(int gameID) throws RemoteException;
    void setStone(int gameID, Stone stone) throws RemoteException;
    ArrayList<Stone> getStone(int gameID, boolean color) throws RemoteException;

    void setGameControl(int gameID, int control, int value) throws RemoteException;
    int[] getGameControl(int gameID) throws RemoteException;

    int requestOpponent(String myName, String opponentName) throws RemoteException;
    String newRequest(String name) throws RemoteException;
    int getRequest(String name) throws RemoteException;
    int getRequestState(int id) throws RemoteException;
    void setRequestState(int id, int state) throws RemoteException;
}
