package world;

import java.lang.Math;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Bomb extends PropAI{

    private CreatureFactory factory;
    private boolean Explode;
    private List<Point> ExplosionTrace;
    private int radius;//爆炸半径
    private Creature host;//炸弹的主人
    public Bomb(Prop prop,int radius,CreatureFactory factory,Creature creature){
        super(prop);
        this.factory = factory;
        this.Explode = false;
        this.ExplosionTrace = new ArrayList<>();
        this.radius = radius;
        this.host = creature;
    }
    
    @Override
    public void run(){
        while(this.Explode == false){
            try{
                Thread.sleep(2000);
                setTrace();
                explode();
                //System.out.println("Bomb explode");
                Thread.sleep(50);
                System.out.println("Boom done!");
                this.prop.remove();
                this.host.modifyBombNum(1);
                //Thread.sleep(100);
                recoverTrace();
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }
    public void explode(){
        this.Explode = true;
        for(Point p:this.ExplosionTrace){
            //System.out.println(p.x + " " +  p.y);
            this.prop.getWorld().bombExplode(p.x, p.y);
            Creature otherCreature = this.prop.getWorld().creature(p.x,p.y);
            if(otherCreature != null){
                System.out.println("Bomb explode to " + otherCreature);
                otherCreature.getAttack(this.prop.attackValue());
                //this.host.setScore(maxScore(this.prop.attackValue(),otherCreature.maxHP()));
                this.host.setScore(maxScore(this.prop.attackValue(),otherCreature.maxHP()) * 100);
            }
        }
    }
    //每次得到炸弹的伤害值或怪物的最大生命值
    private int maxScore(int a,int b){
        if(a > b)
            return b;
        else
            return a;
    }
    public void recoverTrace(){
        for(Point p:this.ExplosionTrace){
            //Creature otheCreature = this.prop.getWorld().creature(p.x,p.y);
            //if(otheCreature == null){
            this.prop.getWorld().setFloor(p.x, p.y);
            //}
        }
    }
    private void setTrace(){
        //this.Explode = true;
        int x = this.prop.x();
        int y = this.prop.y();
        this.ExplosionTrace.add(new Point(x,y));
        
        x = this.prop.x() - 1;
        y = this.prop.y();
        while(this.prop.x() - x <= radius){
            if(this.prop.getWorld().tileType(x, y) == Tile.WALL){
                break;
            }
            this.ExplosionTrace.add(new Point(x,y));
            x--;//向上 //是WALL / FLOOR / BRICK
        }

        x = this.prop.x() + 1;
        y = this.prop.y();
        while(x - this.prop.x() <= radius){
            if(this.prop.getWorld().tileType(x, y) == Tile.WALL){
                break;
            }
            this.ExplosionTrace.add(new Point(x,y));
            x++;
        }

        x = this.prop.x();
        y = this.prop.y() - 1;
        while(this.prop.y() - y <= radius){
            if(this.prop.getWorld().tileType(x, y) == Tile.WALL){
                break;
            }
            this.ExplosionTrace.add(new Point(x,y));
            y--;
        }

        x = this.prop.x();
        y = this.prop.y() + 1;
        while(y - this.prop.y() <= radius){
            if(this.prop.getWorld().tileType(x, y) == Tile.WALL){
                break;
            }
            this.ExplosionTrace.add(new Point(x,y));
            y++;
        }
    }

    //检测Point有无在某一个炸弹的爆炸范围内即可
    public boolean canExplode(int x,int y){
        if(this.Explode == true){
            if(this.prop.y() == y && Math.abs(this.prop.x() - x) < 2){
                //this.prop.getWorld().bombExplode(x, y);
                return true;
            }
            else if(this.prop.x() == x && Math.abs(this.prop.y() - y) < 2){
                //this.prop.getWorld().bombExplode(x, y);
                return true;
            }
            else{
                //this.prop.getWorld().bombExplode(x, y);
                return false;
            }
        }
        else{
            return false;
        }
    }
}
