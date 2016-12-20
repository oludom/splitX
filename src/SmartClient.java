

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * 
 */

/**
 * @author Sören Wirries
 *
 */
public class SmartClient {
	private static final Logger log = Logger.getLogger(SmartClient.class.getName());
	
	
	public static void main(String[] args) {
		
		String[]data = new String[]{"100", "150", "1"};
		SmartExchange smEx = new SmartExchange("INT", "ADD", data);// TYPE, ACTION, DATA
		
		sendData(smEx);
	
	}
	
	private static void sendData(SmartExchange exchange){
		
		try{
			System.out.println("Verbindung wird gestartet!");
			Socket cliSocket = new Socket("192.168.112.223", SmartHomeServerClient.PORT);
			
			ObjectOutputStream outStream = new ObjectOutputStream(cliSocket.getOutputStream());
			
			System.out.println("Übertragung wird gestartet");
			outStream.writeObject(exchange);
			
			BufferedReader serverIn = new BufferedReader(new InputStreamReader(cliSocket.getInputStream()));
			
			String returnMsg = serverIn.readLine();
			
			System.out.println("Retrun:"+returnMsg);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("ERROR");
		}
	}
	
	

}
