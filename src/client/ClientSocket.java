package client;

import server.Main;
import server.Packet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * 
 */

/**
 * @author S?ren Wirries
 *
 */
public class ClientSocket {

	public static void main(String[] args) {
		
		String[]data = new String[]{"100", "150", "1"};
		Packet smEx = new Packet("INT", "ADD", data);// TYPE, ACTION, DATA
		
		sendData(smEx);
	
	}
	
	private static void sendData(Packet exchange){
		
		try{
			Socket cliSocket = new Socket("192.168.112.223", Main.PORT);
			
			ObjectOutputStream outStream = new ObjectOutputStream(cliSocket.getOutputStream());
			
			outStream.writeObject(exchange);
			
			BufferedReader serverIn = new BufferedReader(new InputStreamReader(cliSocket.getInputStream()));
			
			String returnMsg = serverIn.readLine();
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	}
	
	

}
