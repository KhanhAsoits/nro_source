package com.girlkun.services;

import com.girlkun.models.player.Player;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN  💖
 *
 */
public class GiftService {

    private static GiftService i;
    
    private GiftService(){
        
    }
    
    public static GiftService gI(){
        if(i == null){
            i = new GiftService();
        }
        return i;
    }
    
    public void giftCode(Player player, String code){
        
    }
    
}
