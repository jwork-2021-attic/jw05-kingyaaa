package world;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.Point;
import asciiPanel.AsciiPanel;
/*
 * Copyright (C) 2015 Aeranythe Echosong
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
/**
 *
 * @author Aeranythe Echosong
 */
public class World {

    private Tile[][] tiles;
    private int width;
    private int height;
    private List<Creature> creatures;
    private List<Prop> props;
    private Door door;
    private boolean destroy;
    public static final int TILE_TYPES = 2;
    private Prop food;
    private Prop power;

    public World(Tile[][] tiles) {
        this.tiles = tiles;
        this.width = tiles.length;
        this.height = tiles[0].length;
        this.creatures = new ArrayList<>();
        this.props = new ArrayList<>();
        //this.bricks = new ArrayList<>();
        //this.food = new Food();
        //this.power = new Power();
        Point p = randomDoor();
        this.door = new Door(p.x,p.y);
        this.destroy = false;
        CreateFood();
        CreatePower();
    }
    /*
    public synchronized boolean visit(int x,int y){
        if(this.visited[x][y] == false){
            this.visited[x][y] = true;
            return false;
        }
        else{
            return true;
        }
    }
    public synchronized void cancelVisited(int x,int y){
        if(this.creature(x,y) == null)
            this.visited[x][y] = false;
    }
    */
    public Tile tile(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return Tile.BOUNDS;
        } else {
            return tiles[x][y];
        }
    }

    public char glyph(int x, int y) {
        return tiles[x][y].glyph();
    }

    public Color color(int x, int y) {
        return tiles[x][y].color();
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public Tile tileType(int x,int y){
        return this.tiles[x][y];
    }
    public void bombExplode(int x,int y){
        tiles[x][y] = Tile.ExplosionTrace;
    }
    public boolean isDoor(int x,int y){
        return x == this.door.x() && y == this.door.y();
    }
    public boolean isFood(int x,int y){
        return x == this.food.x() && y == this.food.y();
    }
    public boolean isPower(int x,int y){
        return x == this.power.x() && y == this.power.y();
    } 
    public void setFloor(int x,int y){
        if(isDoor(x, y) && door.isEmerge() == false){
            tiles[x][y] = Tile.DOOR;
            door.appear();
        }
        else if(isDoor(x,y) && door.isEmerge() == true){
            tiles[x][y] = Tile.FLOOR;
            this.destroy = true;
        }
        else if(isFood(x,y) && food.getAI().isEmerge() == false){
            food.getAI().appear();
            this.props.add(this.food);
        }
        else if(isFood(x, y) && food.getAI().isEmerge() == true){
            this.food.modifyHP(-this.food.maxHP());
        }
        else if(isPower(x,y) && power.getAI().isEmerge() == true){
            this.power.modifyHP(-this.power.maxHP());
        }
        else if(isPower(x,y) && power.getAI().isEmerge() == false){
            power.getAI().appear();
            this.props.add(this.power);
        }
        else
            tiles[x][y] = Tile.FLOOR;
    }
    public boolean destroyDoor(){
        return this.destroy;
    }
    public Point doorXY(){
        return door.getXY();
    }
    public void dig(int x, int y) {
        if (tile(x, y).isDiggable()) {
            tiles[x][y] = Tile.FLOOR;
        }
    }
    
    public void addAtSpecificLocation(Prop prop,int x,int y){
        prop.setX(x);
        prop.setY(y);
        this.props.add(prop);
    }
    public void addBombLocation(Bomb bomb,int x,int y){
        bomb.prop.setX(x);
        bomb.prop.setY(y);
        this.props.add(bomb.prop);
    }

    public void addAtEmptyLocation(Creature creature) {
        int x;
        int y;

        do {
            x = (int) (Math.random() * this.width);
            y = (int) (Math.random() * this.height);
        } while (!tile(x, y).isGround() || this.creature(x, y) != null);

        creature.setX(x);
        creature.setY(y);

        this.creatures.add(creature);
    }

    public synchronized Creature creature(int x, int y) {
        for (Creature c : this.creatures) {
            if (c.x() == x && c.y() == y) {
                //if(c.getAI() instanceof PlayerAI){
                //    System.out.println("Encounter player");
                //}
                return c;
            }
        }
        return null;
    }
    public synchronized Prop prop(int x,int y){
        for(Prop prop : this.props){
            if(prop.x() == x && prop.y() == y){
                return prop;
            }
        }

        return null;
    }
    public List<Creature> getCreatures() {
        return this.creatures;
    }
    public List<Prop> getProps(){
        return this.props;
    }
    //public List<Bomb> getBombs(){
    //    return this.bombs;
    //}
    public void remove(Creature target) {
        this.creatures.remove(target);
    }
    //public void remove(Bomb bomb){
    //    this.bombs.remove(bomb);
    //}
    public void remove(Prop target){
        tiles[target.x()][target.y()] = Tile.FLOOR;
        this.props.remove(target);
    }

    public void update() {
        ArrayList<Creature> toUpdate = new ArrayList<>(this.creatures);

        for (Creature creature : toUpdate) {
            creature.update();
        }
    }

    public Point randomDoor(){
        //找出所有的Brick
        List<Point> bricks = getAllBricks();
        Random random = new Random();
        int doorindex = random.nextInt(bricks.size());
        //食物和炸弹补给
        //System.out.println(bricks.get(doorindex).x + " " + bricks.get(doorindex).y);
        //System.out.println(bricks.get(foodindex).x + " " + bricks.get(foodindex).y);
        //System.out.println(bricks.get(powerindex).x + " " + bricks.get(powerindex).y);
        return new Point(bricks.get(doorindex).x,bricks.get(doorindex).y);    
    }
    public void CreateFood(){
        this.food = new Prop(this,(char)240,AsciiPanel.brightYellow,200,200,0); 
        Food f = new Food(this.food);
        List<Point> bricks = getAllBricks();
        Random random = new Random();
        int foodindex = random.nextInt(bricks.size());
        while(bricks.get(foodindex).x == this.door.x() && bricks.get(foodindex).y == this.door.y()){
            foodindex  = random.nextInt(bricks.size());
        }
        int x = bricks.get(foodindex).x;
        int y = bricks.get(foodindex).y;
        food.setX(x);
        food.setY(y);
        System.out.println(x + " " + y);
        //addAtSpecificLocation(prop,x,y);
    }
    public void CreatePower(){
        this.power = new Prop(this,(char)241,AsciiPanel.brightBlue,500,500,0); 
        Power p = new Power(this.power);
        List<Point> bricks = getAllBricks();
        Random random = new Random();
        int pindex = random.nextInt(bricks.size());
        while((bricks.get(pindex).x == this.door.x() && bricks.get(pindex).y == this.door.y()) || 
        bricks.get(pindex).x == this.food.x() && bricks.get(pindex).x == this.food.y()){
            pindex  = random.nextInt(bricks.size());
        }
        int x = bricks.get(pindex).x;
        int y = bricks.get(pindex).y;
        power.setX(x);
        power.setY(y);
        System.out.println(x + " " + y);
        //addAtSpecificLocation(prop,x,y);
    }
    public List<Point> getAllBricks(){
        //找出所有的Brick
        List<Point> bricks = new ArrayList<>();
        for(int i = 0;i < this.width;i++){
            for(int j = 0;j < this.height;j++){
                if(tiles[i][j] == Tile.BRICK){
                    Point p = new Point(i,j);
                    bricks.add(p);
                }
            }
        }
        return bricks;
    }
}
