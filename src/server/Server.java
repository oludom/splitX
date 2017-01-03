package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * @author Micha Heiss
 *
 */
public class Server {

	public static final int PORT = 5325;
	private static final int THREADCOUNT = 50;
	private static final Logger log = Logger.getLogger(Server.class.getName());
	public static ClientThreadArray threads = new ClientThreadArray();

	@SuppressWarnings("resource")
	public static void main(String[] args) {

		ExecutorService executor = Executors.newFixedThreadPool(THREADCOUNT);
		ServerSocket serverSocket = null;
		try{
			serverSocket = new ServerSocket(PORT);
		}catch(Exception ex){
			log.info("ERROR:"+ex.toString());
		}
		log.info("Server ready!");
		while(true){
			try {

				Socket conSocket1 = serverSocket.accept();
				log.info("Connectet1: "+ conSocket1);

				int Pos1 = threads.add(new ClientThread(conSocket1));

				//wait for second Client to connect
				Socket conSocket2 = serverSocket.accept();
				log.info("Connectet2: "+ conSocket2);
				int Pos2 = threads.add(new ClientThread(conSocket2));

				// set Opponents (connect Threads)
				threads.getElement(Pos1).setid(Pos1);
				threads.getElement(Pos1).setopid(Pos2);

				threads.getElement(Pos2).setid(Pos2);
				threads.getElement(Pos2).setopid(Pos1);

				// set starting Player
				threads.getElement(Pos1).toggleStarts();

				//run Threads
				executor.execute(threads.getElement(Pos1));
				executor.execute(threads.getElement(Pos2));
				 
			} catch (Exception ex) {
				log.info("ERROR IO:"+ex.toString());
				break;
			}
			
		}

	}

}
