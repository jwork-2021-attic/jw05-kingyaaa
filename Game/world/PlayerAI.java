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

import java.util.List;

/**
 *
 * @author Aeranythe Echosong
 */
public class PlayerAI extends CreatureAI {

    //private int bombNum;

    //public int getBombNum(){
    //    return this.bombNum;
    //}
    //public void modifyBombNum(int num){
    //    this.bombNum += num;
    //}
    //private int score;
    private List<String> messages;

    public PlayerAI(Creature creature, List<String> messages) {
        super(creature);
        this.messages = messages;
        //this.score = 0;
    }
    
    public void onEnter(int x, int y, Tile tile) {
        //如果怪物都消失，门可以进
        if (tile.isGround()) {
            creature.setX(x);
            creature.setY(y);
        }
        if(creature.getWorld().getCreatures().size() == 1){
            if(tile.isDoor()){
                creature.setX(x);
                creature.setY(y);
            }
        }    
    }
    

    public void onNotify(String message) {
        this.messages.add(message);
    }
}
