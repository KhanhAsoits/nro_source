package com.girlkun.models.npc;

import com.girlkun.consts.ConstMap;
import com.girlkun.services.NpcService;
import com.girlkun.consts.ConstNpc;
import com.girlkun.consts.ConstPlayer;
import com.girlkun.consts.ConstTask;
import com.girlkun.jdbc.daos.PlayerDAO;
import com.girlkun.models.clan.Clan;
import com.girlkun.models.clan.ClanMember;
import java.util.HashMap;
import com.girlkun.services.ClanService;
import com.girlkun.services.func.ChangeMapService;
import com.girlkun.services.func.SummonDragon;
import static com.girlkun.services.func.SummonDragon.SHENRON_1_STAR_WISHES_1;
import static com.girlkun.services.func.SummonDragon.SHENRON_1_STAR_WISHES_2;
import static com.girlkun.services.func.SummonDragon.SHENRON_SAY;
import com.girlkun.services.MapService;
import com.girlkun.models.player.Player;
import com.girlkun.models.item.Item;
import com.girlkun.models.item.Item.ItemOption;
import com.girlkun.models.map.Map;
import com.girlkun.models.map.blackball.BlackBallWar;
import com.girlkun.models.map.doanhtrai.DoanhTrai;
import com.girlkun.models.map.doanhtrai.DoanhTraiService;
import com.girlkun.models.player.Inventory;
import com.girlkun.models.player.NPoint;
import com.girlkun.models.matches.PVPService;
import com.girlkun.models.shop.ShopServiceNew;
import com.girlkun.server.Client;
import com.girlkun.server.Maintenance;
import com.girlkun.server.Manager;
import com.girlkun.services.FriendAndEnemyService;
import com.girlkun.services.IntrinsicService;
import com.girlkun.services.ItemService;
import com.girlkun.services.OpenPowerService;
import com.girlkun.services.PetService;
import com.girlkun.services.PlayerService;
import com.girlkun.services.Service;
import com.girlkun.services.TaskService;
import com.girlkun.services.func.CombineServiceNew;
import com.girlkun.services.func.Input;
import com.girlkun.services.func.LuckyRound;
import com.girlkun.services.func.TopService;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.utils.Logger;
import com.girlkun.utils.TimeUtil;
import com.girlkun.utils.Util;

/**
 *
 * @author ???? Tr???n L???i ????
 * @copyright ???? GirlkuN ????
 *
 */
public class NpcFactory {

    private static boolean nhanVang = false;
    private static boolean nhanDeTu = false;

    //playerid - object
    public static final java.util.Map<Long, Object> PLAYERID_OBJECT = new HashMap<Long, Object>();

    private NpcFactory() {

    }

