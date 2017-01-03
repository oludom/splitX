package server;

import game.GameException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * @author Micha Heiß
 */
public class ClientThread extends Thread implements Runnable{

    private int id, opid;
    private boolean starts;
    private static final Logger log = Logger.getLogger(Server.class.getName());
    private ObjectOutputStream outStream;
    private ObjectInputStream inStream;

    public ClientThread(Socket conSocket){

        this.starts = false;

        try{
            outStream = new ObjectOutputStream(conSocket.getOutputStream());
            outStream.flush(); // flush the output stream to send header to Client (if not done here, Client would wait for ever...)
            inStream = new ObjectInputStream(conSocket.getInputStream());
        }catch (Exception e){
            log.info("Connection Error!");
        }

    }

    @Override
    public void run(){

        // variables get initialized by Main thread before this thread gets startet


        log.info("Found Opponent! ID: " + this.id + ", OpponentID: " + this.opid);

        // tell client that second player connected and decide who should make the first move
        int[] data = {this.id, this.opid, this.starts ? 1 : 0};
        Packet p = new Packet("player", "startGame", data);
        this.sendObject(p);


        while(true){

            try {
                // wait for any Packet to arrive
                Packet packet = (Packet) inStream.readObject();
                if(packet.TYPE.equals("MSG") && packet.ACTION.equals("WINNER")){ // winning message sent by client when a game is won
                    throw new GameException.ClientWonException();
                }
                //log.info("received Packet, TYPE: " + packet.TYPE + " ACTION: " + packet.ACTION);
                Server.threads.getElement(this.opid).sendObject(packet); // send opponent the received Packet
            }catch (GameException.ClientWonException e){
                log.info(e.toString());
                break;
            }catch (Exception e){
                log.info("Error receiving Packet. " + e.toString());
                log.info("Sending STOPGAME-Message.");
                Server.threads.getElement(this.opid).sendObject(new Packet("CMD", "STOP")); // tell opponent to stop game because of a problem
                break;
            }

        }

        try {
            // cleanup
            outStream.close();
            inStream.close();
        }catch (Exception e){
            log.info("Failed to close Socket. Exiting thread... " + e.toString());
        }

    }

    /*
     * Getter / Setter
     */

    public void setopid(int id){
        this.opid = id;
    }
    public void setid(int id){
        this.id = id;
    }
    public void toggleStarts(){
        this.starts = !this.starts;
    }

    /**
     *
     * @param packet packet to be sent to client
     * - can be called by opponent to exchange data
     */
    public void sendObject(Packet packet){

        try {
            // send packet to Client
            this.outStream.writeObject(packet);
        }catch (Exception e){

            log.info("Failed to Send Packet!");
        }

    }

}

