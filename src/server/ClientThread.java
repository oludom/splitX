package server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * @author Micha Heiß
 */
public class ClientThread extends Thread implements Runnable{

    private Socket conSocket;
    private int id, opid;
    private boolean starts;
    private static final Logger log = Logger.getLogger(Server.class.getName());
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;

    public ClientThread(Socket conSocket){

        this.conSocket = conSocket;
        this.starts = false;

        try{
            outStream = new ObjectOutputStream(conSocket.getOutputStream());
            outStream.flush();
            inStream = new ObjectInputStream(conSocket.getInputStream());
        }catch (Exception e){
            log.info("Connection Error!");
        }

    }

    @Override
    public void run(){

        try {

            log.info("Found Opponent! ID: " + this.id + ", OpponentID: " + this.opid);

            // wenn zweiter Spieler verbunden ist:
            int[] data = {this.id, this.opid, this.starts ? 1 : 0};
            Packet packet = new Packet("player", "startGame", data);
            outStream.writeObject(packet);



        }catch (Exception e){
            log.info("ERROR IO:"+e.toString());
        }



        while(true){

            try {
                // wait for Packet to arrive
                Packet packet = (Packet) inStream.readObject();
                //log.info("received Packet, TYPE: " + packet.TYPE + " ACTION: " + packet.ACTION);
                Server.threads.getElement(this.opid).sendObject(packet); // send opponent the received Packet
            }catch (Exception e){
                log.info("Error receiving Packet. " + e.toString());
                log.info("Sending STOPGAME-Message.");
                Server.threads.getElement(this.opid).sendObject(new Packet("CMD", "STOP"));
                break;
            }

        }

        try {
            outStream.close();
            inStream.close();
        }catch (Exception e){
            log.info("Failed to close Socket. Exiting thread... " + e.toString());
        }

    }

    public void setopid(int id){
        this.opid = id;
    }
    public void setid(int id){
        this.id = id;
    }
    public void toggleStarts(){
        this.starts = !this.starts;
    }

    public void sendObject(Packet packet){

        try {
            // send packet to Client
            this.outStream.writeObject(packet);
        }catch (Exception e){

            log.info("Failed to Send Packet!");
        }

    }

}

