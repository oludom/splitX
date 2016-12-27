package client;

/**
 * @author Micha Heiß
 */
public class NetworkException extends Exception {

    protected String errorTxt;

    public NetworkException(){

    }

    public static class ConnectionResetException extends NetworkException{
        public ConnectionResetException() {
            this.errorTxt = "Verbindungsfehler!";
        }

        public ConnectionResetException(String text) {
            this.errorTxt = text;
        }
        public String toString(){
            return errorTxt;
        }
    }

    public static class WrongPacketException extends NetworkException {
        public WrongPacketException() {
            this.errorTxt = "Daten Fehlerhaft!";
        }

        public WrongPacketException(String text) {
            this.errorTxt = text;
        }
        public String toString(){
            return errorTxt;
        }
    }
}
