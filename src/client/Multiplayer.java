package client;

import game.Stone;
import server.Main;
import server.Packet;

import java.io.*;
import java.net.Socket;
import java.sql.Time;
import java.util.concurrent.TimeUnit;

/**
 * @author Micha Heiß
 */
public class Multiplayer {

    private Socket clientSocket;
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;

    public Multiplayer(){

        try{
            clientSocket = new Socket("127.0.0.1", Main.PORT);
            outStream = new ObjectOutputStream(clientSocket.getOutputStream());
            outStream.flush();
            inStream = new ObjectInputStream(clientSocket.getInputStream());

            System.out.println("Verbunden! Warte auf zweiten Spieler...");
        }catch (Exception e){
            System.out.println("Verbindung fehlgeschlagen!");
        }


    }

    public Packet waitForOpponent(){
        while(true){
            try{

                Packet player = (Packet) inStream.readObject(); // Server waits for another player to connect
                if(player.TYPE.equals("player") && player.ACTION.equals("startGame")){  // Server found second Player
                    System.out.println("Gegner gefunden! Meine ID: " + player.DATA[0] + ", Gegner ID: " + player.DATA[1]);
                    return player;
                }else{
                    System.out.println("Serverdaten fehlerhaft.");
                }
                Thread.sleep(3000l);
            }catch (IOException e){
                System.out.println("Server IO Fehler.");
            }catch (InterruptedException e){
                System.out.println("interrupted!");
            }catch (Exception e){
                System.out.println("Server Verbingungsfehler.");
            }


        }


    }


    public int recvBoardDim() {

        Packet p = recvPacket();
        if(p.TYPE.equals("DIM") && p.ACTION.equals("SET")){
            return p.DATA[0];
        }else{
            return 0;
        }
    }

    public void sendBoardDim(int dim) {

        this.sendPacket(new Packet("DIM", "SET", new int[]{dim}));

    }

    public void sendStone(Stone stone) {

        sendPacket(new Packet("STONE", "SET", stone.getPoint()));

    }

    public Stone recvStone(boolean color) {

        try {
            Packet p = recvPacket();
            if(p.TYPE.equals("STONE") && p.ACTION.equals("SET")){
                return new Stone(p.point, color);
            }else{
                return null;
            }
        }catch (NullPointerException e){
            System.out.println("Verbindungsfehler!");
            throw e;
        }



    }

    private void sendPacket(Packet p){

        try {
            outStream.writeObject(p);
        }catch (Exception e){
            System.out.println("Fehler beim Senden!");
        }

    }

    private Packet recvPacket(){
        try {
            return (Packet) inStream.readObject();
        }catch (Exception e){
            System.out.println("Fehler beim Empfangen!");
            return null;
        }
    }
}
