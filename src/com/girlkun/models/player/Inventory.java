package com.girlkun.models.player;

import java.util.ArrayList;
import java.util.List;
import com.girlkun.models.item.Item;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN 💖
 *
 */
public class Inventory {

    public static final int LIMIT_GOLD = 2000000000;
    public static final int MAX_ITEMS_BAG = 40;
    public static final int MAX_ITEMS_BOX = 40;

    public Item trainArmor;

    public List<Item> itemsBody;
    public List<Item> itemsBag;
    public List<Item> itemsBox;

    public List<Item> itemsBoxCrackBall;

    public long gold;
    public int gem;
    public int ruby;
    
    public Inventory() {
        itemsBody = new ArrayList<>();
        itemsBag = new ArrayList<>();
        itemsBox = new ArrayList<>();
        itemsBoxCrackBall = new ArrayList<>();
    }

    public int getGemAndRuby() {
        return this.gem + this.ruby;
    }

    public void subGemAndRuby(int num) {
        this.ruby -= num;
        if (this.ruby < 0) {
            this.gem += this.ruby;
            this.ruby = 0;
        }
    }

    public void addGold(int gold) {
        this.gold += gold;
        if (this.gold > LIMIT_GOLD) {
            this.gold = LIMIT_GOLD;
        }
    }

    public void dispose() {
        if (this.trainArmor != null) {
            this.trainArmor.dispose();
        }
        this.trainArmor = null;
        if(this.itemsBody!= null){
            for(Item it : this.itemsBody){
                it.dispose();
            }
            this.itemsBody.clear();
        }
        if(this.itemsBag!= null){
            for(Item it : this.itemsBag){
                it.dispose();
            }
            this.itemsBag.clear();
        }
        if(this.itemsBox!= null){
            for(Item it : this.itemsBox){
                it.dispose();
            }
            this.itemsBox.clear();
        }
        if(this.itemsBoxCrackBall!= null){
            for(Item it : this.itemsBoxCrackBall){
                it.dispose();
            }
            this.itemsBoxCrackBall.clear();
        }
        this.itemsBody = null;
        this.itemsBag = null;
        this.itemsBox = null;
        this.itemsBoxCrackBall = null;
    }

}
