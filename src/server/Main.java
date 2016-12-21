package server;

import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * @author SWirries
 *
 */
public class Main {

	public static final int PORT = 61337;
	private static final Logger log = Logger.getLogger(Main.class.getName());
	
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		String clientSentence;
		String modSentence;
		ServerSocket servSocket = null;
		try{
			servSocket = new ServerSocket(PORT);
		}catch(Exception ex){
			log.info("ERROR:"+ex.toString());
		}
		log.info("Server ready!");
		while(true){
			try {
				
				Socket conSocket = servSocket.accept();
				log.info("Connectet: "+ conSocket);
				
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
				log.info("Connection closed!");
			} catch (Exception ex) {
				log.info("ERROR IO:"+ex.toString());
			}
			
		}

	}

}
