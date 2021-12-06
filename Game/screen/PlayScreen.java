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
package screen;

import world.*;
import asciiPanel.AsciiPanel;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.plaf.metal.MetalBorders.PaletteBorder;

/**
 *
 * @author Aeranythe Echosong
 */
public class PlayScreen implements Screen {

    private World world;
    private Creature player;
    private int screenWidth;
    private int screenHeight;
    private List<String> messages;
    private List<String> oldMessages;
    private CreatureFactory creatureFactory;
    private int remainTime;
    private long lastUpdateTime;
    private int FungusNum;
    private ExecutorService executor;
    private ArrayList<Future<String> > results;
    public PlayScreen() {
        this.screenWidth = 16;
        this.screenHeight = 13;
        createWorld();
        this.messages = new ArrayList<String>();
        this.oldMessages = new ArrayList<String>();
        this.remainTime = 100;
        creatureFactory = new CreatureFactory(this.world);
        executor = Executors.newCachedThreadPool();
        results = new ArrayList<Future<String> >();
        createCreatures(creatureFactory);
        this.lastUpdateTime = System.currentTimeMillis();
    }

    private void createCreatures(CreatureFactory creatureFactory) {
        this.player = creatureFactory.newPlayer(this.messages);
        //怪物10只
        this.FungusNum = 10;
        for (int i = 0; i < this.FungusNum; i++) {
            results.add(executor.submit(creatureFactory.newFungus()));
        }
    }
    private void createWorld() {
        world = new WorldBuilder(16, 13).makeCaves().build();
    }

    private void displayTiles(AsciiPanel terminal, int left, int top) {
        // Show terrain
        for (int x = 0; x < screenWidth; x++) {
            for (int y = 0; y < screenHeight; y++) {
                int wx = x + left;
                int wy = y + top;
                //can see 
                if (player.canSee(wx, wy)) {
                    terminal.write(world.glyph(wx, wy), x, y, world.color(wx, wy));
                } else {
                    terminal.write(world.glyph(wx, wy), x, y, Color.DARK_GRAY);
                }    
            }
        }
        // Show creatures
        for (Creature creature : world.getCreatures()) {
            if (creature.x() >= left && creature.x() < left + screenWidth && creature.y() >= top
                    && creature.y() < top + screenHeight) {
                if (player.canSee(creature.x(), creature.y())) {
                    terminal.write(creature.glyph(), creature.x() - left, creature.y() - top, creature.color());
                }
            }
        }
        //Show props
        for (Prop prop : world.getProps()) {
            if (prop.x() >= left && prop.x() < left + screenWidth && prop.y() >= top
                    && prop.y() < top + screenHeight) {
                if (player.canSee(prop.x(), prop.y())) {
                    terminal.write(prop.glyph(), prop.x() - left, prop.y() - top, prop.color());
                }
            }
        }
        // Creatures can choose their next action now
        world.update();
    }

    private void displayMessages(AsciiPanel terminal, List<String> messages) {
        int top = this.screenHeight - messages.size();
        for (int i = 0; i < messages.size(); i++) {
            terminal.write(messages.get(i), 1, top + i + 1);
        }
        this.oldMessages.addAll(messages);
        messages.clear();
    }

    @Override
    public void displayOutput(AsciiPanel terminal) {
        
        // Terrain and creatures
        displayTiles(terminal, getScrollX(), getScrollY());
        // Player
        //terminal.write(player.glyph(), player.x() - getScrollX(), player.y() - getScrollY(), player.color());
        // Stats
        String stats = String.format("   %3d hp %3d", player.hp(), player.getScore());
        String time = String.format("   TIME: %3d s", updateRemainTime());
        terminal.write(stats, 1, 23);
        if(updateRemainTime() > 0){
            terminal.write(time,1,24);
        }
        else{
            terminal.write("   --",1,24);
        }
        // Messages
        displayMessages(terminal, this.messages);

    }

    private long updateRemainTime(){
        long current = System.currentTimeMillis();
        if(current - this.lastUpdateTime >= 1000){
            this.lastUpdateTime = current;
            this.remainTime -= 1;
        }
        return this.remainTime;
    }

    public boolean isLose(){
        if(this.remainTime < 0)
        {
            return true;
        }
        if(this.player.hp() <= 0){
            return true;
        }
        if(world.destroyDoor() == true){
            return true;
        }
        return false;
    }

    public boolean isWin(){
        int allkill = 0;
        for(Future<String> fs: results){
            if(fs.isDone()){
                allkill += 1;
            }
        }
        if(this.FungusNum == allkill && player.x() == world.doorXY().x && player.y() == world.doorXY().y){ // and player reach the door
            return true;
        }
        return false;
    }
    @Override
    public Screen respondToUserInput(KeyEvent key) {
        switch (key.getKeyCode()) {
            //case KeyEvent.VK_W:
            case KeyEvent.VK_LEFT:
                player.moveBy(-1, 0);
                break;
            case KeyEvent.VK_RIGHT:
                player.moveBy(1, 0);
                break;
            case KeyEvent.VK_UP:
                player.moveBy(0, -1);
                break;
            case KeyEvent.VK_DOWN:
                player.moveBy(0, 1);
                break;
            case KeyEvent.VK_SPACE:
                if(player.getBombNum() > 0){
                    player.modifyBombNum(-1);
                    Thread t = new Thread(creatureFactory.newBomb(player.x(),player.y(),player));
                    t.start();
                }
                break;
            //case KeyEvent.VK_J:
                
        }
        if(isLose()){
            return new LoseScreen();
        }
        if(isWin()){
            return new WinScreen();
        }
        return this;
    }

    public int getScrollX() {
        return Math.max(0, Math.min(player.x() - screenWidth / 2, world.width() - screenWidth));
    }

    public int getScrollY() {
        return Math.max(0, Math.min(player.y() - screenHeight / 2, world.height() - screenHeight));
    }

}
