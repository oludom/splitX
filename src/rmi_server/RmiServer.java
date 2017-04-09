package rmi_server;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 * 09.04.2017
 *
 * @author SWirries
 */
public class RmiServer implements RmiServerInterface {

    private ArrayList<String> clientIPs = new ArrayList<>();


    @Override
    public String getOpponent() {
        return "1.1.1.1";
    }

    @Override
    public void addClient(String ip) {

        clientIPs.add(ip);

        for(Object element : clientIPs){
            System.out.println("Clients: "+element);
        }
    }

    @Override
    public void removeClient(String ip) {
        clientIPs.remove(ip);
    }

    @Override
    public void sendPackage() {

    }

    @Override
    public ArrayList<String> getOpponentList() throws RemoteException {
        return clientIPs;
    }


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
