package server;

/**
 * @author Micha Heiß
 */
public class ClientThreadArray {

    private ClientThread[] array = new ClientThread[0];

    public int add(ClientThread add){
        ClientThread[] newArray = new ClientThread[array.length+1];
        for(int i = 0; i<array.length; i++){
            newArray[i] = array[i];
        }
        newArray[array.length] = add;
        int position = array.length;
        array = newArray;
        return position;
    }

    public ClientThread getElement(int i){
        return array[i];
    }

}
