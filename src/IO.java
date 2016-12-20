import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author Micha Heiß
 */
public class IO implements Input {

    public static void write (String s){
        writeAndFlush(s);
    }
    public static void writeln(String s){
        writeAndFlush(s+"\n");
    }
    public static String promptAndRead(String s){
        writeAndFlush(s);
        try {
            return br.readLine();
        }catch (IOException e){
            System.out.println("Fehler: " + e.toString());
            e.printStackTrace();
            return promptAndRead(s);
        }
    }


    public static Vector getNewUserPosition() {
        String newPos = IO.promptAndRead("Geben Sie die Position ein, auf der Ihr Stein platziert werden soll (x und y getrennt durch Leerzeichen):");
        String[] tmp;
        while (true){
            try{
                tmp = newPos.split(" ");
                return new Vector(Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1]));
            }catch (ArrayIndexOutOfBoundsException e){
                newPos = IO.promptAndRead("Positionseingabe nicht korrekt. Wiederhole: ");
            }catch (Exception e){
                System.out.println("ERROR " + e.toString());
            }
        }


    }

    private static void writeAndFlush (String s){
        System.out.print(s);
        System.out.flush();
    }

    private static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public static int readInt (String s){

        try {
            return Integer.parseInt(promptAndRead(s).trim());
        }catch (Exception e){
            System.out.println("Bitte geben Sie eine Ganze Zahl ein!");
            return readInt(s);
        }
    }
    public static long readLong (String s) throws Exception{

        try {
            return Long.parseLong(promptAndRead(s).trim());
        }catch (Exception e){
            System.out.println("Fehler: " + e.toString());
            e.printStackTrace();
            return readLong(s);
        }
    }
    public static double readDouble (String s) throws Exception{

        try {
            return Double.parseDouble(promptAndRead(s).trim());
        }catch (Exception e){
            System.out.println("Fehler: " + e.toString());
            e.printStackTrace();
            return readDouble(s);
        }
    }
    public static float readFloat (String s) throws Exception{

        try {
            return Float.parseFloat(promptAndRead(s).trim());
        }catch (Exception e){
            System.out.println("Fehler: " + e.toString());
            e.printStackTrace();
            return readFloat(s);
        }
    }
    public static BigInteger readBigInteger (String s) throws Exception{

        try {
            return new BigInteger(promptAndRead(s).trim());
        }catch (Exception e){
            System.out.println("Fehler: " + e.toString());
            e.printStackTrace();
            return readBigInteger(s);
        }
    }
    public static BigDecimal readBigDecimal (String s) throws Exception{

        try {
            return new BigDecimal(promptAndRead(s).trim());
        }catch (Exception e){
            System.out.println("Fehler: " + e.toString());
            e.printStackTrace();
            return readBigDecimal(s);
        }
    }




}