    private static Npc quyLaoKame(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ch??o con, con mu???n ta gi??p g?? n??o?", "Nh???n 2 t??? v??ng", "Gi???i t??n bang h???i", "T??? ch???i");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                if (player.inventory.gold == Inventory.LIMIT_GOLD) {
                                    this.npcChat(player, "B?? ??t th??i con");
                                    break;
                                }
                                player.inventory.gold = 2000000000;
                                Service.getInstance().sendMoney(player);
                                String[] tho = {
                                    "2 3 con m???c\nanh y??u em c???c",
                                    "3 chai t??ng l???c\ny??u em c??ng c???c",
                                    "?? ??a \nM??nh ch??? l?? m???t ng?????i ?????n sau:(",
                                    "Ti??u l???m v??ng th??? con?\n??ng s???p h???t v??ng r???i ?????y!",
                                    "?????i con d??i, trai c??n nhi???u. Ti???n ch??a ti??u, y??u chi v???i",
                                    "Ngh??o th?? ph???i s???ch m?? r??ch th?? SEXY",
                                    "V?? m??nh qu?? th??ch c???u r???i, \nph???i l??m sao ph???i l??m sao?",
                                    "B???p lu???c ?????!!"};
                                this.npcChat(player, tho[Util.nextInt(tho.length)]);
                                break;
                            case 1:
                                Clan clan = player.clan;
                                if (clan != null) {
                                    ClanMember cm = clan.getClanMember((int) player.id);
                                    if (cm != null) {
                                        if (clan.members.size() > 1) {
                                            Service.getInstance().sendThongBao(player, "Bang ph???i c??n m???t ng?????i");
                                            break;
                                        }
                                        if (!clan.isLeader(player)) {
                                            Service.getInstance().sendThongBao(player, "Ph???i l?? b???ng ch???");
                                            break;
                                        }
//                                        
                                        NpcService.gI().createMenuConMeo(player, ConstNpc.CONFIRM_DISSOLUTION_CLAN, -1, "Con c?? ch???c ch???n mu???n gi???i t??n bang h???i kh??ng? Ta cho con 2 l???a ch???n...",
                                                "Yes you do!", "T??? ch???i!");
                                    }
                                    break;
                                }
                                Service.getInstance().sendThongBao(player, "C?? bang h???i ????u ba!!!");
                                break;
                        }
                    }
                }
            }
        };
    }

    public static Npc truongLaoGuru(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        super.openBaseMenu(player);
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {

                }
            }
        };
    }

    public static Npc vuaVegeta(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        super.openBaseMenu(player);
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {

                }
            }
        };
    }

    public static Npc ongGohan_ongMoori_ongParagus(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Con c??? g???ng theo %1 h???c th??nh t??i, ?????ng lo l???ng cho ta."
                                .replaceAll("%1", player.gender == ConstPlayer.TRAI_DAT ? "Quy l??o Kam??"
                                                : player.gender == ConstPlayer.NAMEC ? "Tr?????ng l??o Guru" : "Vua Vegeta"),
                                "?????i m???t kh???u", "Nh???n 200k ng???c xanh", "Nh???n 2 t??? v??ng", "Nh???n ????? t???", "Test");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                Input.gI().createFormChangePassword(player);
                                break;
                            case 1:
                                if (player.inventory.gem == 200000) {
                                    this.npcChat(player, "B?? ??t th??i con");
                                    break;
                                }
                                player.inventory.gem = 200000;
                                Service.getInstance().sendMoney(player);
                                Service.getInstance().sendThongBao(player, "B???n v???a nh???n ???????c 200K ng???c xanh");
                                break;
                            case 2:
                                if (!(player.inventory.gold == Inventory.LIMIT_GOLD)) {
                                    player.inventory.gold = Inventory.LIMIT_GOLD;
                                    Service.getInstance().sendMoney(player);
                                    Service.getInstance().sendThongBao(player, "B???n v???a nh???n ???????c 2 t??? v??ng");
                                } else {
                                    this.npcChat(player, "B?? ??t th??i con");
                                }
                                break;
                            case 3:
                                if (player.pet == null) {
                                    PetService.gI().createNormalPet(player);
                                    Service.getInstance().sendThongBao(player, "B???n v???a nh???n ???????c ????? t???");
                                } else {
                                    this.npcChat(player, "B?? ??t th??i con");
                                }
                                break;
                            case 4:
                                TopService.gI().sendTabTop(player, "Suc manh");
                                break;

                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.QUA_TAN_THU) {
                        switch (select) {
                            case 0:
//                                        if (!player.gift.gemTanThu) {
                                if (true) {
                                    player.inventory.gem = 100000;
                                    Service.getInstance().sendMoney(player);
                                    Service.getInstance().sendThongBao(player, "B???n v???a nh???n ???????c 100K ng???c xanh");
                                    player.gift.gemTanThu = true;
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Con ???? nh???n ph???n qu?? n??y r???i m??",
                                            "????ng");
                                }
                                break;
                            case 1:
                                if (nhanVang) {
                                    player.inventory.gold = Inventory.LIMIT_GOLD;
                                    Service.getInstance().sendMoney(player);
                                    Service.getInstance().sendThongBao(player, "B???n v???a nh???n ???????c 2 t??? v??ng");
                                } else {
                                    this.npcChat("");
                                }
                                break;
                            case 2:
                                if (nhanDeTu) {
                                    if (player.pet == null) {
                                        PetService.gI().createNormalPet(player);
                                        Service.getInstance().sendThongBao(player, "B???n v???a nh???n ???????c ????? t???");
                                    } else {
                                        this.npcChat("Con ???? nh???n ????? t??? r???i");
                                    }
                                }
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_PHAN_THUONG) {
                        switch (select) {
                            case 0:
                                ShopServiceNew.gI().opendShop(player, "ITEMS_REWARD", true);
                                break;
                            case 1:
                                if (player.getSession().goldBar > 0) {
                                    if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                                        int quantity = player.getSession().goldBar;
                                        Item goldBar = ItemService.gI().createNewItem((short) 457, quantity);
                                        InventoryServiceNew.gI().addItemBag(player, goldBar);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "??ng ???? ????? " + quantity + " th???i v??ng v??o h??nh trang con r???i ?????y");
                                        PlayerDAO.subGoldBar(player, quantity);
                                        player.getSession().goldBar = 0;
                                    } else {
                                        this.npcChat(player, "Con ph???i c?? ??t nh???t 1 ?? tr???ng trong h??nh trang ??ng m???i ????a cho con ???????c");
                                    }
                                }
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.NAP_THE) {
                        Input.gI().createFormNapThe(player, (byte) select);
                    }
                }
            }
        };
    }

    public static Npc bulmaQK(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "C???u c???n trang b??? g?? c??? ?????n ch??? t??i nh??", "C???a\nh??ng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0://Shop
                                if (player.gender == ConstPlayer.TRAI_DAT) {
                                    ShopServiceNew.gI().opendShop(player, "BUNMA", true);
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Xin l???i c??ng, ch??? ch??? b??n ????? cho ng?????i Tr??i ?????t", "????ng");
                                }
                                break;
                        }
                    }
                }
            }
        };
    }

    public static Npc dende(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Anh c???n trang b??? g?? c??? ?????n ch??? em nh??", "C???a\nh??ng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0://Shop
                                if (player.gender == ConstPlayer.NAMEC) {
                                    ShopServiceNew.gI().opendShop(player, "DENDE", true);
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Xin l???i anh, em ch??? b??n ????? cho d??n t???c Nam???c", "????ng");
                                }
                                break;
                        }
                    }
                }
            }
        };
    }

    public static Npc appule(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Ng????i c???n trang b??? g?? c??? ?????n ch??? ta nh??", "C???a\nh??ng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0://Shop
                                if (player.gender == ConstPlayer.XAYDA) {
                                    ShopServiceNew.gI().opendShop(player, "APPULE", true);
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "V??? h??nh tinh h??? ?????ng c???a ng????i m?? mua ????? c??i nh??. T???i ????y ta ch??? b??n ????? cho ng?????i Xayda th??i", "????ng");
                                }
                                break;
                        }
                    }
                }
            }
        };
    }

    public static Npc drDrief(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player pl) {
                if (canOpenNpc(pl)) {
                    if (this.mapId == 84) {
                        this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                "T??u V?? Tr??? c???a ta c?? th??? ????a c???u ?????n h??nh tinh kh??c ch??? trong 3 gi??y. C???u mu???n ??i ????u?",
                                pl.gender == ConstPlayer.TRAI_DAT ? "?????n\nTr??i ?????t" : pl.gender == ConstPlayer.NAMEC ? "?????n\nNam???c" : "?????n\nXayda");
                    } else if (!TaskService.gI().checkDoneTaskTalkNpc(pl, this)) {
                        if (pl.playerTask.taskMain.id == 7) {
                            NpcService.gI().createTutorial(pl, this.avartar, "H??y l??n ???????ng c???u ?????a b?? nh?? t??i\n"
                                    + "Ch???c b??y gi??? n?? ??ang s??? h??i l???m r???i");
                        } else {
                            this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                    "T??u V?? Tr??? c???a ta c?? th??? ????a c???u ?????n h??nh tinh kh??c ch??? trong 3 gi??y. C???u mu???n ??i ????u?",
                                    "?????n\nNam???c", "?????n\nXayda", "Si??u th???");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 84) {
                        ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 24, -1, -1);
                    } else if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 25, -1, -1);
                                break;
                            case 1:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 26, -1, -1);
                                break;
                            case 2:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 84, -1, -1);
                                break;
                        }
                    }
                }
            }
        };
    }

    public static Npc cargo(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player pl) {
                if (canOpenNpc(pl)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(pl, this)) {
                        if (pl.playerTask.taskMain.id == 7) {
                            NpcService.gI().createTutorial(pl, this.avartar, "H??y l??n ???????ng c???u ?????a b?? nh?? t??i\n"
                                    + "Ch???c b??y gi??? n?? ??ang s??? h??i l???m r???i");
                        } else {
                            this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                    "T??u V?? Tr??? c???a ta c?? th??? ????a c???u ?????n h??nh tinh kh??c ch??? trong 3 gi??y. C???u mu???n ??i ????u?",
                                    "?????n\nTr??i ?????t", "?????n\nXayda", "Si??u th???");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 24, -1, -1);
                                break;
                            case 1:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 26, -1, -1);
                                break;
                            case 2:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 84, -1, -1);
                                break;
                        }
                    }
                }
            }
        };
    }

    public static Npc cui(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

//            private final int COST_FIND_BOSS = 20000000;

            @Override
            public void openBaseMenu(Player pl) {
                if (canOpenNpc(pl)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(pl, this)) {
                        if (pl.playerTask.taskMain.id == 7) {
                            NpcService.gI().createTutorial(pl, this.avartar, "H??y l??n ???????ng c???u ?????a b?? nh?? t??i\n"
                                    + "Ch???c b??y gi??? n?? ??ang s??? h??i l???m r???i");
                        } else {
                            if (this.mapId == 19) {

                                int taskId = TaskService.gI().getIdTask(pl);
                                switch (taskId) {
//                                    case ConstTask.TASK_19_0:
//                                        this.createOtherMenu(pl, ConstNpc.MENU_FIND_KUKU,
//                                                "?????i qu??n c???a Fide ??ang ??? Thung l??ng Nappa, ta s??? ????a ng????i ?????n ????",
//                                                "?????n ch???\nKuku\n(" + Util.numberToMoney(COST_FIND_BOSS) + " v??ng)", "?????n Cold", "?????n\nNappa", "T??? ch???i");
//                                        break;
//                                    case ConstTask.TASK_19_1:
//                                        this.createOtherMenu(pl, ConstNpc.MENU_FIND_MAP_DAU_DINH,
//                                                "?????i qu??n c???a Fide ??ang ??? Thung l??ng Nappa, ta s??? ????a ng????i ?????n ????",
//                                                "?????n ch???\nM???p ?????u ??inh\n(" + Util.numberToMoney(COST_FIND_BOSS) + " v??ng)", "?????n Cold", "?????n\nNappa", "T??? ch???i");
//                                        break;
//                                    case ConstTask.TASK_19_2:
//                                        this.createOtherMenu(pl, ConstNpc.MENU_FIND_RAMBO,
//                                                "?????i qu??n c???a Fide ??ang ??? Thung l??ng Nappa, ta s??? ????a ng????i ?????n ????",
//                                                "?????n ch???\nRambo\n(" + Util.numberToMoney(COST_FIND_BOSS) + " v??ng)", "?????n Cold", "?????n\nNappa", "T??? ch???i");
//                                        break;
                                    default:
                                        this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                                "?????i qu??n c???a Fide ??ang ??? Thung l??ng Nappa, ta s??? ????a ng????i ?????n ????",
                                                "?????n Cold", "?????n\nNappa", "T??? ch???i");

                                        break;
                                }
                            } else if (this.mapId == 68) {
                                this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                        "Ng????i mu???n v??? Th??nh Ph??? Vegeta", "?????ng ??", "T??? ch???i");
                            } else {
                                this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                        "T??u v?? tr??? Xayda s??? d???ng c??ng ngh??? m???i nh???t, "
                                        + "c?? th??? ????a ng????i ??i b???t k??? ????u, ch??? c???n tr??? ti???n l?? ???????c.",
                                        "?????n\nTr??i ?????t", "?????n\nNam???c", "Si??u th???");
                            }
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 26) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 24, -1, -1);
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 25, -1, -1);
                                    break;
                                case 2:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 84, -1, -1);
                                    break;
                            }
                        }
                    }
                    if (this.mapId == 19) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
                                    break;
                            }
                        }
                    }
