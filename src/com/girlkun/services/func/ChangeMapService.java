package com.girlkun.services.func;

import com.girlkun.consts.ConstMap;
import com.girlkun.consts.ConstPlayer;
import com.girlkun.consts.ConstTask;
import com.girlkun.models.map.Map;
import com.girlkun.models.map.WayPoint;
import com.girlkun.models.map.Zone;
import com.girlkun.models.map.blackball.BlackBallWar;
import com.girlkun.services.MapService;
import com.girlkun.models.mob.Mob;
import com.girlkun.models.player.Player;
import com.girlkun.models.matches.TYPE_LOSE_PVP;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;
import com.girlkun.network.io.Message;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.PlayerService;
import com.girlkun.services.TaskService;
import com.girlkun.utils.Logger;
import com.girlkun.utils.TimeUtil;
import java.util.List;

/**
 *
 * @author 💖 Trần Lại 💖
 * @copyright 💖 GirlkuN 💖
 *
 */
public class ChangeMapService {

    private static final byte EFFECT_GO_TO_TUONG_LAI = 0;
    private static final byte EFFECT_GO_TO_BDKB = 1;

    public static final byte AUTO_SPACE_SHIP = -1;
    public static final byte NON_SPACE_SHIP = 0;
    public static final byte DEFAULT_SPACE_SHIP = 1;
    public static final byte TELEPORT_YARDRAT = 2;
    public static final byte TENNIS_SPACE_SHIP = 3;

    private static ChangeMapService instance;

    private ChangeMapService() {

    }

    public static ChangeMapService gI() {
        if (instance == null) {
            instance = new ChangeMapService();
        }
        return instance;
    }

    /**
     * Mở tab chuyển map
     */
    public void openChangeMapTab(Player pl) {
        List<Zone> list = null;
        switch (pl.iDMark.getTypeChangeMap()) {
        }
        Message msg;
        try {
            msg = new Message(-91);
            switch (pl.iDMark.getTypeChangeMap()) {
                case ConstMap.CHANGE_CAPSULE:
                    list = (pl.mapCapsule = MapService.gI().getMapCapsule(pl));
                    msg.writer().writeByte(list.size());
                    for (int i = 0; i < pl.mapCapsule.size(); i++) {
                        Zone zone = pl.mapCapsule.get(i);
                        if (i == 0 && pl.mapBeforeCapsule != null) {
                            msg.writer().writeUTF("Về chỗ cũ: " + zone.map.mapName);
                        } else if (zone.map.mapName.equals("Nhà Broly") || zone.map.mapName.equals("Nhà Gôhan")
                                || zone.map.mapName.equals("Nhà Moori")) {
                            msg.writer().writeUTF("Về nhà");
                        } else {
                            msg.writer().writeUTF(zone.map.mapName);
                        }
                        msg.writer().writeUTF(zone.map.planetName);
                    }
                case ConstMap.CHANGE_BLACK_BALL:
                    list = (pl.mapBlackBall != null ? pl.mapBlackBall
                            : (pl.mapBlackBall = MapService.gI().getMapBlackBall()));
                    msg.writer().writeByte(list.size());
                    for (Zone zone : list) {
                        msg.writer().writeUTF(zone.map.mapName);
                        msg.writer().writeUTF(zone.map.planetName);
                    }
                    break;
            }
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
            Logger.logException(UseItem.class, e);
        }
    }

