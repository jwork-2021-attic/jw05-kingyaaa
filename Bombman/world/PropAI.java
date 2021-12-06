package world;

public class PropAI implements Runnable{
    protected Prop prop;
    public void run(){

    }
    
    public PropAI(Prop prop){
        this.prop = prop;
        this.prop.setAI(this);
    }
    public void setX(int x){
        this.prop.setX(x);
    }
    public void setY(int y){
        this.prop.setY(y);
    }
    private boolean emerge;

    public boolean isEmerge(){
        return this.emerge;
    }
    public void appear(){
        this.emerge = true;
    }
}
