package client;

/**
 * @author Micha Heiß
 */
public class NetworkException extends Exception {

    String errorTxt;

    public NetworkException(){

    }

    static class ConnectionResetException extends NetworkException{
        private static final long serialVersionUID = 1L;
        String errorTxt;
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

}