    /**
     * Mở tab chọn khu
     *
     * @param pl
     */
    public void openZoneUI(Player pl) {
        if (pl.zone == null) {
            Service.getInstance().sendThongBaoOK(pl, "Không thể đổi khu vực trong map này");
            return;
        }
        if (!pl.isAdmin()) {
            if (MapService.gI().isMapOffline(pl.zone.map.mapId)) {
                Service.getInstance().sendThongBaoOK(pl, "Không thể đổi khu vực trong map này");
                return;
            }
            if (MapService.gI().isMapDoanhTrai(pl.zone.map.mapId)) {
                Service.getInstance().sendThongBaoOK(pl, "Không thể đổi khu vực trong map này");
                return;
            }
        }
        Message msg;
        try {
            msg = new Message(29);
            msg.writer().writeByte(pl.zone.map.zones.size());
            for (Zone zone : pl.zone.map.zones) {
                msg.writer().writeByte(zone.zoneId);
                int numPlayers = zone.getNumOfPlayers();
                msg.writer().writeByte((numPlayers < 5 ? 0 : (numPlayers < 8 ? 1 : 2)));
                msg.writer().writeByte(numPlayers);
                msg.writer().writeByte(zone.maxPlayer);
                msg.writer().writeByte(0);
            }
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    /**
     * Chuyển khu
     *
     * @param pl
     * @param zoneId
     */
    public void changeZone(Player pl, int zoneId) {
        if (pl.zone == null) {
            Service.getInstance().sendThongBaoOK(pl, "Không thể đổi khu vực trong map này");
            return;
        }
        if (!pl.isAdmin()) {
            if (MapService.gI().isMapOffline(pl.zone.map.mapId)) {
                Service.getInstance().sendThongBaoOK(pl, "Không thể đổi khu vực trong map này");
                return;
            }
            if (MapService.gI().isMapDoanhTrai(pl.zone.map.mapId)) {
                Service.getInstance().sendThongBaoOK(pl, "Không thể đổi khu vực trong map này");
                return;
            }
        }
        if (pl.isAdmin() || Util.canDoWithTime(pl.iDMark.getLastTimeChangeZone(), 10000)) {
            pl.iDMark.setLastTimeChangeZone(System.currentTimeMillis());
            Map map = pl.zone.map;
            if (zoneId >= 0 && zoneId <= map.zones.size() - 1) {
                Zone zoneJoin = map.zones.get(zoneId);
                if (zoneJoin != null && (zoneJoin.getNumOfPlayers() >= zoneJoin.maxPlayer && !pl.isAdmin())) {
                    Service.getInstance().sendThongBaoOK(pl, "Khu vực đã đầy");
                    return;
                }
                if (zoneJoin != null) {
                    changeMap(pl, zoneJoin, -1, -1, pl.location.x, pl.location.y, NON_SPACE_SHIP);
                }
            } else {
                Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
            }
        } else {
            Service.getInstance().sendThongBaoOK(pl, "Không thể đổi khu vực lúc này, vui lòng đợi "
                    + TimeUtil.getTimeLeft(pl.iDMark.getLastTimeChangeZone(), 10));
        }
    }

    /**
     * Chuyển map bằng tàu vũ trụ
     *
     * @param pl
     * @param mapId
     * @param zone
     * @param x
     */
    public void changeMapBySpaceShip(Player pl, int mapId, int zone, int x) {
        if (pl.isDie()) {
            if (pl.haveTennisSpaceShip) {
                Service.getInstance().hsChar(pl, pl.nPoint.hpMax, pl.nPoint.mpMax);
            } else {
                Service.getInstance().hsChar(pl, 1, 1);
            }
        } else {
            if (pl.haveTennisSpaceShip) {
                pl.nPoint.setFullHpMp();
                PlayerService.gI().sendInfoHpMp(pl);
            }
        }
        changeMap(pl, null, mapId, zone, x, 5, AUTO_SPACE_SHIP);
    }

    public void changeMapBySpaceShip(Player pl, Zone zoneJoin, int x) {
        if (pl.isDie()) {
            if (pl.haveTennisSpaceShip) {
                Service.getInstance().hsChar(pl, pl.nPoint.hpMax, pl.nPoint.mpMax);
            } else {
                Service.getInstance().hsChar(pl, 1, 1);
            }
        } else {
            if (pl.haveTennisSpaceShip) {
                pl.nPoint.setFullHpMp();
                PlayerService.gI().sendInfoHpMp(pl);
            }
        }
        changeMap(pl, zoneJoin, -1, -1, x, 5, AUTO_SPACE_SHIP);
    }

    /**
     * Chuyển map đứng trên mặt đất
     *
     * @param pl
     * @param mapId
     * @param zoneId
     * @param x
     */
    public void changeMapInYard(Player pl, int mapId, int zoneId, int x) {
        Zone zoneJoin = MapService.gI().getMapCanJoin(pl, mapId, zoneId);
        if (zoneJoin != null) {
            x = x != -1 ? x : Util.nextInt(100, zoneJoin.map.mapWidth - 100);
            changeMap(pl, zoneJoin, -1, -1, x, zoneJoin.map.yPhysicInTop(x, 100), NON_SPACE_SHIP);
        }
    }

    /**
     * Chuyển map đứng trên mặt đất
     *
     * @param pl
     * @param zoneJoin
     * @param x
     */
    public void changeMapInYard(Player pl, Zone zoneJoin, int x) {
        changeMap(pl, zoneJoin, -1, -1, x, zoneJoin.map.yPhysicInTop(x, 100), NON_SPACE_SHIP);
    }

    /**
     * Chuyển map
     *
     * @param pl
     * @param mapId
     * @param zone
     * @param x
     * @param y
     */
    public void changeMap(Player pl, int mapId, int zone, int x, int y) {
        changeMap(pl, null, mapId, zone, x, y, NON_SPACE_SHIP);
    }

    /**
     * Chuyển map
     *
     * @param pl
     * @param zoneJoin
     * @param x
     * @param y
     */
    public void changeMap(Player pl, Zone zoneJoin, int x, int y) {
        changeMap(pl, zoneJoin, -1, -1, x, y, NON_SPACE_SHIP);
    }

    /**
     * Chuyển map bằng dịch chuyển
     *
     * @param pl
     * @param zoneJoin
     * @param x
     * @param y
     */
    public void changeMapYardrat(Player pl, Zone zoneJoin, int x, int y) {
        changeMap(pl, zoneJoin, -1, -1, x, y, TELEPORT_YARDRAT);
    }

    private void changeMap(Player pl, Zone zoneJoin, int mapId, int zoneId, int x, int y, byte typeSpace) {
        TransactionService.gI().cancelTrade(pl);
        if (zoneJoin == null) {
            if (mapId != -1) {
                zoneJoin = MapService.gI().getMapCanJoin(pl, mapId, zoneId);
            }
        }
        zoneJoin = checkMapCanJoin(pl, zoneJoin);
        if (zoneJoin != null) {
            boolean currMapIsCold = MapService.gI().isMapCold(pl.zone.map);
            boolean nextMapIsCold = MapService.gI().isMapCold(zoneJoin.map);
            if (typeSpace == AUTO_SPACE_SHIP) {
                spaceShipArrive(pl, (byte) 0, pl.haveTennisSpaceShip ? TENNIS_SPACE_SHIP : DEFAULT_SPACE_SHIP);
                pl.iDMark.setIdSpaceShip(pl.haveTennisSpaceShip ? TENNIS_SPACE_SHIP : DEFAULT_SPACE_SHIP);
            } else {
                pl.iDMark.setIdSpaceShip(typeSpace);
            }
            if (pl.effectSkill.isCharging) {
                EffectSkillService.gI().stopCharge(pl);
            }
            if (pl.effectSkill.useTroi) {
                EffectSkillService.gI().removeUseTroi(pl);
            }
            if (x != -1) {
                pl.location.x = x;
            } else {
                pl.location.x = Util.nextInt(100, zoneJoin.map.mapWidth - 100);
            }
            pl.location.y = y;
            this.goToMap(pl, zoneJoin);
            if (pl.pet != null) {
                pl.pet.joinMapMaster();
            }
            Service.getInstance().clearMap(pl);
            zoneJoin.mapInfo(pl); //-24
            pl.zone.load_Me_To_Another(pl);
            if (!pl.isBoss && !pl.isPet) {
                pl.zone.load_Another_To_Me(pl);
            }
            pl.iDMark.setIdSpaceShip(NON_SPACE_SHIP);
            if (currMapIsCold != nextMapIsCold) {
                if (!currMapIsCold && nextMapIsCold) {
                    Service.getInstance().sendThongBao(pl, "Bạn đã đến hành tinh Cold");
                    Service.getInstance().sendThongBao(pl, "Sức tấn công và HP của bạn bị giảm 50% vì quá lạnh");
                } else {
                    Service.getInstance().sendThongBao(pl, "Bạn đã rời hành tinh Cold");
                    Service.getInstance().sendThongBao(pl, "Sức tấn công và HP của bạn đã trở lại bình thường");
                }
                Service.getInstance().point(pl);
                Service.getInstance().Send_Info_NV(pl);
            }
            checkJoinSpecialMap(pl);
        } else {
            int plX = pl.location.x;
            if (pl.location.x >= pl.zone.map.mapWidth - 60) {
                plX = pl.zone.map.mapWidth - 60;
            } else if (pl.location.x <= 60) {
                plX = 60;
            }
            Service.getInstance().resetPoint(pl, plX, pl.location.y);
            Service.getInstance().sendThongBaoOK(pl, "Không thể đến khu vực này");
        }
    }

    public void changeMapWaypoint(Player player) {
        Zone zoneJoin = null;
        WayPoint wp = null;
        int xGo = player.location.x;
        int yGo = player.location.y;
        if (player.zone.map.mapId == 45 || player.zone.map.mapId == 46) {
            int x = player.location.x;
            int y = player.location.y;
            if (x >= 35 && x <= 685 && y >= 550 && y <= 560) {
                xGo = player.zone.map.mapId == 45 ? 420 : 636;
                yGo = 150;
                zoneJoin = MapService.gI().getMapCanJoin(player, player.zone.map.mapId + 1, -1);
            }
        }
        if (zoneJoin == null) {
            wp = MapService.gI().getWaypointPlayerIn(player);
            if (wp != null) {
                zoneJoin = MapService.gI().getMapCanJoin(player, wp.goMap, -1);
                if (zoneJoin != null) {
                    xGo = wp.goX;
                    yGo = wp.goY;
                }
            }
        }
        if (zoneJoin != null) {
            changeMap(player, zoneJoin, -1, -1, xGo, yGo, NON_SPACE_SHIP);
        } else {
            int x = player.location.x;
            if (player.location.x >= player.zone.map.mapWidth - 60) {
                x = player.zone.map.mapWidth - 60;
            } else if (player.location.x <= 60) {
                x = 60;
            }
            Service.getInstance().resetPoint(player, x, player.location.y);
            Service.getInstance().sendThongBaoOK(player, "Không thể đến khu vực này");
        }
    }

    public void finishLoadMap(Player player) {
        sendEffectMapToMe(player);
        sendEffectMeToMap(player);
        TaskService.gI().checkDoneTaskGoToMap(player, player.zone);
//        Logger.log(Logger.PURPLE, "finish load map player " + player.name + "\n");
    }

    private void sendEffectMeToMap(Player player) {
        Message msg;
        try {
            if (player.effectSkill.isShielding) {
                msg = new Message(-124);
                msg.writer().writeByte(1);
                msg.writer().writeByte(0);
                msg.writer().writeByte(33);
                msg.writer().writeInt((int) player.id);
                Service.getInstance().sendMessAnotherNotMeInMap(player, msg);
                msg.cleanup();
            }

            if (player.mobMe != null) {
                msg = new Message(-95);
                msg.writer().writeByte(0);//type
                msg.writer().writeInt((int) player.id);
                msg.writer().writeShort(player.mobMe.tempId);
                msg.writer().writeInt(player.mobMe.point.gethp());// hp mob
                Service.getInstance().sendMessAnotherNotMeInMap(player, msg);
                msg.cleanup();
            }
            if (player.pet != null && player.pet.mobMe != null) {
                msg = new Message(-95);
                msg.writer().writeByte(0);//type
                msg.writer().writeInt((int) player.pet.mobMe.id);
                msg.writer().writeShort(player.pet.mobMe.tempId);
                msg.writer().writeInt(player.pet.mobMe.point.gethp());// hp mob
                Service.getInstance().sendMessAnotherNotMeInMap(player, msg);
                msg.cleanup();
            }
        } catch (Exception e) {
        }
    }

    private void sendEffectMapToMe(Player player) {
        Message msg;
        try {
            for (Mob mob : player.zone.mobs) {
                if (mob.isDie()) {
                    continue;
                }
                if (mob.effectSkill.isThoiMien) {
                    msg = new Message(-124);
                    msg.writer().writeByte(1); //b5
                    msg.writer().writeByte(1); //b6
                    msg.writer().writeByte(41); //num6
                    msg.writer().writeByte(mob.id); //b7
                    player.sendMessage(msg);
                    msg.cleanup();
                }
                if (mob.effectSkill.isSocola) {
                    msg = new Message(-112);
                    msg.writer().writeByte(1);
                    msg.writer().writeByte(mob.id); //b4
                    msg.writer().writeShort(4133);//b5
                    player.sendMessage(msg);
                    msg.cleanup();
                }
                if (mob.effectSkill.isStun || mob.effectSkill.isBlindDCTT) {
                    msg = new Message(-124);
                    msg.writer().writeByte(1);
                    msg.writer().writeByte(1);
                    msg.writer().writeByte(40);
                    msg.writer().writeByte(mob.id);
                    player.sendMessage(msg);
                    msg.cleanup();
                }
            }
        } catch (Exception e) {

        }
        try {
            List<Player> players = player.zone.getHumanoids();
            for (Player pl : players) {
                if (!player.equals(pl)) {

                    if (pl.effectSkill.isShielding) {
                        msg = new Message(-124);
                        msg.writer().writeByte(1);
                        msg.writer().writeByte(0);
                        msg.writer().writeByte(33);
                        msg.writer().writeInt((int) pl.id);
                        player.sendMessage(msg);
                        msg.cleanup();
                    }
                    if (pl.effectSkill.isThoiMien) {
                        msg = new Message(-124);
                        msg.writer().writeByte(1); //b5
                        msg.writer().writeByte(0); //b6
                        msg.writer().writeByte(41); //num3
                        msg.writer().writeInt((int) pl.id); //num4
                        player.sendMessage(msg);
                        msg.cleanup();
                    }
                    if (pl.effectSkill.isBlindDCTT || pl.effectSkill.isStun) {
                        msg = new Message(-124);
                        msg.writer().writeByte(1);
                        msg.writer().writeByte(0);
                        msg.writer().writeByte(40);
                        msg.writer().writeInt((int) pl.id);
                        msg.writer().writeByte(0);
                        msg.writer().writeByte(32);
                        player.sendMessage(msg);
                        msg.cleanup();
                    }

                    if (pl.effectSkill.useTroi) {
                        if (pl.effectSkill.plAnTroi != null) {
                            msg = new Message(-124);
                            msg.writer().writeByte(1); //b5
                            msg.writer().writeByte(0);//b6
                            msg.writer().writeByte(32);//num3
                            msg.writer().writeInt((int) pl.effectSkill.plAnTroi.id);//num4
                            msg.writer().writeInt((int) pl.id);//num9
                            player.sendMessage(msg);
                            msg.cleanup();
                        }
                        if (pl.effectSkill.mobAnTroi != null) {
                            msg = new Message(-124);
                            msg.writer().writeByte(1); //b4
                            msg.writer().writeByte(1);//b5
                            msg.writer().writeByte(32);//num8
                            msg.writer().writeByte(pl.effectSkill.mobAnTroi.id);//b6
                            msg.writer().writeInt((int) pl.id);//num9
                            player.sendMessage(msg);
                            msg.cleanup();
                        }
                    }
                    if (pl.mobMe != null) {
                        msg = new Message(-95);
                        msg.writer().writeByte(0);//type
                        msg.writer().writeInt((int) pl.id);
                        msg.writer().writeShort(pl.mobMe.tempId);
                        msg.writer().writeInt(pl.mobMe.point.gethp());// hp mob
                        player.sendMessage(msg);
                        msg.cleanup();
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    public void spaceShipArrive(Player player, byte typeSendMSG, byte typeSpace) {
        Message msg;
        try {
            msg = new Message(-65);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeByte(typeSpace);
            switch (typeSendMSG) {
                case 0: //cho tất cả
                    Service.getInstance().sendMessAllPlayerInMap(player, msg);
                    break;
                case 1: //cho bản thân
                    player.sendMessage(msg);
                    break;
                case 2: //cho người chơi trong map
                    Service.getInstance().sendMessAllPlayerIgnoreMe(player, msg);
                    break;
            }
            msg.cleanup();
        } catch (Exception e) {

        }
    }

    public void goToMap(Player player, Zone zoneJoin) {
        Zone oldZone = player.zone;
        if (oldZone != null) {
            this.exitMap(player);
            if (player.mobMe != null) {
                player.mobMe.goToMap(zoneJoin);
            }
        }
        player.zone = zoneJoin;
        player.zone.addPlayer(player);
    }

    public void exitMap(Player player) {
        if (player.zone != null) {
            //xử thua pvp
            if (player.pvp != null) {
                player.pvp.lose(player, TYPE_LOSE_PVP.RUNS_AWAY);
            }
            BlackBallWar.gI().dropBlackBall(player);
            if (player.effectSkill.useTroi) {
                EffectSkillService.gI().removeUseTroi(player);
            }
            if(player.effectSkin.xHPKI > 1){
                player.effectSkin.xHPKI = 1;
                Service.getInstance().point(player);
            }
            player.zone.removePlayer(player);
            if (!MapService.gI().isMapOffline(player.zone.map.mapId)) {
                Message msg;
                try {
                    msg = new Message(-6);
                    msg.writer().writeInt((int) player.id);
                    Service.getInstance().sendMessAnotherNotMeInMap(player, msg);
                    msg.cleanup();
                    player.zone = null;
                } catch (Exception e) {
                    Logger.logException(MapService.class, e);
                }
            }
        }
    }

    public void goToTuongLai(Player player) {
        if (!player.iDMark.isGotoFuture()) {
            player.iDMark.setLastTimeGoToFuture(System.currentTimeMillis());
            player.iDMark.setGotoFuture(true);
            spaceShipArrive(player, (byte) 1, DEFAULT_SPACE_SHIP);
            effectChangeMap(player, 60, EFFECT_GO_TO_TUONG_LAI);
        }
    }

    public void goToDBKB(Player player) {
        if (!player.iDMark.isGoToBDKB()) {
            player.iDMark.setLastTimeGoToBDKB(System.currentTimeMillis());
            player.iDMark.setGoToBDKB(true);
            spaceShipArrive(player, (byte) 1, DEFAULT_SPACE_SHIP);
            effectChangeMap(player, 60, EFFECT_GO_TO_BDKB);
        }
    }

    public void goToQuaKhu(Player player) {
        ChangeMapService.this.changeMapBySpaceShip(player, 24, -1, -1);
    }

    public void goToPotaufeu(Player player) {
        ChangeMapService.this.changeMapBySpaceShip(player, 139, -1, Util.nextInt(60, 200));
    }

    private void effectChangeMap(Player player, int seconds, byte type) {
        Message msg;
        try {
            msg = new Message(-105);
            msg.writer().writeShort(seconds);
            msg.writer().writeByte(type);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    //kiểm tra map có thể vào với nhiệm vụ hiện tại
    public Zone checkMapCanJoin(Player player, Zone zoneJoin) {
        //dong map 
        if (zoneJoin.map.mapId == 154 || zoneJoin.map.mapId == 160) {
            return null;
        }
        if (player.isPet || player.isBoss || player.getSession() != null && player.isAdmin()) {
            return zoneJoin;
        }

        if (zoneJoin != null) {
            switch (zoneJoin.map.mapId) {
//                case 1: //đồi hoa cúc
//                case 8: //đồi nấm tím
//                case 15: //đồi hoang
//                    if (TaskService.gI().getIdTask(player) < ConstTask.TASK_2_0) {
//                        return null;
//                    }
//                    break;
//                case 42: //vách aru
//                case 43: //vách moori
//                case 44: //vách kakarot
//                    if (TaskService.gI().getIdTask(player) < ConstTask.TASK_3_1) {
//                        return null;
//                    }
//                    break;
//                case 2: //thung lũng tre
//                case 9: //thị trấn moori
//                case 16: //làng plane
//                    if (TaskService.gI().getIdTask(player) < ConstTask.TASK_4_0) {
//                        return null;
//                    }
//                    break;
//                case 24: //trạm tàu vũ trụ trái đất
//                case 25: //trạm tàu vũ trụ namếc
//                case 26: //trạm tàu vũ trụ xayda
//                    if (TaskService.gI().getIdTask(player) < ConstTask.TASK_6_0) {
//                        return null;
//                    }
//                    break;
//                case 3: //rừng nấm
//                case 11: //thung lũng maima
//                case 17: //rừng nguyên sinh
//                    if (TaskService.gI().getIdTask(player) < ConstTask.TASK_7_0) {
//                        return null;
//                    }
//                    break;
//                case 27: //rừng bamboo
//                case 28: //rừng dương xỉ
//                case 31: //núi hoa vàng
//                case 32: //núi hoa tím
//                case 35: //rừng cọ
//                case 36: //rừng đá
//                    if (TaskService.gI().getIdTask(player) < ConstTask.TASK_14_0) {
//                        return null;
//                    }
//                    break;
//                case 30: //đảo bulong
//                case 34: //đông nam guru
//                case 38: //bờ vực đen
//                    if (TaskService.gI().getIdTask(player) < ConstTask.TASK_15_0) {
//                        return null;
//                    }
//                    break;
//                case 6: //đông karin
//                case 10: //thung lũng namếc
//                case 19: //thành phố vegeta
//                    if (TaskService.gI().getIdTask(player) < ConstTask.TASK_16_0) {
//                        return null;
//                    }
//                    break;
                case 68: //thung lũng nappa
                case 69: //vực cấm
                case 70: //núi appule
                case 71: //căn cứ rasphery
                case 72: //thung lũng rasphery
                case 64: //núi dây leo
                case 65: //núi cây quỷ
                    if (TaskService.gI().getIdTask(player) < ConstTask.TASK_18_0) {
                        return null;
                    }
                    break;
                case 63: //trại lính fide
                case 66: //trại quỷ già
                case 67: //vực chết
                case 73: //thung lũng chết
                case 74: //đồi cây fide
                case 75: //khe núi tử thần
                case 76: //núi đá
                case 77: //rừng đá
                    if (TaskService.gI().getIdTask(player) < ConstTask.TASK_19_0) {
                        return null;
                    }
                    break;
                case 81: //hang quỷ chim
                case 82: //núi khỉ đen
                case 83: //hang khỉ đen
                case 79: //núi khỉ đỏ
                    if (TaskService.gI().getIdTask(player) < ConstTask.TASK_20_1) {
                        return null;
                    }
                    break;
                case 80: //núi khỉ vàng
                    if (TaskService.gI().getIdTask(player) < ConstTask.TASK_21_1) {
                        return null;
                    }
                    break;
                case 105: //cánh đồng tuyết
                case 106: //rừng tuyết
                case 107: //núi tuyết
                case 108: //dòng sông băng
                case 109: //rừng băng
                case 110: //hang băng
                    if (TaskService.gI().getIdTask(player) < ConstTask.TASK_21_4) {
                        return null;
                    }
                    break;
                case 102: //nhà bunma
                case 92: //thành phố phía đông
                case 93: //thành phố phía nam
                case 94: //đảo balê
                case 96: //cao nguyên
                case 97: //thành phố phía bắc
                case 98: //ngọn núi phía bắc
                case 99: //thung lũng phía bắc
                case 100: //thị trấn ginder
                    if (TaskService.gI().getIdTask(player) < ConstTask.TASK_20_0) {
                        return null;
                    }
                    break;
                case 103: //võ đài xên
                    if (TaskService.gI().getIdTask(player) < ConstTask.TASK_25_0) {
                        return null;
                    }
                    break;
            }
        }
        if (zoneJoin != null) {
            switch (player.gender) {
                case ConstPlayer.TRAI_DAT:
                    if (zoneJoin.map.mapId == 22 || zoneJoin.map.mapId == 23) {
                        zoneJoin = null;
                    }
                    break;
                case ConstPlayer.NAMEC:
                    if (zoneJoin.map.mapId == 21 || zoneJoin.map.mapId == 23) {
                        zoneJoin = null;
                    }
                    break;
                case ConstPlayer.XAYDA:
                    if (zoneJoin.map.mapId == 21 || zoneJoin.map.mapId == 22) {
                        zoneJoin = null;
                    }
                    break;
            }
        }
        return zoneJoin;
    }

    private void checkJoinSpecialMap(Player player) {
        if (player != null && player.zone != null) {
            switch (player.zone.map.mapId) {
                //map ngọc rồng đen
                case 85:
                case 86:
                case 87:
                case 88:
                case 89:
                case 90:
                case 91:
                    BlackBallWar.gI().joinMapBlackBallWar(player);
                    break;
            }
        }
    }
}
