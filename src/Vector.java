/**
 * @author Micha Heiß
 */
public class Vector {

    private int x,y;

    public Vector(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int x(){
        return this.x;
    }
    public int y(){
        return this.y;
    }

    public double length(){
        return Math.sqrt(x*x+y*y);
    }

    public Vector me(){
        return this;
    }

    public Vector add(Vector v){
        this.x += v.x;
        this.y += v.y;
        return this;
    }
    public Vector subtract(Vector v){
        this.x -= v.x;
        this.y -= v.y;
        return this;
    }

}
