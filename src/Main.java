/**
 * @author Micha Heiß
 */
public class Main {

    public static Spielbrett brett;

    public static void main(String[] args) {

        // TODO Programmstart

        int size = IO.readInt("Wie groß soll der Spielplan sein (6-20)?");
        while (true){

            try {
                brett = new Spielbrett(size);
                break;
            }catch (FieldException e){
                size = IO.readInt("Eingabe falsch, muss zwischen 6 und 20 liegen! ?:");
            }
        }



        brett.draw();
        //brett.randomize();
        // Spieler Weiß
        while(true) {
            System.out.println("nächster Spieler: ");
            while (true) {
                try {
                    brett.setFeldWeiss(IO.getNewUserPosition());
                    break;
                } catch (FieldException e) {
                    System.out.println("Die angegebene Position muss auf dem Spielfeld liegen und darf noch nicht besetzt sein! Wiederhole: ");
                }
            }
            brett.draw();
        }

    }


}