//                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_FIND_KUKU) {
//                            switch (select) {
//                                case 0:
////                                    Boss boss = BossManager.gI().getBossById(BossFactory.KUKU);
////                                    if (boss != null && !boss.isDie()) {
////                                        if (player.inventory.gold >= COST_FIND_BOSS) {
////                                            player.inventory.gold -= COST_FIND_BOSS;
////                                            ChangeMapService.gI().changeMap(player, boss.zone, boss.location.x, boss.location.y);
////                                            Service.getInstance().sendMoney(player);
////                                        } else {
////                                            Service.getInstance().sendThongBao(player, "Kh??ng ????? v??ng, c??n thi???u "
////                                                    + Util.numberToMoney(COST_FIND_BOSS - player.inventory.gold) + " v??ng");
////                                        }
////                                    }
//                                    break;
//                                case 1:
//                                    ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
//                                    break;
//                                case 2:
//                                    ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
//                                    break;
//                            }
//                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_FIND_MAP_DAU_DINH) {
//                            switch (select) {
//                                case 0:
////                                    Boss boss = BossManager.gI().getBossById(BossFactory.MAP_DAU_DINH);
////                                    if (boss != null && !boss.isDie()) {
////                                        if (player.inventory.gold >= COST_FIND_BOSS) {
////                                            player.inventory.gold -= COST_FIND_BOSS;
////                                            ChangeMapService.gI().changeMap(player, boss.zone, boss.location.x, boss.location.y);
////                                            Service.getInstance().sendMoney(player);
////                                        } else {
////                                            Service.getInstance().sendThongBao(player, "Kh??ng ????? v??ng, c??n thi???u "
////                                                    + Util.numberToMoney(COST_FIND_BOSS - player.inventory.gold) + " v??ng");
////                                        }
////                                    }
//                                    break;
//                                case 1:
//                                    ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
//                                    break;
//                                case 2:
//                                    ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
//                                    break;
//                            }
//                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_FIND_RAMBO) {
//                            switch (select) {
//                                case 0:
////                                    Boss boss = BossManager.gI().getBossById(BossFactory.RAMBO);
////                                    if (boss != null && !boss.isDie()) {
////                                        if (player.inventory.gold >= COST_FIND_BOSS) {
////                                            player.inventory.gold -= COST_FIND_BOSS;
////                                            ChangeMapService.gI().changeMap(player, boss.zone, boss.location.x, boss.location.y);
////                                            Service.getInstance().sendMoney(player);
////                                        } else {
////                                            Service.getInstance().sendThongBao(player, "Kh??ng ????? v??ng, c??n thi???u "
////                                                    + Util.numberToMoney(COST_FIND_BOSS - player.inventory.gold) + " v??ng");
////                                        }
////                                    }
//                                    break;
//                                case 1:
//                                    ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
//                                    break;
//                                case 2:
//                                    ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
//                                    break;
//                            }
//                        }
//                    }
                    if (this.mapId == 68) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 19, -1, 1100);
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc santa(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Xin ch??o, ta c?? m???t s??? v???t ph???m ?????t bi???t c???u c?? mu???n xem kh??ng?",
                            "C???a h??ng", "Ti???m\nh???t t??c");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5 || this.mapId == 13 || this.mapId == 20) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0: //shop
                                    ShopServiceNew.gI().opendShop(player, "SANTA", false);
                                    break;
                                case 1: //ti???m h???t t??c
                                    ShopServiceNew.gI().opendShop(player, "SANTA_HEAD", false);
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc uron(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player pl) {
                if (canOpenNpc(pl)) {
                    ShopServiceNew.gI().opendShop(pl, "URON", false);
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {

                }
            }
        };
    }

    public static Npc baHatMit(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Ng????i t??m ta c?? vi???c g???",
                                "??p sao\ntrang b???", "Pha l??\nh??a\ntrang b???");
                    } else if (this.mapId == 121) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Ng????i t??m ta c?? vi???c g???",
                                "V??? ?????o\nr??a");

                    } else {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Ng????i t??m ta c?? vi???c g???",
                                "C???a h??ng\nB??a", "N??ng c???p\nV???t ph???m",
                                "N??ng c???p\nB??ng tai\nPorata", "L??m ph??p\nNh???p ????",
                                "Nh???p\nNg???c R???ng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
//                                                CombineService.gI().openTabCombine(player, CombineService.EP_SAO_TRANG_BI);
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.EP_SAO_TRANG_BI);
                                    break;
                                case 1:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.PHA_LE_HOA_TRANG_BI);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                            switch (player.combineNew.typeCombine) {
                                case CombineServiceNew.EP_SAO_TRANG_BI:
                                case CombineServiceNew.PHA_LE_HOA_TRANG_BI:
                                case CombineServiceNew.CHUYEN_HOA_TRANG_BI:
                                    if (select == 0) {
                                        CombineServiceNew.gI().startCombine(player);
                                    }
                                    break;
                            }
                        }
                    } else if (this.mapId == 112) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
                                    break;
                            }
                        }
                    } else if (this.mapId == 42 || this.mapId == 43 || this.mapId == 44) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0: //shop b??a
                                    createOtherMenu(player, ConstNpc.MENU_OPTION_SHOP_BUA,
                                            "B??a c???a ta r???t l???i h???i, nh??n ng????i y???u ??u???i th??? n??y, ch???c mu???n mua b??a ????? "
                                            + "m???nh m??? ??, mua kh??ng ta b??n cho, x??i r???i l???i th??ch cho m?? xem.",
                                            "B??a\n1 gi???", "B??a\n8 gi???", "B??a\n1 th??ng", "????ng");
                                    break;
                                case 1:
//                                                CombineService.gI().openTabCombine(player, CombineService.NANG_CAP_TRANG_BI);
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_VAT_PHAM);
                                    break;
                                case 2: //n??ng c???p b??ng tai
                                    break;
                                case 3: //l??m ph??p nh???p ????
                                    break;
                                case 4:
