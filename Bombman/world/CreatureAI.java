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
package world;

import java.awt.Point;
import java.util.Random;
import java.util.concurrent.Callable;
/**
 *
 * @author Aeranythe Echosong
 */
class CreatureAI implements Callable<String>{
    protected Creature creature;
    public String call(){
        return "Over";
    }
    public CreatureAI(Creature creature) {
        this.creature = creature;
        this.creature.setAI(this);
    }

    public void onEnter(int x, int y, Tile tile) {
        if(tile.isBounds()){
        }
        if (tile.isGround()) {
            //准备移动
            
            this.creature.setX(x);
            this.creature.setY(y);
        }
    }

    public void onUpdate() {
    }

    public void onNotify(String message) {
    }

    public boolean canSee(int x, int y) {
        if ((creature.x() - x) * (creature.x() - x) + (creature.y() - y) * (creature.y() - y) > creature.visionRadius()
                * creature.visionRadius()) {
            //return false;
            return true;
        }
        for (Point p : new Line(creature.x(), creature.y(), x, y)) {
            if (creature.tile(p.x, p.y).isGround() || (p.x == x && p.y == y)) {
                continue;
            }
            //return false;
            return true;
        }
        return true;
    }

    //AI's random step
    public void randomStep(){
        Random r = new Random();
        int res = r.nextInt(4);
        switch(res){
            case 0:
                this.creature.moveBy(-1, 0);
                //System.out.println(this + " move: " + "left");
                break;    
            case 1:
                this.creature.moveBy(1, 0);
                //System.out.println(this + " move: " + "right");
                break;
            case 2:
                this.creature.moveBy(0, -1);
                //System.out.println(this + " move: " + "up");
                break;
            case 3:
                this.creature.moveBy(0, 1);
                //System.out.println(this + " move: " + "down");
                break;
        }
    }
}
