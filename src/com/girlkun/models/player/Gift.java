package com.girlkun.models.player;

/**
 *
 * @author ๐ Trแบงn Lแบกi ๐
 * @copyright ๐ GirlkuN  ๐
 *
 */
public class Gift {

    private Player player;
    
    public Gift(Player player){
        this.player = player;
    }
    
    public boolean goldTanThu;
    public boolean gemTanThu;
    
    public void dispose(){
        this.player = null;
    }
    
}