//                                                CombineService.gI().openTabCombine(player, CombineService.NHAP_NGOC_RONG);
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NHAP_NGOC_RONG);
                                    break;

                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_SHOP_BUA) {
                            switch (select) {
                                case 0:
                                    ShopServiceNew.gI().opendShop(player, "BUA_1H", true);
                                    break;
                                case 1:
                                    ShopServiceNew.gI().opendShop(player, "BUA_8H", true);
                                    break;
                                case 2:
                                    ShopServiceNew.gI().opendShop(player, "BUA_1M", true);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                            switch (player.combineNew.typeCombine) {
                                case CombineServiceNew.NANG_CAP_VAT_PHAM:
                                case CombineServiceNew.NANG_CAP_BONG_TAI:
                                case CombineServiceNew.LAM_PHEP_NHAP_DA:
                                case CombineServiceNew.NHAP_NGOC_RONG:
                                    if (select == 0) {
                                        CombineServiceNew.gI().startCombine(player);
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc ruongDo(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    InventoryServiceNew.gI().sendItemBox(player);
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {

                }
            }
        };
    }

    public static Npc dauThan(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    player.magicTree.openMenuTree();
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    TaskService.gI().checkDoneTaskConfirmMenuNpc(player, this, (byte) select);
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.MAGIC_TREE_NON_UPGRADE_LEFT_PEA:
                            if (select == 0) {
                                player.magicTree.harvestPea();
                            } else if (select == 1) {
                                if (player.magicTree.level == 10) {
                                    player.magicTree.fastRespawnPea();
                                } else {
                                    player.magicTree.showConfirmUpgradeMagicTree();
                                }
                            } else if (select == 2) {
                                player.magicTree.fastRespawnPea();
                            }
                            break;
                        case ConstNpc.MAGIC_TREE_NON_UPGRADE_FULL_PEA:
                            if (select == 0) {
                                player.magicTree.harvestPea();
                            } else if (select == 1) {
                                player.magicTree.showConfirmUpgradeMagicTree();
                            }
                            break;
                        case ConstNpc.MAGIC_TREE_CONFIRM_UPGRADE:
                            if (select == 0) {
                                player.magicTree.upgradeMagicTree();
                            }
                            break;
                        case ConstNpc.MAGIC_TREE_UPGRADE:
                            if (select == 0) {
                                player.magicTree.fastUpgradeMagicTree();
                            } else if (select == 1) {
                                player.magicTree.showConfirmUnuppgradeMagicTree();
                            }
                            break;
                        case ConstNpc.MAGIC_TREE_CONFIRM_UNUPGRADE:
                            if (select == 0) {
                                player.magicTree.unupgradeMagicTree();
                            }
                            break;
                    }
                }
            }
        };
    }

    public static Npc calick(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            private final byte COUNT_CHANGE = 50;
            private int count;

            private void changeMap() {
                if (this.mapId != 102) {
                    count++;
                    if (this.count >= COUNT_CHANGE) {
                        count = 0;
                        this.map.npcs.remove(this);
                        Map map = MapService.gI().getMapForCalich();
                        this.mapId = map.mapId;
                        this.cx = Util.nextInt(100, map.mapWidth - 100);
                        this.cy = map.yPhysicInTop(this.cx, 0);
                        this.map = map;
                        this.map.npcs.add(this);
                    }
                }
            }

            @Override
            public void openBaseMenu(Player player) {
                player.iDMark.setIndexMenu(ConstNpc.BASE_MENU);
                if (TaskService.gI().getIdTask(player) < ConstTask.TASK_20_0) {
                    Service.getInstance().hideWaitDialog(player);
                    Service.getInstance().sendThongBao(player, "Kh??ng th??? th???c hi???n");
                    return;
                }
                if (this.mapId != player.zone.map.mapId) {
                    Service.getInstance().sendThongBao(player, "Cal??ch ???? r???i kh???i map!");
                    Service.getInstance().hideWaitDialog(player);
                    return;
                }

                if (this.mapId == 102) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Ch??o ch??, ch??u c?? th??? gi??p g???",
                            "K???\nChuy???n", "Quay v???\nQu?? kh???");
                } else {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Ch??o ch??, ch??u c?? th??? gi??p g???", "K???\nChuy???n", "??i ?????n\nT????ng lai", "T??? ch???i");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (this.mapId == 102) {
                    if (player.iDMark.isBaseMenu()) {
                        if (select == 0) {
                            //k??? chuy???n
                            NpcService.gI().createTutorial(player, this.avartar, ConstNpc.CALICK_KE_CHUYEN);
                        } else if (select == 1) {
                            //v??? qu?? kh???
                            ChangeMapService.gI().goToQuaKhu(player);
                        }
                    }
                } else if (player.iDMark.isBaseMenu()) {
                    if (select == 0) {
                        //k??? chuy???n
                        NpcService.gI().createTutorial(player, this.avartar, ConstNpc.CALICK_KE_CHUYEN);
                    } else if (select == 1) {
                        //?????n t????ng lai
//                                    changeMap();
                        if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_20_0) {
                            ChangeMapService.gI().goToTuongLai(player);
                        }
                    } else {
                        Service.getInstance().sendThongBao(player, "Kh??ng th??? th???c hi???n");
                    }
                }
            }
        };
    }

    public static Npc jaco(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 24) {
                        if (player.iDMark.isBaseMenu()) {
                            if (select == 0) {
                                //?????n potaufeu
                                ChangeMapService.gI().goToPotaufeu(player);
                            }
                        }
                    } else if (this.mapId == 139) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 24, -1, -1);
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 25, -1, -1);
                                    break;
                                case 2:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 26, -1, -1);
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc thuongDe(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 45) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Con mu???n l??m g?? n??o", "?????n Kaio", "Quay s???\nmay m???n");
                    }
                    if (this.mapId == 0){
                        this.createOtherMenu(player, 0,
                                "Con mu???n g?? n??o??", "?????n DHVT", "?????i C???i trang s??? ki??n", "Item Eat");
                    }
                    if (this.mapId == 129){
                        this.createOtherMenu(player, 0,
                                "Con mu???n g?? n??o?", "Quay ve");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 0){
                        if (player.iDMark.getIndexMenu() == 0){ // 
                            switch (select){
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 129, -1, 354);
                                    Service.getInstance().changeFlag(player, Util.nextInt(8));
                                    break; // qua dhvt
                                case 1:  // 
                                    this.createOtherMenu(player, 1,
                                "B???n c?? mu???n ?????i 350 ??i???m PVP l???y \n|6|C???i trang Th???y Th??? M???t Tr??ng v???i t???t c??? ch??? s??? l?? 80%\n ", "Ok", "Tu choi");
                                   // bat menu doi item
                                    break;
                                case 2:
                                    this.createOtherMenu(player, 2,
                                "B???n mu???n ?????i 20 ??i???m PVP\n|6|????? ?????i l???y x20 ITEM kh??ng?",  "Gi??p x??n", "B??? Huy???t","Cu???ng N???","B??? kh??","Tu choi");
                                    break;
                                   
                            }
                        }
                        if (player.iDMark.getIndexMenu() == 1){ // action doi item
                            switch (select){
                                case 0: // trade
                                    if (player.pointPvp >= 350){
                                        player.pointPvp -= 350;
                                        Item item = ItemService.gI().createNewItem((short)760);
//                                         item.quantity = 20;
                                        item.itemOptions.add(new ItemOption(49,80));
                                         item.itemOptions.add(new ItemOption(77,80));
                                         item.itemOptions.add(new ItemOption(103,80));
//                                       item.itemOptions.add(new ItemOption(7,8000));
//                                       item.itemOptions.add(new ItemOption(159,0));
                                        InventoryServiceNew.gI().addItemBag(player, item);
                                        Service.getInstance().sendThongBao(player, "Chuc mung ban da doi thanh cong Cai Trang !");
                                    }else{
                                        Service.getInstance().sendThongBao(player, "Khong du diem, ban con " +(350 - player.pointPvp) +" diem nua");
                                    }
                                    break;
//                                 case 1: // trade
//                                    if (player.pointPvp >= 100){
//                                        player.pointPvp -= 100;
//                                        Item item = ItemService.gI().createNewItem((short)590);
//                                         item.quantity = 10000;
////                                        item.itemOptions.add(new ItemOption(49,80));
////                                         item.itemOptions.add(new ItemOption(77,80));
////                                         item.itemOptions.add(new ItemOption(103,80));
////                                       item.itemOptions.add(new ItemOption(7,8000));
////                                       item.itemOptions.add(new ItemOption(159,0));
//                                        InventoryServiceNew.gI().addItemBag(player, item);
//                                        Service.getInstance().sendThongBao(player, "Chuc mung ban da doi thanh cong Bi kip !");
//                                    }else{
//                                        Service.getInstance().sendThongBao(player, "Khong du diem, ban con " +(100 - player.pointPvp) +" diem nua");
//                                    }
//                                    break;   
                                case 2: // canel
                                  break;
                            }
                        }
                        if (player.iDMark.getIndexMenu() == 2){ // action doi item 2
                            switch (select){
                                case 0: // trade
                                    if (player.pointPvp >= 20){
                                        player.pointPvp -= 20; // - 10 diem PVP
                                        Item item = ItemService.gI().createNewItem((short)384);// id item giap xen
                                         item.quantity = 20;
//                                        item.itemOptions.add(new ItemOption(49,25)); // option suc danh 25%
////                                        item.itemOptions.add(new ItemOption(159,0)); // option x4 kame
                                        InventoryServiceNew.gI().addItemBag(player, item);
                                        Service.getInstance().sendThongBao(player, "Ch??c m???ng b???n ???? ?????i ???????c x20 Gi??p x??n!");
                                    }else{
                                        Service.getInstance().sendThongBao(player, "B???n kh??ng ????? ??i???m, b???n c??n " +(20 - player.pointPvp) +" ??i???m ????? ?????i n???a");
                                    }
                                case 1: // trade
                                    if (player.pointPvp >= 20){
                                        player.pointPvp -= 20; // - 10 diem PVP
                                        Item item = ItemService.gI().createNewItem((short)382); // id item bo huyet
                                         item.quantity = 20;
//                                        item.itemOptions.add(new ItemOption(49,25)); // option suc danh 25%
//                                        item.itemOptions.add(new ItemOption(159,0)); // option x4 kame
                                        InventoryServiceNew.gI().addItemBag(player, item);
                                        Service.getInstance().sendThongBao(player, "Ch??c m???ng b???n ???? ?????i ???????c x20 B??? huy???t!");
                                    }else{
                                        Service.getInstance().sendThongBao(player, "B???n kh??ng ????? ??i???m, b???n c??n " +(20 - player.pointPvp) +" ??i???m ????? ?????i n???a");
                                    }
                                case 2: // trade
                                    if (player.pointPvp >= 20){
                                        player.pointPvp -= 20; // - 10 diem PVP
                                        Item item = ItemService.gI().createNewItem((short)381); // id item cuong no
                                         item.quantity = 20;
//                                        item.itemOptions.add(new ItemOption(49,25)); // option suc danh 25%
//                                        item.itemOptions.add(new ItemOption(159,0)); // option x4 kame
                                        InventoryServiceNew.gI().addItemBag(player, item);
                                        Service.getInstance().sendThongBao(player, "Ch??c m???ng b???n ???? ?????i ???????c x20 Cu???ng n???!");
                                    }else{
                                        Service.getInstance().sendThongBao(player, "B???n kh??ng ????? ??i???m, b???n c??n " +(20 - player.pointPvp) +" ??i???m ????? ?????i n???a");
                                    }
                                 case 3: // trade
                                    if (player.pointPvp >= 20){
                                        player.pointPvp -= 20; // - 10 diem PVP
                                        Item item = ItemService.gI().createNewItem((short)383); // id item bo khi
                                         item.quantity = 20;
//                                        item.itemOptions.add(new ItemOption(49,25)); // option suc danh 25%
//                                        item.itemOptions.add(new ItemOption(159,0)); // option x4 kame
                                        InventoryServiceNew.gI().addItemBag(player, item);
                                        Service.getInstance().sendThongBao(player, "Ch??c m???ng b???n ???? ?????i ???????c x20 B??? kh??!");
                                    }else{
                                        Service.getInstance().sendThongBao(player, "B???n kh??ng ????? ??i???m, b???n c??n " +(20 - player.pointPvp) +" ??i???m ????? ?????i n???a");
                                    }
                                    break;
                        
                                case 4: // canel
                                  break;
//                            }
                        } 
                    }
                    if (this.mapId == 129){
                        switch (select){
                            case 0: // quay ve
                                ChangeMapService.gI().changeMapBySpaceShip(player, 0, -1, 354);
                                break;
                        }
                    }
                    if (this.mapId == 45) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 48, -1, 354);
                                    break;
                                case 1:
                                    this.createOtherMenu(player, ConstNpc.MENU_CHOOSE_LUCKY_ROUND,
                                            "Con mu???n l??m g?? n??o?", "Quay b???ng\nv??ng",
                                            "R????ng ph???\n("
                                            + (player.inventory.itemsBoxCrackBall.size()
                                            - InventoryServiceNew.gI().getCountEmptyListItem(player.inventory.itemsBoxCrackBall))
                                            + " m??n)",
                                            "X??a h???t\ntrong r????ng", "????ng");
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_CHOOSE_LUCKY_ROUND) {
                            switch (select) {
                                case 0:
                                    LuckyRound.gI().openCrackBallUI(player, LuckyRound.USING_GOLD);
                                    break;
                                case 1:
                                    ShopServiceNew.gI().opendShop(player, "ITEMS_LUCKY_ROUND", true);
                                    break;
                                case 2:
                                    NpcService.gI().createMenuConMeo(player,
                                            ConstNpc.CONFIRM_REMOVE_ALL_ITEM_LUCKY_ROUND, this.avartar,
                                            "Con c?? ch???c mu???n x??a h???t v???t ph???m trong r????ng ph???? Sau khi x??a "
                                            + "s??? kh??ng th??? kh??i ph???c!",
                                            "?????ng ??", "H???y b???");
                                    break;
                            }
                        }
                    }
                }
            }
        }
        };
                }

    public static Npc thanVuTru(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 48) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Con mu???n l??m g?? n??o", "Di chuy???n");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 48) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, ConstNpc.MENU_DI_CHUYEN,
                                            "Con mu???n ??i ????u?", "V???\nth???n ??i???n", "Th??nh ?????a\nKaio", "Con\n???????ng\nr???n ?????c", "T??? ch???i");
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_DI_CHUYEN) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 45, -1, 354);
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMap(player, 50, -1, 318, 336);
                                    break;
                                case 2:
                                    //con ???????ng r???n ?????c
                                    break;
                            }
                        }
                    }
                }
            }

        };
    }

    public static Npc kibit(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 50) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta c?? th??? gi??p g?? cho ng????i ?",
                                "?????n\nKaio", "T??? ch???i");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 50) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMap(player, 48, -1, 354, 240);
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc osin(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 50) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta c?? th??? gi??p g?? cho ng????i ?",
                                "?????n\nKaio", "?????n\nh??nh tinh\nBill", "T??? ch???i");
                    } else if (this.mapId == 154) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta c?? th??? gi??p g?? cho ng????i ?",
                                "V??? th??nh ?????a", "?????n\nh??nh tinh\nng???c t??", "T??? ch???i");
                    } else if (this.mapId == 155) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta c?? th??? gi??p g?? cho ng????i ?",
                                "Quay v???", "T??? ch???i");
                    } else {
                        super.openBaseMenu(player);
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 50) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMap(player, 48, -1, 354, 240);
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMap(player, 154, -1, 200, 312);
                                    break;
                            }
                        }
                    } else if (this.mapId == 154) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMap(player, 50, -1, 318, 336);
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMap(player, 155, -1, 111, 792);
                                    break;
                            }
                        }
                    } else if (this.mapId == 155) {
                        if (player.iDMark.isBaseMenu()) {
                            if (select == 0) {
                                ChangeMapService.gI().changeMap(player, 154, -1, 200, 312);
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc linhCanh(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (player.clan == null) {
                        this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Ch??? ti???p c??c bang h???i, mi???n ti???p kh??ch v??ng lai", "????ng");
                        return;
                    }
                    if (player.clan.getMembers().size() < DoanhTrai.N_PLAYER_CLAN) {
                        this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Bang h???i ph???i c?? ??t nh???t 5 th??nh vi??n m???i c?? th??? m???", "????ng");
                        return;
                    }
                    if (player.clan.doanhTrai != null) {
                        createOtherMenu(player, ConstNpc.MENU_JOIN_DOANH_TRAI,
                                "Bang h???i c???a ng????i ??ang ????nh tr???i ?????c nh??n\n"
                                + "Th???i gian c??n l???i l?? "
                                + TimeUtil.getSecondLeft(player.clan.doanhTrai.getLastTimeOpen(), DoanhTrai.TIME_DOANH_TRAI / 1000)
                                + ". Ng????i c?? mu???n tham gia kh??ng?",
                                "Tham gia", "Kh??ng", "H?????ng\nd???n\nth??m");
                        return;
                    }
                    int nPlSameClan = 0;
                    for (Player pl : player.zone.getPlayers()) {
                        if (!pl.equals(player) && pl.clan != null
                                && pl.clan.equals(player.clan) && pl.location.x >= 1285
                                && pl.location.x <= 1645) {
                            nPlSameClan++;
                        }
                    }
                    if (nPlSameClan < DoanhTrai.N_PLAYER_MAP) {
                        createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Ng????i ph???i c?? ??t nh???t " + DoanhTrai.N_PLAYER_MAP + " ?????ng ?????i c??ng bang ?????ng g???n m???i c?? th???\nv??o\n"
                                + "tuy nhi??n ta khuy??n ng????i n??n ??i c??ng v???i 3-4 ng?????i ????? kh???i ch???t.\n"
                                + "Hahaha.", "OK", "H?????ng\nd???n\nth??m");
                        return;
                    }
                    if (player.clanMember.getNumDateFromJoinTimeToToday() < 1) {
                        createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Doanh tr???i ch??? cho ph??p nh???ng ng?????i ??? trong bang tr??n 1 ng??y. H???n ng????i quay l???i v??o l??c kh??c",
                                "OK", "H?????ng\nd???n\nth??m");
                        return;
                    }
                    if (player.clan.haveGoneDoanhTrai) {
                        createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Bang h???i c???a ng????i ???? ??i tr???i l??c " + TimeUtil.formatTime(player.clan.lastTimeOpenDoanhTrai, "HH:mm:ss") + " h??m nay. Ng?????i m???\n"
                                + "(" + player.clan.playerOpenDoanhTrai + "). H???n ng????i quay l???i v??o ng??y mai", "OK", "H?????ng\nd???n\nth??m");
                        return;
                    }
                    createOtherMenu(player, ConstNpc.MENU_JOIN_DOANH_TRAI,
                            "H??m nay bang h???i c???a ng????i ch??a v??o tr???i l???n n??o. Ng????i c?? mu???n v??o\n"
                            + "kh??ng?\n????? v??o, ta khuy??n ng????i n??n c?? 3-4 ng?????i c??ng bang ??i c??ng",
                            "V??o\n(mi???n ph??)", "Kh??ng", "H?????ng\nd???n\nth??m");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.MENU_JOIN_DOANH_TRAI:
                            if (select == 0) {
                                DoanhTraiService.gI().joinDoanhTrai(player);
                            } else if (select == 2) {
                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_DOANH_TRAI);
                            }
                            break;
                        case ConstNpc.IGNORE_MENU:
                            if (select == 1) {
                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_DOANH_TRAI);
                            }
                            break;
                    }
                }
            }
        };
    }

    public static Npc quaTrung(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            private final int COST_AP_TRUNG_NHANH = 1000000000;

            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    player.mabuEgg.sendMabuEgg();
                    if (player.mabuEgg.getSecondDone() != 0) {
                        this.createOtherMenu(player, ConstNpc.CAN_NOT_OPEN_EGG, "B?? b?? b??...",
                                "H???y b???\ntr???ng", "???p nhanh\n" + Util.numberToMoney(COST_AP_TRUNG_NHANH) + " v??ng", "????ng");
                    } else {
                        this.createOtherMenu(player, ConstNpc.CAN_OPEN_EGG, "B?? b?? b??...", "N???", "H???y b???\ntr???ng", "????ng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.CAN_NOT_OPEN_EGG:
                            if (select == 0) {
                                this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_EGG,
                                        "B???n c?? ch???c ch???n mu???n h???y b??? tr???ng Mab???", "?????ng ??", "T??? ch???i");
                            } else if (select == 1) {
                                if (player.inventory.gold >= COST_AP_TRUNG_NHANH) {
                                    player.inventory.gold -= COST_AP_TRUNG_NHANH;
                                    player.mabuEgg.timeDone = 0;
                                    Service.getInstance().sendMoney(player);
                                    player.mabuEgg.sendMabuEgg();
                                } else {
                                    Service.getInstance().sendThongBao(player,
                                            "B???n kh??ng ????? v??ng ????? th???c hi???n, c??n thi???u "
                                            + Util.numberToMoney((COST_AP_TRUNG_NHANH - player.inventory.gold)) + " v??ng");
                                }
                            }
                            break;
                        case ConstNpc.CAN_OPEN_EGG:
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, ConstNpc.CONFIRM_OPEN_EGG,
                                            "B???n c?? ch???c ch???n cho tr???ng n????\n"
                                            + "????? t??? c???a b???n s??? ???????c thay th??? b???ng ????? Mab??",
                                            "????? mab??\nTr??i ?????t", "????? mab??\nNam???c", "????? mab??\nXayda", "T??? ch???i");
                                    break;
                                case 1:
                                    this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_EGG,
                                            "B???n c?? ch???c ch???n mu???n h???y b??? tr???ng Mab???", "?????ng ??", "T??? ch???i");
                                    break;
                            }
                            break;
                        case ConstNpc.CONFIRM_OPEN_EGG:
                            switch (select) {
                                case 0:
                                    player.mabuEgg.openEgg(ConstPlayer.TRAI_DAT);
                                    break;
                                case 1:
                                    player.mabuEgg.openEgg(ConstPlayer.NAMEC);
                                    break;
                                case 2:
                                    player.mabuEgg.openEgg(ConstPlayer.XAYDA);
                                    break;
                                default:
                                    break;
                            }
                            break;
                        case ConstNpc.CONFIRM_DESTROY_EGG:
                            if (select == 0) {
                                player.mabuEgg.destroyEgg();
                            }
                            break;
                    }
                }
            }
        };
    }

    public static Npc quocVuong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            @Override
            public void openBaseMenu(Player player) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Con mu???n n??ng gi???i h???n s???c m???nh cho b???n th??n hay ????? t????",
                        "B???n th??n", "????? t???", "T??? ch???i");
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                if (player.nPoint.limitPower < NPoint.MAX_LIMIT) {
                                    this.createOtherMenu(player, ConstNpc.OPEN_POWER_MYSEFT,
                                            "Ta s??? tru???n n??ng l?????ng gi??p con m??? gi???i h???n s???c m???nh c???a b???n th??n l??n "
                                            + Util.numberToMoney(player.nPoint.getPowerNextLimit()),
                                            "N??ng\ngi???i h???n\ns???c m???nh",
                                            "N??ng ngay\n" + Util.numberToMoney(OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) + " v??ng", "????ng");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                            "S???c m???nh c???a con ???? ?????t t???i gi???i h???n",
                                            "????ng");
                                }
                                break;
                            case 1:
                                if (player.pet != null) {
                                    if (player.pet.nPoint.limitPower < NPoint.MAX_LIMIT) {
                                        this.createOtherMenu(player, ConstNpc.OPEN_POWER_PET,
                                                "Ta s??? tru???n n??ng l?????ng gi??p con m??? gi???i h???n s???c m???nh c???a ????? t??? l??n "
                                                + Util.numberToMoney(player.pet.nPoint.getPowerNextLimit()),
                                                "N??ng ngay\n" + Util.numberToMoney(OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) + " v??ng", "????ng");
                                    } else {
                                        this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                                "S???c m???nh c???a ????? con ???? ?????t t???i gi???i h???n",
                                                "????ng");
                                    }
                                } else {
                                    Service.getInstance().sendThongBao(player, "Kh??ng th??? th???c hi???n");
                                }
                                //gi???i h???n ????? t???
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.OPEN_POWER_MYSEFT) {
                        switch (select) {
                            case 0:
                                OpenPowerService.gI().openPowerBasic(player);
                                break;
                            case 1:
                                if (player.inventory.gold >= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) {
                                    if (OpenPowerService.gI().openPowerSpeed(player)) {
                                        player.inventory.gold -= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER;
                                        Service.getInstance().sendMoney(player);
                                    }
                                } else {
                                    Service.getInstance().sendThongBao(player,
                                            "B???n kh??ng ????? v??ng ????? m???, c??n thi???u "
                                            + Util.numberToMoney((OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER - player.inventory.gold)) + " v??ng");
                                }
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.OPEN_POWER_PET) {
                        if (select == 0) {
                            if (player.inventory.gold >= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) {
                                if (OpenPowerService.gI().openPowerSpeed(player.pet)) {
                                    player.inventory.gold -= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER;
                                    Service.getInstance().sendMoney(player);
                                }
                            } else {
                                Service.getInstance().sendThongBao(player,
                                        "B???n kh??ng ????? v??ng ????? m???, c??n thi???u "
                                        + Util.numberToMoney((OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER - player.inventory.gold)) + " v??ng");
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc bulmaTL(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "C???u b?? mu???n mua g?? n??o?", "C???a h??ng", "????ng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        if (select == 0) {
                            ShopServiceNew.gI().opendShop(player, "BUNMA_FUTURE", true);
                        }
                    }
                }
            }
        };
    }

    public static Npc rongOmega(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    BlackBallWar.gI().setTime();
                    if (this.mapId == 24 || this.mapId == 25 || this.mapId == 26) {
                        try {
                            long now = System.currentTimeMillis();
                            if (now > BlackBallWar.TIME_OPEN && now < BlackBallWar.TIME_CLOSE) {
                                this.createOtherMenu(player, ConstNpc.MENU_OPEN_BDW, "???????ng ?????n v???i ng???c r???ng sao ??en ???? m???, "
                                        + "ng????i c?? mu???n tham gia kh??ng?",
                                        "H?????ng d???n\nth??m", "Tham gia", "T??? ch???i");
                            } else {
                                String[] optionRewards = new String[7];
                                int index = 0;
                                for (int i = 0; i < 7; i++) {
                                    if (player.rewardBlackBall.timeOutOfDateReward[i] > System.currentTimeMillis()) {
                                        optionRewards[index] = "Nh???n th?????ng\n" + (i + 1) + " sao";
                                        index++;
                                    }
                                }
                                if (index != 0) {
                                    String[] options = new String[index + 1];
                                    for (int i = 0; i < index; i++) {
                                        options[i] = optionRewards[i];
                                    }
                                    options[options.length - 1] = "T??? ch???i";
                                    this.createOtherMenu(player, ConstNpc.MENU_REWARD_BDW, "Ng????i c?? m???t v??i ph???n th?????ng ng???c "
                                            + "r???ng sao ??en ????y!",
                                            options);
                                } else {
                                    this.createOtherMenu(player, ConstNpc.MENU_NOT_OPEN_BDW,
                                            "Ta c?? th??? gi??p g?? cho ng????i?", "H?????ng d???n", "T??? ch???i");
                                }
                            }
                        } catch (Exception ex) {
                            Logger.error("L???i m??? menu r???ng Omega");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.MENU_REWARD_BDW:
                            player.rewardBlackBall.getRewardSelect((byte) select);
                            break;
                        case ConstNpc.MENU_OPEN_BDW:
                            if (select == 0) {
                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_BLACK_BALL_WAR);
                            } else if (select == 1) {
                                player.iDMark.setTypeChangeMap(ConstMap.CHANGE_BLACK_BALL);
                                ChangeMapService.gI().openChangeMapTab(player);
                            }
                            break;
                        case ConstNpc.MENU_NOT_OPEN_BDW:
                            if (select == 0) {
                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_BLACK_BALL_WAR);
                            }
                            break;
                    }
                }
            }
        };
    }

    public static Npc rong1_to_7s(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isHoldBlackBall()) {
                        this.createOtherMenu(player, ConstNpc.MENU_PHU_HP, "Ta c?? th??? gi??p g?? cho ng????i?", "Ph?? h???", "T??? ch???i");
                    } else {
                        this.createOtherMenu(player, ConstNpc.MENU_OPTION_GO_HOME, "Ta c?? th??? gi??p g?? cho ng????i?", "V??? nh??", "T??? ch???i");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.getIndexMenu() == ConstNpc.MENU_PHU_HP) {
                        if (select == 0) {
                            this.createOtherMenu(player, ConstNpc.MENU_OPTION_PHU_HP,
                                    "Ta s??? gi??p ng????i t??ng HP l??n m???c kinh ho??ng, ng????i ch???n ??i",
                                    "x3 HP\n" + Util.numberToMoney(BlackBallWar.COST_X3) + " v??ng",
                                    "x5 HP\n" + Util.numberToMoney(BlackBallWar.COST_X5) + " v??ng",
                                    "x7 HP\n" + Util.numberToMoney(BlackBallWar.COST_X7) + " v??ng",
                                    "T??? ch???i"
                            );
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_GO_HOME) {
                        if (select == 0) {
                            ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_PHU_HP) {
                        if (player.effectSkin.xHPKI > 1) {
                            Service.getInstance().sendThongBao(player, "B???n ???? ???????c ph?? h??? r???i!");
                            return;
                        }
                        switch (select) {
                            case 0:
                                BlackBallWar.gI().xHPKI(player, BlackBallWar.X3);
                                break;
                            case 1:
                                BlackBallWar.gI().xHPKI(player, BlackBallWar.X5);
                                break;
                            case 2:
                                BlackBallWar.gI().xHPKI(player, BlackBallWar.X7);
                                break;
                            case 3:
                                this.npcChat(player, "????? ta xem ng????i tr??? ???????c bao l??u");
                                break;
                        }
                    }
                }
            }
        };
    }

    public static Npc npcThienSu64(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    createOtherMenu(player, ConstNpc.BASE_MENU, "Ng????i mu???n xem th??ng tin g???",
                            "Top\ns???c m???nh", "????ng");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        if (select == 0) {
                            TopService.gI().showTopPower(player);
                        }
                    }
                }
            }
        };
    }

    public static Npc bill(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 48) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ng????i mu???n g?? n??o?", "????ng");
                    } else {
                        super.openBaseMenu(player);
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (this.mapId) {
                        case 48:
                            switch (player.iDMark.getIndexMenu()) {
                                case ConstNpc.BASE_MENU:
                                    if (select == 0) {

                                    }
                                    break;
                            }
                            break;
                    }
                }
            }
        };
    }

    public static Npc boMong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 47) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Xin ch??o, c???u mu???n t??i gi??p g???", "Nhi???m v???\nh??ng ng??y", "T??? ch???i");
                        } else if (this.mapId == 84) {
                         this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Xin ch??o, c???u mu???n t??i gi??p g???", "Nhi???m v???\nh??ng ng??y", "T??? ch???i");
                    }
                    if (this.mapId == 47) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Xin ch??o, c???u mu???n t??i gi??p g???", "T??? ch???i");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 47) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    if (player.playerTask.sideTask.template != null) {
                                        String npcSay = "Nhi???m v??? hi???n t???i: " + player.playerTask.sideTask.getName() + " ("
                                                + player.playerTask.sideTask.getLevel() + ")"
                                                + "\nHi???n t???i ???? ho??n th??nh: " + player.playerTask.sideTask.count + "/"
                                                + player.playerTask.sideTask.maxCount + " ("
                                                + player.playerTask.sideTask.getPercentProcess() + "%)\nS??? nhi???m v??? c??n l???i trong ng??y: "
                                                + player.playerTask.sideTask.leftTask + "/" + ConstTask.MAX_SIDE_TASK;
                                        this.createOtherMenu(player, ConstNpc.MENU_OPTION_PAY_SIDE_TASK,
                                                npcSay, "Tr??? nhi???m\nv???", "H???y nhi???m\nv???");
                                    } else {
                                        this.createOtherMenu(player, ConstNpc.MENU_OPTION_LEVEL_SIDE_TASK,
                                                "T??i c?? v??i nhi???m v??? theo c???p b???c, "
                                                + "s???c c???u c?? th??? l??m ???????c c??i n??o?",
                                                "D???", "B??nh th?????ng", "Kh??", "Si??u kh??", "?????a ng???c", "T??? ch???i");
                                    }
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_LEVEL_SIDE_TASK) {
                            switch (select) {
                                case 0:
                                case 1:
                                case 2:
                                case 3:
                                case 4:
                                    TaskService.gI().changeSideTask(player, (byte) select);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_PAY_SIDE_TASK) {
                            switch (select) {
                                case 0:
                                    TaskService.gI().paySideTask(player);
                                    break;
                                case 1:
                                    TaskService.gI().removeSideTask(player);
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc gokuSSJ_1(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 80) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Xin ch??o, t??i c?? th??? gi??p g?? cho c???u?",
                                "T???i h??nh tinh\nTh???c v???t", "T???i h??nh tinh\nYardart", "T??? ch???i");
                    } else if (this.mapId == 131) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Xin ch??o, t??i c?? th??? gi??p g?? cho c???u?",
                                "Quay v???", "T??? ch???i");
                    } else {
                        super.openBaseMenu(player);
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.BASE_MENU:
                            if (this.mapId == 80) {
                                if (select == 0) {
                                    if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_24_0) {
                                        ChangeMapService.gI().changeMapBySpaceShip(player, 160, -1, 168);
                                    } else {
                                        this.npcChat(player, "Xin l???i, t??i ch??a th??? ????a c???u t???i n??i ???? l??c n??y...");
                                    }
                                } else if (select == 1) {
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 131, -1, 940);
                                }
                            } else if (this.mapId == 131) {
                                if (select == 0) {
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 80, -1, 870);
                                }
                            }
                            break;
                    }
                }
            }
        };
    }

    public static Npc gokuSSJ_2(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 131) {
                        try {
                            Item biKiep = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 590);
                            int soLuong = 0;
                            if (biKiep != null) {
                                soLuong = biKiep.quantity;
                            }
                            if (soLuong >= 10000) {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU, "B???n ??ang c?? " + soLuong + " b?? ki???p.\n"
                                        + "H??y ki???m ????? 10000 b?? ki???p t??i s??? d???y b???n c??ch d???ch chuy???n t???c th???i c???a ng?????i Yardart", "H???c d???ch\nchuy???n", "????ng");
                            } else {
                                this.createOtherMenu(player, ConstNpc.BASE_MENU, "B???n ??ang c?? " + soLuong + " b?? ki???p.\n"
                                        + "H??y ki???m ????? 10000 b?? ki???p t??i s??? d???y b???n c??ch d???ch chuy???n t???c th???i c???a ng?????i Yardart", "????ng");
                            }
                        } catch (Exception ex) {

                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 133) {
                        try {
                            Item biKiep = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 590);
                            int soLuong = 0;
                            if (biKiep != null) {
                                soLuong = biKiep.quantity;
                            }
                            if (soLuong >= 10000 && InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                                Item yardart = ItemService.gI().createNewItem((short) (player.gender + 592));
                                yardart.itemOptions.add(new Item.ItemOption(47, 400));
                                yardart.itemOptions.add(new Item.ItemOption(108, 10));
                                InventoryServiceNew.gI().addItemBag(player, yardart);
                                InventoryServiceNew.gI().subQuantityItemsBag(player, biKiep, 10000);
                                InventoryServiceNew.gI().sendItemBags(player);
                                Service.getInstance().sendThongBao(player, "B???n v???a nh???n ???????c trang ph???c t???c Yardart");
                            }
                        } catch (Exception ex) {

                        }
                    }
                }
            }
        };
    }

    public static Npc createNPC(int mapId, int status, int cx, int cy, int tempId) {
        int avatar = Manager.NPC_TEMPLATES.get(tempId).avatar;
        try {
            switch (tempId) {
                case ConstNpc.QUY_LAO_KAME:
                    return quyLaoKame(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.TRUONG_LAO_GURU:
                    return truongLaoGuru(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.VUA_VEGETA:
                    return vuaVegeta(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.ONG_GOHAN:
                case ConstNpc.ONG_MOORI:
                case ConstNpc.ONG_PARAGUS:
                    return ongGohan_ongMoori_ongParagus(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BUNMA:
                    return bulmaQK(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DENDE:
                    return dende(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.APPULE:
                    return appule(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DR_DRIEF:
                    return drDrief(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CARGO:
                    return cargo(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CUI:
                    return cui(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.SANTA:
                    return santa(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.URON:
                    return uron(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BA_HAT_MIT:
                    return baHatMit(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RUONG_DO:
                    return ruongDo(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DAU_THAN:
                    return dauThan(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CALICK:
                    return calick(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.JACO:
                    return jaco(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.THUONG_DE:
                    return thuongDe(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.THAN_VU_TRU:
                    return thanVuTru(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.KIBIT:
                    return kibit(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.OSIN:
                    return osin(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.LINH_CANH:
                    return linhCanh(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.QUA_TRUNG:
                    return quaTrung(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.QUOC_VUONG:
                    return quocVuong(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BUNMA_TL:
                    return bulmaTL(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_OMEGA:
                    return rongOmega(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_1S:
                case ConstNpc.RONG_2S:
                case ConstNpc.RONG_3S:
                case ConstNpc.RONG_4S:
                case ConstNpc.RONG_5S:
                case ConstNpc.RONG_6S:
                case ConstNpc.RONG_7S:
                    return rong1_to_7s(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.NPC_64:
                    return npcThienSu64(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BILL:
                    return bill(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BO_MONG:
                    return boMong(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.GOKU_SSJ:
                    return gokuSSJ_1(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.GOKU_SSJ_:
                    return gokuSSJ_2(mapId, status, cx, cy, tempId, avatar);
                default:
                    return new Npc(mapId, status, cx, cy, tempId, avatar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                super.openBaseMenu(player);
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
//                                ShopService.gI().openShopNormal(player, this, ConstNpc.SHOP_BUNMA_TL_0, 0, player.gender);
                            }
                        }
                    };
            }
        } catch (Exception e) {
            Logger.logException(NpcFactory.class, e, "L???i load npc");
            return null;
        }
    }

    //girlkun75-mark
    public static void createNpcRongThieng() {
        Npc npc = new Npc(-1, -1, -1, -1, ConstNpc.RONG_THIENG, -1) {
            @Override
            public void confirmMenu(Player player, int select) {
                switch (player.iDMark.getIndexMenu()) {
                    case ConstNpc.IGNORE_MENU:

                        break;
                    case ConstNpc.SHENRON_CONFIRM:
                        if (select == 0) {
                            SummonDragon.gI().confirmWish();
                        } else if (select == 1) {
                            SummonDragon.gI().reOpenShenronWishes(player);
                        }
                        break;
                    case ConstNpc.SHENRON_1_1:
                        if (player.iDMark.getIndexMenu() == ConstNpc.SHENRON_1_1 && select == SHENRON_1_STAR_WISHES_1.length - 1) {
                            NpcService.gI().createMenuRongThieng(player, ConstNpc.SHENRON_1_2, SHENRON_SAY, SHENRON_1_STAR_WISHES_2);
                            break;
                        }
                    case ConstNpc.SHENRON_1_2:
                        if (player.iDMark.getIndexMenu() == ConstNpc.SHENRON_1_2 && select == SHENRON_1_STAR_WISHES_2.length - 1) {
                            NpcService.gI().createMenuRongThieng(player, ConstNpc.SHENRON_1_1, SHENRON_SAY, SHENRON_1_STAR_WISHES_1);
                            break;
                        }
                    default:
                        SummonDragon.gI().showConfirmShenron(player, player.iDMark.getIndexMenu(), (byte) select);
                        break;
                }
            }
        };
    }

    public static void createNpcConMeo() {
        Npc npc = new Npc(-1, -1, -1, -1, ConstNpc.CON_MEO, 351) {
            @Override
            public void confirmMenu(Player player, int select) {
                switch (player.iDMark.getIndexMenu()) {
                    case ConstNpc.IGNORE_MENU:

                        break;
                    case ConstNpc.MAKE_MATCH_PVP:
                        PVPService.gI().sendInvitePVP(player, (byte) select);
                        break;
                    case ConstNpc.MAKE_FRIEND:
                        if (select == 0) {
                            Object playerId = PLAYERID_OBJECT.get(player.id);
                            if (playerId != null) {
                                FriendAndEnemyService.gI().acceptMakeFriend(player,
                                        Integer.parseInt(String.valueOf(playerId)));
                            }
                        }
                        break;
                    case ConstNpc.REVENGE:
                        if (select == 0) {
                            PVPService.gI().acceptRevenge(player);
                        }
                        break;
                    case ConstNpc.TUTORIAL_SUMMON_DRAGON:
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, -1, SummonDragon.SUMMON_SHENRON_TUTORIAL);
                        }
                        break;
                    case ConstNpc.SUMMON_SHENRON:
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, -1, SummonDragon.SUMMON_SHENRON_TUTORIAL);
                        } else if (select == 1) {
                            SummonDragon.gI().summonShenron(player);
                        }
                        break;

                    case ConstNpc.INTRINSIC:
                        if (select == 0) {
                            IntrinsicService.gI().showAllIntrinsic(player);
                        } else if (select == 1) {
                            IntrinsicService.gI().showConfirmOpen(player);
                        } else if (select == 2) {
                            IntrinsicService.gI().showConfirmOpenVip(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_OPEN_INTRINSIC:
                        if (select == 0) {
                            IntrinsicService.gI().open(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_OPEN_INTRINSIC_VIP:
                        if (select == 0) {
                            IntrinsicService.gI().openVip(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_LEAVE_CLAN:
                        if (select == 0) {
                            ClanService.gI().leaveClan(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_NHUONG_PC:
                        if (select == 0) {
                            ClanService.gI().phongPc(player, (int) PLAYERID_OBJECT.get(player.id));
                        }
                        break;
                    case ConstNpc.BAN_PLAYER:
                        if (select == 0) {
                            PlayerService.gI().banPlayer((Player) PLAYERID_OBJECT.get(player.id));
                            Service.getInstance().sendThongBao(player, "Ban ng?????i ch??i " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " th??nh c??ng");
                        }
                        break;
                    case ConstNpc.BUFF_PET:
                        if (select == 0) {
                            Player pl = (Player) PLAYERID_OBJECT.get(player.id);
                            if (pl.pet == null) {
                                PetService.gI().createNormalPet(pl);
                                Service.getInstance().sendThongBao(player, "Ph??t ????? t??? cho " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " th??nh c??ng");
                            }
                        }
                        break;
                    case ConstNpc.MENU_ADMIN:
                        switch (select) {
                            case 0:
                                for (int i = 14; i <= 20; i++) {
                                    Item item = ItemService.gI().createNewItem((short) i);
                                    InventoryServiceNew.gI().addItemBag(player, item);
                                }
                                InventoryServiceNew.gI().sendItemBags(player);
                                break;
                            case 1:
                                if (player.pet == null) {
                                    PetService.gI().createNormalPet(player);
                                } else {
                                    if (player.pet.typePet == 1) {
                                        PetService.gI().changeNormalPet(player);
                                    } else {
                                        PetService.gI().changeMabuPet(player);
                                    }
                                }
                                break;
                            case 2:
//                                PlayerService.gI().baoTri();
                                Maintenance.gI().start(15);
                                break;
                            case 3:
                                Input.gI().createFormFindPlayer(player);
                                break;
                        }
                        break;

                    case ConstNpc.CONFIRM_DISSOLUTION_CLAN:
                        switch (select) {
                            case 0:
                                Clan clan = player.clan;
                                clan.delete(clan.id);
                                Manager.CLANS.remove(clan);
                                player.clan = null;
                                player.clanMember = null;
                                ClanService.gI().sendMyClan(player);
                                ClanService.gI().sendClanId(player);
                                Service.getInstance().sendThongBao(player, "???? gi???i t??n bang h???i.");
                                break;
                        }
                        break;
                    case ConstNpc.CONFIRM_REMOVE_ALL_ITEM_LUCKY_ROUND:
                        if (select == 0) {
                            for (int i = 0; i < player.inventory.itemsBoxCrackBall.size(); i++) {
                                player.inventory.itemsBoxCrackBall.set(i, ItemService.gI().createItemNull());
                            }
                            Service.getInstance().sendThongBao(player, "???? x??a h???t v???t ph???m trong r????ng");
                        }
                        break;
                    case ConstNpc.MENU_FIND_PLAYER:
                        Player p = (Player) PLAYERID_OBJECT.get(player.id);
                        if (p != null) {
                            switch (select) {
                                case 0:
                                    if (p.zone != null) {
                                        ChangeMapService.gI().changeMapYardrat(player, p.zone, p.location.x, p.location.y);
                                    }
                                    break;
                                case 1:
                                    if (p.zone != null) {
                                        ChangeMapService.gI().changeMap(p, player.zone, player.location.x, player.location.y);
                                    }
                                    break;
                                case 2:
                                    if (p != null) {
                                        Input.gI().createFormChangeName(player, p);
                                    }
                                    break;
                                case 3:
                                    if (p != null) {
                                        String[] selects = new String[]{"?????ng ??", "H???y"};
                                        NpcService.gI().createMenuConMeo(player, ConstNpc.BAN_PLAYER, -1,
                                                "B???n c?? ch???c ch???n mu???n ban " + p.name, selects, p);
                                    }
                                    break;
                            }
                        }
                        break;
                }
            }
        };
    }

}
