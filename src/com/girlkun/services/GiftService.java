package com.girlkun.services;

import com.girlkun.models.player.Player;

/**
 *
 * @author ๐ Trแบงn Lแบกi ๐
 * @copyright ๐ GirlkuN  ๐
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
