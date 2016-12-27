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
	private static final int THREADCOUNT = 30;
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


				//Packet exData = (Packet) inStream.readObject();

				/*
				//BufferedReader reader = new BufferedReader(new InputStreamReader(conSocket.getInputStream()));
				ObjectInputStream inStream = new ObjectInputStream(conSocket.getInputStream());
				Packet exData = (Packet) inStream.readObject();
				
				//clientSentence = reader.readLine();
				String reCode = "Daten angekommen\n";
				switch(exData.TYPE){
					case "TEXT":
						if(exData.ACTION.equals("SHOW")){
							System.out.println("---------Daten-----------");
							for(String element : exData.DATA){
								System.out.println(element);
							}
							System.out.println("---------Daten Ende-----------");
							reCode = "Daten wurden gezeigt"; 
						}
						break;
					case "INT":
						if(exData.ACTION.equals("SHOW")){
							System.out.println("---------NUMMERN-----------");
							for(String element : exData.DATA){
								System.out.println(element);
							}
							System.out.println("---------NUMMERN Ende-----------");
							reCode = "Daten wurden gezeigt"; 
						}else{
							
							int summe = 0;
							for(int i = 0; i < exData.DATA.length -1; i++){
								summe += Integer.parseInt(exData.DATA[i]);
							}
							
							reCode = "Die Summer ist " + summe;
						}

				}
				 
				DataOutputStream outStream = new DataOutputStream(conSocket.getOutputStream());
				outStream.writeBytes(reCode);
				 
				conSocket.close();
				log.info("Connection closed!");*/
			} catch (Exception ex) {
				log.info("ERROR IO:"+ex.toString());
				break;
			}
			
		}

	}

}
