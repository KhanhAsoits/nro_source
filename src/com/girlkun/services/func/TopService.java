package com.girlkun.services.func;

import com.girlkun.jdbc.daos.PlayerDAO;
import com.girlkun.models.player.Player;
import com.girlkun.server.Client;
import com.girlkun.server.Manager;
import com.girlkun.network.io.Message;
import com.girlkun.utils.Util;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN 💖
 *
 */
public class TopService {
    
    private static final String QUERY_TOP_POWER = "select player.id, player.name,"
            + "player.power, player.head, player.gender, player.have_tennis_space_ship,"
            + "player.clan_id_sv" + Manager.SERVER + ", player.data_inventory,"
            + "player.data_location, player.data_point, player.data_magic_tree,"
            + "player.items_body, player.items_bag, player.items_box, player.items_box_lucky_round,"
            + "player.friends, player.enemies, player.data_intrinsic,player.data_item_time,"
            + "player.data_task, player.data_mabu_egg, player.data_charm, player.skills,"
            + "player.skills_shortcut, player.pet_info, player.pet_power, player.pet_point,"
            + "player.pet_body, player.pet_skill, player.data_black_ball from player join "
            + "account on player.account_id = account.id where account.is_admin = 0 order by "
            + "player.power desc limit 20";

    private static final int TIME_TARGET_GET_TOP_POWER = 1800000;

    private static TopService i;

    private long lastTimeGetTopPower;
    private List<Player> listTopPower;

    private TopService() {
        this.listTopPower = new ArrayList<>();
    }

    public static TopService gI() {
        if (i == null) {
            i = new TopService();
        }
        return i;
    }
    public void sendTabTop(Player pl, String nameBXH){
        Message msg = null;
        try{
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF(nameBXH);
            msg.writer().writeByte(this.listTopPower.size());
            for(int i = 0 ; i < this.listTopPower.size() ; i++){
                
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt((int)this.listTopPower.get(i).id);//id
                msg.writer().writeShort(this.listTopPower.get(i).head);
                //if(pl.session.get_version() > 214){
               
                    msg.writer().writeShort(-1);
                //}
                msg.writer().writeShort(this.listTopPower.get(i).getBody());
                msg.writer().writeShort(this.listTopPower.get(i).getLeg());
                msg.writer().writeUTF(this.listTopPower.get(i).name);
                msg.writer().writeUTF("NULL");
                msg.writer().writeUTF("NULL");
            }
            pl.sendMessage(msg);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void showTopPower(Player player) {
        if(true){
            return;
        }
        if (Util.canDoWithTime(lastTimeGetTopPower, TIME_TARGET_GET_TOP_POWER)) {
            this.lastTimeGetTopPower = System.currentTimeMillis();
            this.listTopPower.clear();
//            this.listTopPower = PlayerDAO.getPlayers(QUERY_TOP_POWER);
        }
        Message msg;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top Sức Mạnh");
            msg.writer().writeByte(this.listTopPower.size());
            for (int i = 0; i < this.listTopPower.size(); i++) {
                Player pl = this.listTopPower.get(i);
                msg.writer().writeInt(i+1);
                msg.writer().writeInt((int) pl.id);
                msg.writer().writeShort(pl.getHead());
                msg.writer().writeShort(pl.getBody());
                msg.writer().writeShort(pl.getLeg());
                msg.writer().writeUTF(pl.name);
                msg.writer().writeUTF(Client.gI().getPlayer(pl.id) != null ? "Online" : "");
                msg.writer().writeUTF("Sức mạnh: " + Util.numberToMoney(pl.nPoint.power));
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

}
