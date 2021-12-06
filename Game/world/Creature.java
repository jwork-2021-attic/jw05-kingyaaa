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

import java.awt.Color;
import java.lang.annotation.Retention;

import asciiPanel.AsciiPanel;

/**
 *
 * @author Aeranythe Echosong
 */
public class Creature {
    private int score;

    public int getScore(){
        return this.score;
    }
    public void setScore(int bonus){
        this.score += bonus;
    }

    private int bombNum;

    public int getBombNum(){
        return this.bombNum;
    }
    public void modifyBombNum(int num){
        this.bombNum += num;
    }
    private World world;

    public World getWorld(){
        return this.world;
    }

    private int x;

    public void setX(int x) {
        this.x = x;
    }

    public int x() {
        return x;
    }

    private int y;

    public void setY(int y) {
        this.y = y;
    }

    public int y() {
        return y;
    }

    private char glyph;

    public char glyph() {
        return this.glyph;
    }

    private Color color;

    public Color color() {
        return this.color;
    }

    public void setColor(Color color){
        this.color = color;
    }
    private CreatureAI ai;

    public void setAI(CreatureAI ai) {
        this.ai = ai;
    }
    public CreatureAI getAI(){
        return this.ai;
    }
    private int maxHP;

    public int maxHP() {
        return this.maxHP;
    }

    private int hp;

    public int hp() {
        return this.hp;
    }

    public void modifyHP(int amount) {
        this.hp += amount;

        if (this.hp < 1) {
            world.remove(this);
        }
    }

    private int attackValue;

    public int attackValue() {
        return this.attackValue;
    }

    private int defenseValue;

    public int defenseValue() {
        return this.defenseValue;
    }

    private int visionRadius;

    public int visionRadius() {
        return this.visionRadius;
    }

    public boolean canSee(int wx, int wy) {
        return ai.canSee(wx, wy);
    }

    public Tile tile(int wx, int wy) {
        return world.tile(wx, wy);
    }

    public void dig(int wx, int wy) {
        world.dig(wx, wy);
    }
    
    public void moveBy(int mx, int my) {
        Creature otherCreature = world.creature(x + mx, y + my);
        Prop prop = world.prop(x + mx,y + my);
        if(prop != null){
            //玩家遇到食物
            if(prop.getAI() instanceof Food && this.getAI() instanceof PlayerAI){
                this.setScore(prop.maxHP());
                prop.modifyHP(-prop.maxHP());
                ai.onEnter(x + mx, y + my, world.tile(x + mx, y + my));
            }
            if(prop.getAI() instanceof Power && this.getAI() instanceof PlayerAI){
                //this.setScore(prop.maxHP());
                this.modifyBombNum(1);
                prop.modifyHP(-prop.maxHP());
                ai.onEnter(x + mx, y + my, world.tile(x + mx, y + my));
            }
        }
        else if (otherCreature == null) {
            //如果新位置可走，则同步访问
            //this.world.visit(x + my,y + my);
            ai.onEnter(x + mx, y + my, world.tile(x + mx, y + my));
            //可能没走，也可能已走
            //如果走了，则取消原来的tile[x][y]的标记
            //this.world.cancelVisited(x, y);
        }
        else{
            //怪物袭击玩家
            if(otherCreature.getAI() instanceof PlayerAI){
                attack(otherCreature);
            }
        }
    }

    public synchronized void getAttack(int gotAttackValue){
        this.modifyHP(-gotAttackValue);
        //System.out.println(this + " got attack: " + gotAttackValue);
    }
    //道具对生物造成伤害，道具毁灭
    public void attack(Creature creature,Prop prop){
        prop.modifyHP(-prop.maxHP());
        int damage = Math.max(0, this.attackValue() - creature.defenseValue());
        damage = (int) (Math.random() * damage) + 1;
        creature.modifyHP(-damage);
    }

    public void attack(Creature other) {
        int damage = Math.max(0, this.attackValue() - other.defenseValue());
        damage = (int) (Math.random() * damage) + 1;

        other.getAttack(damage);
        
        //this.notify("You attack the '%s' for %d damage.", other.glyph, damage);
        //other.notify("The '%s' attacks you for %d damage.", glyph, damage);
    }
    
    public void update() {
        this.ai.onUpdate();
    }

    public boolean canEnter(int x, int y) {
        return world.tile(x, y).isGround();
    }

    public void notify(String message, Object... params) {
        ai.onNotify(String.format(message, params));
    }

    public Creature(World world, char glyph, Color color, int maxHP, int attack, int defense, int visionRadius) {
        this.world = world;
        this.glyph = glyph;
        this.color = color;
        this.maxHP = maxHP;
        this.hp = maxHP;
        this.attackValue = attack;
        this.defenseValue = defense;
        this.visionRadius = visionRadius;
        this.bombNum = 0;
        this.score = 0;
    }
}
