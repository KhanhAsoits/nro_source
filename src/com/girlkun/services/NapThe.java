package com.girlkun.services;

import com.girlkun.models.player.Player;

/**
 *
 * @author ❤Girlkun75❤
 * @copyright ❤Trần Lại❤
 */
public class NapThe {

    private static NapThe I;
    
    public static NapThe gI(){
        if(NapThe.I == null){
            NapThe.I = new NapThe();
        }
        return NapThe.I;
    }
    
    public void napThe(Player pl, String maThe, String seri){
        System.out.println(maThe);
        System.out.println(seri);
    }
    
}






















/**
 * Vui lòng không sao chép mã nguồn này dưới mọi hình thức.
 * Hãy tôn trọng tác giả của mã nguồn này.
 * Xin cảm ơn! - Girlkun75
 */