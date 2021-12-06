package world;
import java.awt.Point;

public class Door {
    private Point door;
    private boolean emerge;
    public Door(int x,int y){
        this.emerge = false;
        door = new Point(x,y);
    }
    public void appear(){
        this.emerge = true;
    }
    public boolean isEmerge(){
        return this.emerge;
    }
    public int x(){
        return this.door.x;
    }
    public int y(){
        return this.door.y;
    }
    public Point getXY(){
        return door;
    }
}
