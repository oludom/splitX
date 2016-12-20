/**
 * @author Micha Heiß
 */
public class Spielbrett {

    private int[][] feld;

    public Spielbrett(int size) throws FieldException {
        // TODO erstelle leeren Spielplan ( static? )

        if(size <6 || size > 20){
            throw new FieldException();
        }
        feld = new int[size][size]; // 0 ist unbesetzt, 1 ist schwarz, 2 ist weiß

    }

    public void draw(){
        for(int[] line : feld){
            System.out.print("[ ");
            for(int field : line){
                if(field == 1) System.out.print("S ");
                else if(field == 2) System.out.print("W ");
                else System.out.print("O ");
            }
            System.out.println("]");
        }
        System.out.println();
        System.out.println();
    }

    public void randomize(){
        for(int i = 0; i<feld.length;i++){
            for(int j = 0; j<feld.length; j++){
                feld[i][j] = (int)( Math.random() *5 ) %3;
            }
        }
    }

    public void setFeldWeiss(Vector v) throws FieldException{
        try{
            v.subtract(new Vector(1,1));
            if(feld[v.y()][v.x()] != 0) throw new FieldException();
            feld[v.y()][v.x()] = 2;
        }catch (ArrayIndexOutOfBoundsException e){
            throw new FieldException();
        }catch (FieldException e){
            throw e;
        }

    }
    public void setFeldSchwarz(Vector v){
        feld[v.y()][v.x()] = 1;
    }

}
