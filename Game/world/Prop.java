package world;
import java.awt.Color;

public class Prop implements Runnable{
    public void run(){

    }
    private PropAI ai;

    public void setAI(PropAI ai) {
        this.ai = ai;
    }
    public PropAI getAI(){
        return this.ai;
    }
    private World world;
    public World getWorld(){
        return this.world;
    }
    private int x;
    public void setX(int x){
        this.x = x;
    }    
    public int x(){
        return x;
    }
    private int y;
    public void setY(int y){
        this.y = y;
    }
    public int y(){
        return y;
    }
    private char glyph;
    public char glyph(){
        return this.glyph;
    }
    private Color color;
    public Color color(){
        return this.color;
    }
    private int maxHP;
    public int maxHP(){
        return this.maxHP;
    }
    private int hp;
    public int hp(){
        return this.hp;
    }
    public void modifyHP(int amount){
        this.hp += amount;
        if(this.hp < 1){
            world.remove(this);
        }
    }
    public void remove(){
        world.remove(this);
    }
    private int attackValue;
    public int attackValue(){
        return this.attackValue;
    }

    public Tile tile(int wx,int wy){
        return world.tile(wx,wy);
    }

    public void dig(int wx,int wy){
        world.dig(wx,wy);
    }

    //private boolean effect;

    //public void setEffect(boolean b){
    //    this.effect = b;
    //}
    //public boolean getEffect(){
    //    return this.effect;
    //}
    public Prop(World world,char glyph,Color color,int maxHP,int hp,int attack){
        this.world = world;
        this.glyph = glyph;
        this.color = color;
        this.maxHP = maxHP;
        this.hp = hp;
        this.attackValue = attack;
        //this.effect = false;
    }

    public Prop(Prop prop){
        this.world = prop.world;
        this.glyph = prop.glyph;
        this.color = prop.color;
        this.maxHP = prop.maxHP;
        this.hp = prop.hp;
        this.attackValue = prop.attackValue;
        //this.effect = false;
    }
}
