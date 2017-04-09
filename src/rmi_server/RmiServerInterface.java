package rmi_server;

import javafx.collections.ObservableList;

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

    String getOpponent() throws RemoteException;
    void addClient(String ip) throws RemoteException;
    void removeClient(String ip) throws RemoteException;
    void sendPackage() throws RemoteException;
    ArrayList<String> getOpponentList() throws RemoteException;
}
