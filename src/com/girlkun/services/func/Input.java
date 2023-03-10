package com.girlkun.services.func;

import com.girlkun.database.GirlkunDB;
import com.girlkun.consts.ConstNpc;
import com.girlkun.models.map.Zone;
import com.girlkun.models.npc.Npc;
import com.girlkun.models.npc.NpcManager;
import com.girlkun.models.player.Player;
import com.girlkun.network.io.Message;
import com.girlkun.network.session.ISession;
import com.girlkun.server.Client;
import com.girlkun.services.Service;
import com.girlkun.services.GiftService;
import com.girlkun.services.NapThe;
import com.girlkun.services.NpcService;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ๐ Trแบงn Lแบกi ๐
 * @copyright ๐ GirlkuN ๐
 *
 */
public class Input {

    private static final Map<Integer, Object> PLAYER_ID_OBJECT = new HashMap<Integer, Object>();

    public static final int CHANGE_PASSWORD = 500;
    public static final int GIFT_CODE = 501;
    public static final int FIND_PLAYER = 502;
    public static final int CHANGE_NAME = 503;
    public static final int CHOOSE_LEVEL_BDKB = 504;
    public static final int NAP_THE = 505;

    public static final byte NUMERIC = 0;
    public static final byte ANY = 1;
    public static final byte PASSWORD = 2;

    private static Input intance;

    private Input() {

    }

    public static Input gI() {
        if (intance == null) {
            intance = new Input();
        }
        return intance;
    }

    public void doInput(Player player, Message msg) {
        try {
            String[] text = new String[msg.reader().readByte()];
            for (int i = 0; i < text.length; i++) {
                text[i] = msg.reader().readUTF();
            }
            switch (player.iDMark.getTypeInput()) {
                case NAP_THE:
                    NapThe.gI().napThe(player, text[0], text[1]);
                    break;
                case CHANGE_PASSWORD:
                    Service.getInstance().changePassword(player, text[0], text[1], text[2]);
                    break;
                case GIFT_CODE:
                    GiftService.gI().giftCode(player, text[0]);
                    break;
                case FIND_PLAYER:
                    Player pl = Client.gI().getPlayer(text[0]);
                    if (pl != null) {
                        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_FIND_PLAYER, -1, "Ngร?i muแปn..?",
                                new String[]{"ฤi tแปi\n" + pl.name, "Gแปi " + pl.name + "\ntแปi ฤรขy", "ฤแปi tรชn", "Ban"},
                                pl);
                    } else {
                        Service.getInstance().sendThongBao(player, "Ngฦฐแปi chฦกi khรดng tแปn tแบกi hoแบทc ฤang offline");
                    }
                    break;
                case CHANGE_NAME:
                    Player plChanged = (Player) PLAYER_ID_OBJECT.get((int) player.id);
                    if (plChanged != null) {
                        if (GirlkunDB.executeQuery("select * from player where name = ?", text[0]).next()) {
                            Service.getInstance().sendThongBao(player, "Tรชn nhรขn vแบญt ฤรฃ tแปn tแบกi");
                        } else {
                            plChanged.name = text[0];
                            GirlkunDB.executeUpdate("update player set name = ? where id = ?", plChanged.name, plChanged.id);
                            Service.getInstance().player(plChanged);
                            Service.getInstance().Send_Caitrang(plChanged);
                            Service.getInstance().sendFlagBag(plChanged);
                            Zone zone = plChanged.zone;
                            ChangeMapService.gI().changeMap(plChanged, zone, plChanged.location.x, plChanged.location.y);
                            Service.getInstance().sendThongBao(plChanged, "Chรบc mแปซng bแบกn ฤรฃ cรณ cรกi tรชn mแปi ฤแบนp ฤแบฝ hฦกn tรชn ban ฤแบงu");
                            Service.getInstance().sendThongBao(player, "ฤแปi tรชn ngฦฐแปi chฦกi thร?nh cรดng");
                        }
                    }
                    break;
                case CHOOSE_LEVEL_BDKB:
                    int level = Integer.parseInt(text[0]);
                    if (level >= 1 && level <= 110) {
                        Npc npc = NpcManager.getByIdAndMap(ConstNpc.QUY_LAO_KAME, player.zone.map.mapId);
                        if (npc != null) {
                            npc.createOtherMenu(player, ConstNpc.MENU_ACCEPT_GO_TO_BDKB,
                                    "Con cรณ chแบฏc chแบฏn muแปn tแปi bแบฃn ฤแป kho bรกu cแบฅp ฤแป " + level + "?",
                                    new String[]{"ฤแปng รฝ", "Tแปซ chแปi"}, level);
                        }
                    } else {
                        Service.getInstance().sendThongBao(player, "Khรดng thแป thแปฑc hiแปn");
                    }

//                    BanDoKhoBauService.gI().openBanDoKhoBau(player, (byte) );
                    break;
            }
        } catch (Exception e) {
        }
    }

    public void createForm(Player pl, int typeInput, String title, SubInput... subInputs) {
        pl.iDMark.setTypeInput(typeInput);
        Message msg;
        try {
            msg = new Message(-125);
            msg.writer().writeUTF(title);
            msg.writer().writeByte(subInputs.length);
            for (SubInput si : subInputs) {
                msg.writer().writeUTF(si.name);
                msg.writer().writeByte(si.typeInput);
            }
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void createForm(ISession session, int typeInput, String title, SubInput... subInputs) {
        Message msg;
        try {
            msg = new Message(-125);
            msg.writer().writeUTF(title);
            msg.writer().writeByte(subInputs.length);
            for (SubInput si : subInputs) {
                msg.writer().writeUTF(si.name);
                msg.writer().writeByte(si.typeInput);
            }
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void createFormChangePassword(Player pl) {
        createForm(pl, CHANGE_PASSWORD, "ฤแปi mแบญt khแบฉu", new SubInput("Mแบญt khแบฉu cลฉ", PASSWORD),
                new SubInput("Mแบญt khแบฉu mแปi", PASSWORD),
                new SubInput("Nhแบญp lแบกi mแบญt khแบฉu mแปi", PASSWORD));
    }

    public void createFormGiftCode(Player pl) {
        createForm(pl, GIFT_CODE, "Gift code Ngแปc rแปng Girlkun", new SubInput("Gift-code", ANY));
    }

    public void createFormFindPlayer(Player pl) {
        createForm(pl, FIND_PLAYER, "Tรฌm kiแบฟm ngฦฐแปi chฦกi", new SubInput("Tรชn ngฦฐแปi chฦกi", ANY));
    }
    
    public void createFormNapThe(Player pl, byte loaiThe){
        pl.iDMark.setLoaiThe(loaiThe);
        createForm(pl, NAP_THE, "Nแบกp thแบป", new SubInput("Mรฃ thแบป", ANY), new SubInput("Seri", ANY));
    }

    public void createFormChangeName(Player pl, Player plChanged) {
        PLAYER_ID_OBJECT.put((int) pl.id, plChanged);
        createForm(pl, CHANGE_NAME, "ฤแปi tรชn " + plChanged.name, new SubInput("Tรชn mแปi", ANY));
    }

    public void createFormChooseLevelBDKB(Player pl) {
        createForm(pl, CHOOSE_LEVEL_BDKB, "Chแปn cแบฅp ฤแป", new SubInput("Cแบฅp ฤแป (1-110)", NUMERIC));
    }

    public static class SubInput {

        private String name;
        private byte typeInput;

        public SubInput(String name, byte typeInput) {
            this.name = name;
            this.typeInput = typeInput;
        }
    }

}
