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

import asciiPanel.AsciiPanel;

/**
 *
 * @author Aeranythe Echosong
 */
public class CreatureFactory {

    private World world;

    public CreatureFactory(World world) {
        this.world = world;
    }

    public Creature newPlayer(List<String> messages) {
        Creature player = new Creature(this.world, (char)12, AsciiPanel.brightWhite, 5, 0, 0, 9);
        player.modifyBombNum(1);
        world.addAtEmptyLocation(player);
        new PlayerAI(player, messages);
        return player;
    }

    public CreatureAI newFungus() {
        Creature fungus = new Creature(this.world, (char)4, AsciiPanel.green, 1, 0, 0, 0);
        world.addAtEmptyLocation(fungus);
        return new FungusAI(fungus, this);
    }

    public Bomb newBomb(int x,int y,Creature creature){
        Prop bomb = new Prop(this.world,(char)241,AsciiPanel.red,1,1,1);
        //world.addAtSpecificLocation(bomb, x, y);
        int radius = 1 + creature.getScore() / 500;
        Bomb b = new Bomb(bomb,radius,this,creature);
        world.addBombLocation(b,x,y);
        return b;
    }
}
