package client;

import game.Stone;
import server.Server;
import server.Packet;

import java.io.*;
import java.net.Socket;

/**
 * @author Micha Heiß
 */
public class Multiplayer {

    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;

    public Multiplayer() throws NetworkException.ConnectionResetException{

        try{
            Socket clientSocket = new Socket("127.0.0.1", Server.PORT);
            outStream = new ObjectOutputStream(clientSocket.getOutputStream());
            outStream.flush();
            inStream = new ObjectInputStream(clientSocket.getInputStream());

            System.out.println("Verbunden! Warte auf zweiten Spieler...");
        }catch (Exception e){
            //System.out.println("Verbindung fehlgeschlagen!");
            throw new NetworkException.ConnectionResetException();
        }


    }

    public Packet waitForOpponent() throws NetworkException.ConnectionResetException
    {
        try {

            Packet player = (Packet) inStream.readObject(); // Server waits for another player to connect
            if (player.TYPE.equals("player") && player.ACTION.equals("startGame")) {  // Server found second Player
                System.out.println("Gegner gefunden! Meine ID: " + player.DATA[0] + ", Gegner ID: " + player.DATA[1]);
                return player;
            } else {
                //System.out.println("Serverdaten fehlerhaft.");
                throw new NetworkException.ConnectionResetException();
            }
        }catch (Exception e){
            //System.out.println("Server Verbingungsfehler.");
            throw new NetworkException.ConnectionResetException();
        }
    }

    public int recvBoardDim() throws NetworkException.ConnectionResetException, NetworkException.WrongPacketException {

        Packet p = recvPacket();
        if(p.TYPE.equals("DIM") && p.ACTION.equals("SET")){
            return p.DATA[0];
        }else if (p.TYPE.equals("CMD") && p.ACTION.equals("STOP")){
            throw new NetworkException.ConnectionResetException("Opponent Left");
        }else{
            throw new NetworkException.WrongPacketException("Invalid Packet received!");
        }
    }

    public void sendBoardDim(int dim) throws NetworkException.ConnectionResetException {

        this.sendPacket(new Packet("DIM", "SET", new int[]{dim}));

    }

    public void sendStone(Stone stone) throws NetworkException.ConnectionResetException {

        sendPacket(new Packet("STONE", "SET", stone.getPoint()));

    }

    public Stone recvStone(boolean color) throws NetworkException.ConnectionResetException, NetworkException.WrongPacketException {

        try {
            Packet p = recvPacket();
            if(p.TYPE.equals("STONE") && p.ACTION.equals("SET")){
                return new Stone(p.POINT, color);
            }else{
                throw new NetworkException.WrongPacketException();
            }
        }catch (NullPointerException e){
            //System.out.println("Verbindungsfehler!");
            throw new NetworkException.ConnectionResetException();
        }



    }

    private void sendPacket(Packet p) throws NetworkException.ConnectionResetException {

        try {
            outStream.writeObject(p);
        }catch (Exception e){
            //System.out.println("Fehler beim Senden! " + e.toString());
            throw new NetworkException.ConnectionResetException();
        }

    }

    private Packet recvPacket() throws NetworkException.ConnectionResetException{
        try {
            return (Packet) inStream.readObject();
        }catch (Exception e){
            //System.out.println("Fehler beim Empfangen!");
            throw new NetworkException.ConnectionResetException("");
        }
    }

    public void die() {

        try {
            outStream.close();
            inStream.close();
        }catch (Exception e){
            System.out.println("Failed to close Socket." + e.toString());
        }

    }
}
