package com.girlkun.services.func;

import com.girlkun.consts.ConstNpc;
import com.girlkun.models.item.Item;
import com.girlkun.models.npc.Npc;
import com.girlkun.models.npc.NpcManager;
import com.girlkun.models.player.Player;
import com.girlkun.server.ServerNotify;
import com.girlkun.network.io.Message;
import com.girlkun.services.ItemService;
import com.girlkun.services.RewardService;
import com.girlkun.services.Service;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.utils.Util;

/**
 *
 * @author ๐ Trแบงn Lแบกi ๐
 * @copyright ๐ GirlkuN ๐
 *
 */
public class CombineServiceNew {

    private static final int COST_DOI_VE_DOI_DO_HUY_DIET = 500000000;
    private static final int COST_DAP_DO_KICH_HOAT = 500000000;
    private static final int COST_DOI_MANH_KICH_HOAT = 500000000;

    private static final int TIME_COMBINE = 1500;

    private static final byte MAX_STAR_ITEM = 20;
    private static final byte MAX_LEVEL_ITEM = 20;

    private static final byte OPEN_TAB_COMBINE = 0;
    private static final byte REOPEN_TAB_COMBINE = 1;
    private static final byte COMBINE_SUCCESS = 2;
    private static final byte COMBINE_FAIL = 3;
    private static final byte COMBINE_CHANGE_OPTION = 4;
    private static final byte COMBINE_DRAGON_BALL = 5;
    public static final byte OPEN_ITEM = 6;

    public static final int EP_SAO_TRANG_BI = 500;
    public static final int PHA_LE_HOA_TRANG_BI = 501;
    public static final int CHUYEN_HOA_TRANG_BI = 502;
    public static final int DOI_VE_HUY_DIET = 503;
    public static final int DAP_SET_KICH_HOAT = 504;
    public static final int DOI_MANH_KICH_HOAT = 505;
    public static final int DOI_CHUOI_KIEM = 506;
    public static final int DOI_LUOI_KIEM = 507;
    public static final int DOI_KIEM_THAN = 508;

    public static final int NANG_CAP_VAT_PHAM = 510;
    public static final int NANG_CAP_BONG_TAI = 511;
    public static final int LAM_PHEP_NHAP_DA = 512;
    public static final int NHAP_NGOC_RONG = 513;

    private final Npc baHatMit;

    private static CombineServiceNew i;

    public CombineServiceNew() {
        this.baHatMit = NpcManager.getNpc(ConstNpc.BA_HAT_MIT);
    }

    public static CombineServiceNew gI() {
        if (i == null) {
            i = new CombineServiceNew();
        }
        return i;
    }

    /**
     * Mแป tab ฤแบญp ฤแป
     *
     * @param player
     * @param type kiแปu ฤแบญp ฤแป
     */
    public void openTabCombine(Player player, int type) {
        player.combineNew.setTypeCombine(type);
        Message msg;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(OPEN_TAB_COMBINE);
            msg.writer().writeUTF(getTextInfoTabCombine(type));
            msg.writer().writeUTF(getTextTopTabCombine(type));
            if (player.iDMark.getNpcChose() != null) {
                msg.writer().writeShort(player.iDMark.getNpcChose().tempId);
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    /**
     * Hiแปn thแป thรดng tin ฤแบญp ฤแป
     *
     * @param player
     */
    public void showInfoCombine(Player player, int[] index) {
        player.combineNew.clearItemCombine();
        if (index.length > 0) {
            for (int i = 0; i < index.length; i++) {
                player.combineNew.itemsCombine.add(player.inventory.itemsBag.get(index[i]));
            }
        }
        switch (player.combineNew.typeCombine) {
            case EP_SAO_TRANG_BI:
                if (player.combineNew.itemsCombine.size() == 2) {
                    Item trangBi = null;
                    Item daPhaLe = null;
                    for (Item item : player.combineNew.itemsCombine) {
                        if (isTrangBiPhaLeHoa(item)) {
                            trangBi = item;
                        } else if (isDaPhaLe(item)) {
                            daPhaLe = item;
                        }
                    }
                    int star = 0; //sao pha lรช ฤรฃ รฉp
                    int starEmpty = 0; //lแป sao pha lรช
                    if (trangBi != null && daPhaLe != null) {
                        for (Item.ItemOption io : trangBi.itemOptions) {
                            if (io.optionTemplate.id == 102) {
                                star = io.param;
                            } else if (io.optionTemplate.id == 107) {
                                starEmpty = io.param;
                            }
                        }
                        if (star < starEmpty) {
                            player.combineNew.gemCombine = getGemEpSao(star);
                            String npcSay = trangBi.template.name + "\n|2|";
                            for (Item.ItemOption io : trangBi.itemOptions) {
                                if (io.optionTemplate.id != 102) {
                                    npcSay += io.getOptionString() + "\n";
                                }
                            }
                            if (daPhaLe.template.type == 30) {
                                for (Item.ItemOption io : daPhaLe.itemOptions) {
                                    npcSay += "|20|" + io.getOptionString() + "\n";
                                }
                            } else {
                                npcSay += "|20|" + ItemService.gI().getItemOptionTemplate(getOptionDaPhaLe(daPhaLe)).name.replaceAll("#", getParamDaPhaLe(daPhaLe) + "") + "\n";
                            }
                            npcSay += "|1|Cแบงn " + Util.numberToMoney(player.combineNew.gemCombine) + " ngแปc";
                            baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                    "Nรขng cแบฅp\ncแบงn " + player.combineNew.gemCombine + " ngแปc");

                        } else {
                            this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                    "Cแบงn 1 trang bแป cรณ lแป sao pha lรช vร? 1 loแบกi ฤรก pha lรช ฤแป รฉp vร?o", "ฤรณng");
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Cแบงn 1 trang bแป cรณ lแป sao pha lรช vร? 1 loแบกi ฤรก pha lรช ฤแป รฉp vร?o", "ฤรณng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                            "Cแบงn 1 trang bแป cรณ lแป sao pha lรช vร? 1 loแบกi ฤรก pha lรช ฤแป รฉp vร?o", "ฤรณng");
                }
                break;
            case PHA_LE_HOA_TRANG_BI:
                if (player.combineNew.itemsCombine.size() == 1) {
                    Item item = player.combineNew.itemsCombine.get(0);
                    if (isTrangBiPhaLeHoa(item)) {
                        int star = 0;
                        for (Item.ItemOption io : item.itemOptions) {
                            if (io.optionTemplate.id == 107) {
                                star = io.param;
                                break;
                            }
                        }
                        if (star < MAX_STAR_ITEM) {
                            player.combineNew.goldCombine = getGoldPhaLeHoa(star);
                            player.combineNew.gemCombine = getGemPhaLeHoa(star);
                            player.combineNew.ratioCombine = getRatioPhaLeHoa(star) * 50;

                            String npcSay = item.template.name + "\n|2|";
                            for (Item.ItemOption io : item.itemOptions) {
                                if (io.optionTemplate.id != 102) {
                                    npcSay += io.getOptionString() + "\n";
                                }
                            }
                            npcSay += "|20|Tแป lแป thร?nh cรดng: " + player.combineNew.ratioCombine + "%" + "\n";
                            if (player.combineNew.goldCombine <= player.inventory.gold) {
                                npcSay += "|1|Cแบงn " + Util.numberToMoney(player.combineNew.goldCombine) + " vร?ng";
                                baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay,
                                        "Nรขng cแบฅp\ncแบงn " + player.combineNew.gemCombine + " ngแปc");
                            } else {
                                npcSay += "Cรฒn thiแบฟu " + Util.numberToMoney(player.combineNew.goldCombine - player.inventory.gold) + " vร?ng";
                                baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, npcSay, "ฤรณng");
                            }

                        } else {
                            this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Vแบญt phแบฉm ฤรฃ ฤแบกt tแปi ฤa sao pha lรช", "ฤรณng");
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Vแบญt phแบฉm nร?y khรดng thแป ฤแปฅc lแป", "ฤรณng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hรฃy hรฃy chแปn 1 vแบญt phแบฉm ฤแป pha lรช hรณa", "ฤรณng");
                }
                break;
            case NHAP_NGOC_RONG:
                if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                    if (player.combineNew.itemsCombine.size() == 1) {
                        Item item = player.combineNew.itemsCombine.get(0);
                        if (item != null && item.isNotNullItem() && (item.template.id > 14 && item.template.id <= 20) && item.quantity >= 7) {
                            String npcSay = "|2|Con cรณ muแปn biแบฟn 7 " + item.template.name + " thร?nh\n"
                                    + "1 viรชn " + ItemService.gI().getTemplate((short) (item.template.id - 1)).name + "\n"
                                    + "|7|Cแบงn 7 " + item.template.name;
                            this.baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE, npcSay, "Lร?m phรฉp", "Tแปซ chแปi");
                        } else {
                            this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Cแบงn 7 viรชn ngแปc rแปng 2 sao trแป lรชn", "ฤรณng");
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Cแบงn 7 viรชn ngแปc rแปng 2 sao trแป lรชn", "ฤรณng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hร?nh trang cแบงn รญt nhแบฅt 1 chแป trแปng", "ฤรณng");
                }
                break;
            case NANG_CAP_VAT_PHAM:
                if (player.combineNew.itemsCombine.size() == 2) {
                    if (isCoupleItemNangCap(player.combineNew.itemsCombine.get(0), player.combineNew.itemsCombine.get(1))) {
                        Item trangBi = null;
                        Item daNangCap = null;
                        if (player.combineNew.itemsCombine.get(0).template.type < 5) {
                            trangBi = player.combineNew.itemsCombine.get(0);
                            daNangCap = player.combineNew.itemsCombine.get(1);
                        } else {
                            trangBi = player.combineNew.itemsCombine.get(1);
                            daNangCap = player.combineNew.itemsCombine.get(0);
                        }

                        int level = 0;
                        for (Item.ItemOption io : trangBi.itemOptions) {
                            if (io.optionTemplate.id == 72) {
                                level = io.param;
                                break;
                            }
                        }
                        if (level < MAX_LEVEL_ITEM) {
                            player.combineNew.goldCombine = getGoldNangCapDo(level);
                            player.combineNew.ratioCombine = getTileNangCapDo(level);
                            player.combineNew.countDaNangCap = getCountDaNangCapDo(level);

                            String npcSay = "|2|Hiแปn tแบกi " + trangBi.template.name + " (+" + level + ")\n|0|";
                            for (Item.ItemOption io : trangBi.itemOptions) {
                                if (io.optionTemplate.id != 72) {
                                    npcSay += io.getOptionString() + "\n";
                                }
                            }
                            String option = null;
                            int param = 0;
                            for (Item.ItemOption io : trangBi.itemOptions) {
                                if (io.optionTemplate.id == 47
                                        || io.optionTemplate.id == 6
                                        || io.optionTemplate.id == 0
                                        || io.optionTemplate.id == 7
                                        || io.optionTemplate.id == 14
                                        || io.optionTemplate.id == 22
                                        || io.optionTemplate.id == 23) {
                                    option = io.optionTemplate.name;
                                    param = io.param + (io.param * 10 / 100);
                                    break;
                                }
                            }
                            npcSay += "|2|Sau khi nรขng cแบฅp (+" + (level + 1) + ")\n|7|"
                                    + option.replaceAll("#", String.valueOf(param))
                                    + "\n|7|Tแป lแป thร?nh cรดng: " + player.combineNew.ratioCombine + "%\n"
                                    + (player.combineNew.countDaNangCap > daNangCap.quantity ? "|7|" : "|1|")
                                    + "Cแบงn " + player.combineNew.countDaNangCap + " " + daNangCap.template.name
                                    + "\n"
                                    + (player.combineNew.goldCombine > player.inventory.gold ? "|7|" : "|1|")
                                    + "Cแบงn " + Util.numberToMoney(player.combineNew.goldCombine) + " vร?ng";
                            if (level == 2 || level == 4 || level == 6) {
                                npcSay += "\nNแบฟu thแบฅt bแบกi sแบฝ rแปt xuแปng (+" + (level - 1) + ")";
                            }
                            if (player.combineNew.countDaNangCap > daNangCap.quantity) {
                                this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        npcSay, "Cรฒn thiแบฟu\n" + (player.combineNew.countDaNangCap - daNangCap.quantity) + " " + daNangCap.template.name);
                            } else if (player.combineNew.goldCombine > player.inventory.gold) {
                                this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                        npcSay, "Cรฒn thiแบฟu\n" + Util.numberToMoney((player.combineNew.goldCombine - player.inventory.gold)) + " vร?ng");
                            } else {
                                this.baHatMit.createOtherMenu(player, ConstNpc.MENU_START_COMBINE,
                                        npcSay, "Nรขng cแบฅp\n" + Util.numberToMoney(player.combineNew.goldCombine) + " vร?ng", "Tแปซ chแปi");
                            }
                        } else {
                            this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Trang bแป cแปงa ngฦฐฦกi ฤรฃ ฤแบกt cแบฅp tแปi ฤa", "ฤรณng");
                        }
                    } else {
                        this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hรฃy chแปn 1 trang bแป vร? 1 loแบกi ฤรก nรขng cแบฅp", "ฤรณng");
                    }
                } else {
                    this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hรฃy chแปn 1 trang bแป vร? 1 loแบกi ฤรก nรขng cแบฅp", "ฤรณng");
                }
                break;
        }
    }

    /**
     * Bแบฏt ฤแบงu ฤแบญp ฤแป - ฤiแปu hฦฐแปng tแปซng loแบกi ฤแบญp ฤแป
     *
     * @param player
     */
    public void startCombine(Player player) {
        if (false) {
            Service.getInstance().sendThongBao(player, "Tรญnh nฤng ฤang tแบกm khรณa");
            return;
        }
        switch (player.combineNew.typeCombine) {
            case EP_SAO_TRANG_BI:
                epSaoTrangBi(player);
                break;
            case PHA_LE_HOA_TRANG_BI:
                phaLeHoaTrangBi(player);
                break;
            case CHUYEN_HOA_TRANG_BI:

                break;
            case NHAP_NGOC_RONG:
                nhapNgocRong(player);
                break;
            case NANG_CAP_VAT_PHAM:
                nangCapVatPham(player);
                break;
        }
        player.iDMark.setIndexMenu(ConstNpc.IGNORE_MENU);
        player.combineNew.clearParamCombine();
        player.combineNew.lastTimeCombine = System.currentTimeMillis();

    }

    private void doiKiemThan(Player player) {
        if (player.combineNew.itemsCombine.size() == 3) {
            Item keo = null, luoiKiem = null, chuoiKiem = null;
            for (Item it : player.combineNew.itemsCombine) {
                if (it.template.id == 2015) {
                    keo = it;
                } else if (it.template.id == 2016) {
                    chuoiKiem = it;
                } else if (it.template.id == 2017) {
                    luoiKiem = it;
                }
            }
            if (keo != null && keo.quantity >= 99 && luoiKiem != null && chuoiKiem != null) {
                if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                    sendEffectSuccessCombine(player);
                    Item item = ItemService.gI().createNewItem((short) 2018);
                    item.itemOptions.add(new Item.ItemOption(50, Util.nextInt(9, 15)));
                    item.itemOptions.add(new Item.ItemOption(77, Util.nextInt(8, 15)));
                    item.itemOptions.add(new Item.ItemOption(103, Util.nextInt(8, 15)));
                    if (Util.isTrue(80, 100)) {
                        item.itemOptions.add(new Item.ItemOption(93, Util.nextInt(1, 15)));
                    }
                    InventoryServiceNew.gI().addItemBag(player, item);

                    InventoryServiceNew.gI().subQuantityItemsBag(player, keo, 99);
                    InventoryServiceNew.gI().subQuantityItemsBag(player, luoiKiem, 1);
                    InventoryServiceNew.gI().subQuantityItemsBag(player, chuoiKiem, 1);

                    InventoryServiceNew.gI().sendItemBags(player);
                    Service.getInstance().sendMoney(player);
                    reOpenItemCombine(player);
                }
            }
        }
    }

    private void doiChuoiKiem(Player player) {
        if (player.combineNew.itemsCombine.size() == 1) {
            Item manhNhua = player.combineNew.itemsCombine.get(0);
            if (manhNhua.template.id == 2014 && manhNhua.quantity >= 99) {
                if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                    sendEffectSuccessCombine(player);
                    Item item = ItemService.gI().createNewItem((short) 2016);
                    InventoryServiceNew.gI().addItemBag(player, item);

                    InventoryServiceNew.gI().subQuantityItemsBag(player, manhNhua, 99);

                    InventoryServiceNew.gI().sendItemBags(player);
                    Service.getInstance().sendMoney(player);
                    reOpenItemCombine(player);
                }
            }
        }
    }

    private void doiLuoiKiem(Player player) {
        if (player.combineNew.itemsCombine.size() == 1) {
            Item manhSat = player.combineNew.itemsCombine.get(0);
            if (manhSat.template.id == 2013 && manhSat.quantity >= 99) {
                if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                    sendEffectSuccessCombine(player);
                    Item item = ItemService.gI().createNewItem((short) 2017);
                    InventoryServiceNew.gI().addItemBag(player, item);
                    InventoryServiceNew.gI().subQuantityItemsBag(player, manhSat, 99);

                    InventoryServiceNew.gI().sendItemBags(player);
                    Service.getInstance().sendMoney(player);
                    reOpenItemCombine(player);
                }
            }
        }
    }

    private void doiManhKichHoat(Player player) {
        if (player.combineNew.itemsCombine.size() == 2 || player.combineNew.itemsCombine.size() == 3) {
            Item nr1s = null, doThan = null, buaBaoVe = null;
            for (Item it : player.combineNew.itemsCombine) {
                if (it.template.id == 14) {
                    nr1s = it;
                } else if (it.template.id == 2010) {
                    buaBaoVe = it;
                } else if (it.template.id >= 555 && it.template.id <= 567) {
                    doThan = it;
                }
            }

            if (nr1s != null && doThan != null) {
                if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0
                        && player.inventory.gold >= COST_DOI_MANH_KICH_HOAT) {
                    player.inventory.gold -= COST_DOI_MANH_KICH_HOAT;
                    int tiLe = buaBaoVe != null ? 100 : 50;
                    if (Util.isTrue(tiLe, 100)) {
                        sendEffectSuccessCombine(player);
                        Item item = ItemService.gI().createNewItem((short) 2009);
                        item.itemOptions.add(new Item.ItemOption(30, 0));
                        InventoryServiceNew.gI().addItemBag(player, item);
                    } else {
                        sendEffectFailCombine(player);
                    }
                    InventoryServiceNew.gI().subQuantityItemsBag(player, nr1s, 1);
                    InventoryServiceNew.gI().subQuantityItemsBag(player, doThan, 1);
                    if (buaBaoVe != null) {
                        InventoryServiceNew.gI().subQuantityItemsBag(player, buaBaoVe, 1);
                    }
                    InventoryServiceNew.gI().sendItemBags(player);
                    Service.getInstance().sendMoney(player);
                    reOpenItemCombine(player);
                }
            } else {
                this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hรฃy chแปn 1 trang bแป thแบงn linh vร? 1 viรชn ngแปc rแปng 1 sao", "ฤรณng");
            }
        }
    }

    private void dapDoKichHoat(Player player) {
        if (player.combineNew.itemsCombine.size() == 1 || player.combineNew.itemsCombine.size() == 2) {
            Item dhd = null, dtl = null;
            for (Item item : player.combineNew.itemsCombine) {
                if (item.isNotNullItem()) {
                    if (item.template.id >= 650 && item.template.id <= 662) {
                        dhd = item;
                    } else if (item.template.id >= 555 && item.template.id <= 567) {
                        dtl = item;
                    }
                }
            }
            if (dhd != null) {
                if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0
                        && player.inventory.gold >= COST_DAP_DO_KICH_HOAT) {
                    player.inventory.gold -= COST_DAP_DO_KICH_HOAT;
                    int tiLe = dtl != null ? 100 : 50;
                    if (Util.isTrue(tiLe, 100)) {
                        sendEffectSuccessCombine(player);
                        Item item = ItemService.gI().createNewItem((short) getTempIdItemC0(dhd.template.gender, dhd.template.type));
                        RewardService.gI().initBaseOptionClothes(item.template.id, item.template.type, item.itemOptions);
                        RewardService.gI().initActivationOption(item.template.gender < 3 ? item.template.gender : player.gender, item.template.type, item.itemOptions);
                        InventoryServiceNew.gI().addItemBag(player, item);
                    } else {
                        sendEffectFailCombine(player);
                    }
                    InventoryServiceNew.gI().subQuantityItemsBag(player, dhd, 1);
                    if (dtl != null) {
                        InventoryServiceNew.gI().subQuantityItemsBag(player, dtl, 1);
                    }
                    InventoryServiceNew.gI().sendItemBags(player);
                    Service.getInstance().sendMoney(player);
                    reOpenItemCombine(player);
                }
            }
        }
    }

    private void doiVeHuyDiet(Player player) {
        if (player.combineNew.itemsCombine.size() == 1) {
            Item item = player.combineNew.itemsCombine.get(0);
            if (item.isNotNullItem() && item.template.id >= 555 && item.template.id <= 567) {
                if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0
                        && player.inventory.gold >= COST_DOI_VE_DOI_DO_HUY_DIET) {
                    player.inventory.gold -= COST_DOI_VE_DOI_DO_HUY_DIET;
                    Item ticket = ItemService.gI().createNewItem((short) (2001 + item.template.type));
                    ticket.itemOptions.add(new Item.ItemOption(30, 0));
                    InventoryServiceNew.gI().subQuantityItemsBag(player, item, 1);
                    InventoryServiceNew.gI().addItemBag(player, ticket);
                    sendEffectOpenItem(player, item.template.iconID, ticket.template.iconID);

                    InventoryServiceNew.gI().sendItemBags(player);
                    Service.getInstance().sendMoney(player);
                    reOpenItemCombine(player);
                }
            }
        }
    }

    private void epSaoTrangBi(Player player) {
        if (player.combineNew.itemsCombine.size() == 2) {
            int gem = player.combineNew.gemCombine;
            if (player.inventory.gem < gem) {
                Service.getInstance().sendThongBao(player, "Khรดng ฤแปง ngแปc ฤแป thแปฑc hiแปn");
                return;
            }
            Item trangBi = null;
            Item daPhaLe = null;
            for (Item item : player.combineNew.itemsCombine) {
                if (isTrangBiPhaLeHoa(item)) {
                    trangBi = item;
                } else if (isDaPhaLe(item)) {
                    daPhaLe = item;
                }
            }
            int star = 0; //sao pha lรช ฤรฃ รฉp
            int starEmpty = 0; //lแป sao pha lรช
            if (trangBi != null && daPhaLe != null) {
                Item.ItemOption optionStar = null;
                for (Item.ItemOption io : trangBi.itemOptions) {
                    if (io.optionTemplate.id == 102) {
                        star = io.param;
                        optionStar = io;
                    } else if (io.optionTemplate.id == 107) {
                        starEmpty = io.param;
                    }
                }
                if (star < starEmpty) {
                    player.inventory.subGemAndRuby(gem);
                    int optionId = getOptionDaPhaLe(daPhaLe);
                    int param = getParamDaPhaLe(daPhaLe);
                    Item.ItemOption option = null;
                    for (Item.ItemOption io : trangBi.itemOptions) {
                        if (io.optionTemplate.id == optionId) {
                            option = io;
                            break;
                        }
                    }
                    if (option != null) {
                        option.param += param;
                    } else {
                        trangBi.itemOptions.add(new Item.ItemOption(optionId, param));
                    }
                    if (optionStar != null) {
                        optionStar.param++;
                    } else {
                        trangBi.itemOptions.add(new Item.ItemOption(102, 1));
                    }

                    InventoryServiceNew.gI().subQuantityItemsBag(player, daPhaLe, 1);
                    sendEffectSuccessCombine(player);
                }
                InventoryServiceNew.gI().sendItemBags(player);
                Service.getInstance().sendMoney(player);
                reOpenItemCombine(player);
            }
        }
    }

    private void phaLeHoaTrangBi(Player player) {
        if (!player.combineNew.itemsCombine.isEmpty()) {
            int gold = player.combineNew.goldCombine;
            int gem = player.combineNew.gemCombine;
            if (player.inventory.gold < gold) {
                Service.getInstance().sendThongBao(player, "Khรดng ฤแปง vร?ng ฤแป thแปฑc hiแปn");
                return;
            } else if (player.inventory.gem < gem) {
                Service.getInstance().sendThongBao(player, "Khรดng ฤแปง ngแปc ฤแป thแปฑc hiแปn");
                return;
            }
            Item item = player.combineNew.itemsCombine.get(0);
            if (isTrangBiPhaLeHoa(item)) {
                int star = 0;
                Item.ItemOption optionStar = null;
                for (Item.ItemOption io : item.itemOptions) {
                    if (io.optionTemplate.id == 107) {
                        star = io.param;
                        optionStar = io;
                        break;
                    }
                }
                if (star < MAX_STAR_ITEM) {
                    player.inventory.gold -= gold;
                    player.inventory.subGemAndRuby(gem);
                    if (Util.isTrue(player.combineNew.ratioCombine, 230)) {
                        if (optionStar == null) {
                            item.itemOptions.add(new Item.ItemOption(107, 1));
                        } else {
                            optionStar.param++;
                        }
//                        sendEffectSuccessCombine(player);
//                        if (optionStar != null && optionStar.param >= 7) {
//                            ServerNotify.gI().notify("Chรบc mแปซng " + player.name + " vแปซa pha lรช hรณa "
//                                    + "thร?nh cรดng " + item.template.name + " lรชn " + optionStar.param + " sao pha lรช");
//                        }
                    } else {
                        sendEffectFailCombine(player);
                    }
                }
                InventoryServiceNew.gI().sendItemBags(player);
                Service.getInstance().sendMoney(player);
                reOpenItemCombine(player);
            }
        }
    }

    private void nhapNgocRong(Player player) {
        if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
            if (!player.combineNew.itemsCombine.isEmpty()) {
                Item item = player.combineNew.itemsCombine.get(0);
                if (item != null && item.isNotNullItem() && (item.template.id > 14 && item.template.id <= 20) && item.quantity >= 7) {
                    Item nr = ItemService.gI().createNewItem((short) (item.template.id - 1));
                    InventoryServiceNew.gI().addItemBag(player, nr);
                    InventoryServiceNew.gI().subQuantityItemsBag(player, item, 7);
                    InventoryServiceNew.gI().sendItemBags(player);
                    reOpenItemCombine(player);
                    sendEffectCombineDB(player, item.template.iconID);
                }
            }
        }
    }

    private void nangCapVatPham(Player player) {
        if (player.combineNew.itemsCombine.size() == 2) {
            if (isCoupleItemNangCap(player.combineNew.itemsCombine.get(0), player.combineNew.itemsCombine.get(1))) {
                int countDaNangCap = player.combineNew.countDaNangCap;
                int gold = player.combineNew.goldCombine;
                if (player.inventory.gold < gold) {
                    Service.getInstance().sendThongBao(player, "Khรดng ฤแปง vร?ng ฤแป thแปฑc hiแปn");
                    return;
                }
                Item trangBi = null;
                Item daNangCap = null;
                if (player.combineNew.itemsCombine.get(0).template.type < 5) {
                    trangBi = player.combineNew.itemsCombine.get(0);
                    daNangCap = player.combineNew.itemsCombine.get(1);
                } else {
                    trangBi = player.combineNew.itemsCombine.get(1);
                    daNangCap = player.combineNew.itemsCombine.get(0);
                }
                if (daNangCap.quantity < countDaNangCap) {
                    return;
                }
                int level = 0;
                Item.ItemOption optionLevel = null;
                for (Item.ItemOption io : trangBi.itemOptions) {
                    if (io.optionTemplate.id == 72) {
                        level = io.param;
                        optionLevel = io;
                        break;
                    }
                }
                if (level < MAX_LEVEL_ITEM) {
                    player.inventory.gold -= gold;
                    Item.ItemOption option = null;
                    Item.ItemOption option2 = null;
                    for (Item.ItemOption io : trangBi.itemOptions) {
                        if (io.optionTemplate.id == 47
                                || io.optionTemplate.id == 6
                                || io.optionTemplate.id == 0
                                || io.optionTemplate.id == 7
                                || io.optionTemplate.id == 14
                                || io.optionTemplate.id == 22
                                || io.optionTemplate.id == 23) {
                            option = io;
                        } else if (io.optionTemplate.id == 27
                                || io.optionTemplate.id == 28) {
                            option2 = io;
                        }
                    }
                    if (Util.isTrue(player.combineNew.ratioCombine, 100)) {
                        option.param += (option.param * 10 / 100);
                        if (option2 != null) {
                            option2.param += (option2.param * 10 / 100);
                        }
                        if (optionLevel == null) {
                            trangBi.itemOptions.add(new Item.ItemOption(72, 1));
                        } else {
                            optionLevel.param++;
                        }
//                        if (optionLevel != null && optionLevel.param >= 5) {
//                            ServerNotify.gI().notify("Chรบc mแปซng " + player.name + " vแปซa nรขng cแบฅp "
//                                    + "thร?nh cรดng " + trangBi.template.name + " lรชn +" + optionLevel.param);
//                        }
                        sendEffectSuccessCombine(player);
                    } else {
                        if (level == 2 || level == 4 || level == 6) {
                            option.param -= (option.param * 10 / 100);
                            if (option2 != null) {
                                option2.param -= (option2.param * 10 / 100);
                            }
                            optionLevel.param--;
                        }
                        sendEffectFailCombine(player);
                    }
                    InventoryServiceNew.gI().subQuantityItemsBag(player, daNangCap, player.combineNew.countDaNangCap);
                    InventoryServiceNew.gI().sendItemBags(player);
                    Service.getInstance().sendMoney(player);
                    reOpenItemCombine(player);

                }
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Hiแปu แปฉng mแป item
     *
     * @param player
     */
    public void sendEffectOpenItem(Player player, short icon1, short icon2) {
        Message msg;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(OPEN_ITEM);
            msg.writer().writeShort(icon1);
            msg.writer().writeShort(icon2);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    /**
     * Hiแปu แปฉng ฤแบญp ฤแป thร?nh cรดng
     *
     * @param player
     */
    private void sendEffectSuccessCombine(Player player) {
        Message msg;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(COMBINE_SUCCESS);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    /**
     * Hiแปu แปฉng ฤแบญp ฤแป thแบฅt bแบกi
     *
     * @param player
     */
    private void sendEffectFailCombine(Player player) {
        Message msg;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(COMBINE_FAIL);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    /**
     * Gแปญi lแบกi danh sรกch ฤแป trong tab combine
     *
     * @param player
     */
    private void reOpenItemCombine(Player player) {
        Message msg;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(REOPEN_TAB_COMBINE);
            msg.writer().writeByte(player.combineNew.itemsCombine.size());
            for (Item it : player.combineNew.itemsCombine) {
                for (int j = 0; j < player.inventory.itemsBag.size(); j++) {
                    if (it == player.inventory.itemsBag.get(j)) {
                        msg.writer().writeByte(j);
                    }
                }
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    /**
     *
     * Hiแปu แปฉng ghรฉp ngแปc rแปng
     *
     * @param player
     * @param icon
     */
    private void sendEffectCombineDB(Player player, short icon) {
        Message msg;
        try {
            msg = new Message(-81);
            msg.writer().writeByte(COMBINE_DRAGON_BALL);
            msg.writer().writeShort(icon);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    //--------------------------------------------------------------------------Ratio, cost combine
    private int getGoldPhaLeHoa(int star) {
        switch (star) {
            case 0:
                return 5000000;
            case 1:
                return 10000000;
            case 2:
                return 20000000;
            case 3:
                return 40000000;
            case 4:
                return 60000000;
            case 5:
                return 90000000;
            case 6:
                return 120000000;
            case 7:
                return 200000000;
           case 8:
                return 5000000;
            case 9:
                return 10000000;
            case 10:
                return 20000000;
            case 11:
                return 40000000;
            case 12:
                return 60000000;
            case 13:
                return 90000000;
            case 14:
                return 120000000;
            case 15:
                return 200000000;
                
            case 16:
                return 5000000;
            case 17:
                return 10000000;
            case 18:
                return 20000000;
            case 19:
                return 40000000;
            case 20:
                return 60000000;
            
        }
        return 0;
    }

    private float getRatioPhaLeHoa(int star) {
        switch (star) {
            case 0:
                return 80f;
            case 1:
                return 50f;
            case 2:
                return 20f;
            case 3:
                return 10f;
            case 4:
                return 5f;
            case 5:
                return 2f;
            case 6:
                return 1f;
            case 7:
                return 0.3f;
            case 8:
                return 1f;
            case 9:
                return 1f;
            case 10:
                return 1f;
            case 11:
                return 1f;
            case 12:
                return 5f;
            case 13:
                return 2f;
            case 14:
                return 1f;
            case 15:
                return 0.3f;
            case 16:
                return 80f;
            case 17:
                return 50f;
            case 18:
                return 20f;
            case 19:
                return 10f;
            case 20:
                return 5f;
            
        }
        return 0;
    }

    private int getGemPhaLeHoa(int star) {
        switch (star) {
            case 0:
                return 10;
            case 1:
                return 20;
            case 2:
                return 30;
            case 3:
                return 40;
            case 4:
                return 50;
            case 5:
                return 60;
            case 6:
                return 70;
            case 7:
                return 80;
            case 8:
                return 10;
            case 9:
                return 20;
            case 10:
                return 30;
            case 11:
                return 40;
            case 12:
                return 50;
            case 13:
                return 60;
            case 14:
                return 70;
            case 15:
                return 80;
            case 16:
                return 10;
            case 17:
                return 20;
            case 18:
                return 30;
            case 19:
                return 40;
            case 20:
                return 50;
            
        }
        return 0;
    }

    private int getGemEpSao(int star) {
        switch (star) {
           case 0:
                return 10;
            case 1:
                return 20;
            case 2:
                return 30;
            case 3:
                return 40;
            case 4:
                return 50;
            case 5:
                return 60;
            case 6:
                return 70;
            case 7:
                return 80;
            case 8:
                return 10;
            case 9:
                return 20;
            case 10:
                return 30;
            case 11:
                return 40;
            case 12:
                return 50;
            case 13:
                return 60;
            case 14:
                return 70;
            case 15:
                return 80;
            case 16:
                return 10;
            case 17:
                return 20;
            case 18:
                return 30;
            case 19:
                return 40;
            case 20:
                return 50;
        }
        return 0;
    }

    private int getTileNangCapDo(int level) {
        switch (level) {
            case 0:
                return 80;
            case 1:
                return 50;
            case 2:
                return 20;
            case 3:
                return 10;
            case 4:
                return 7;
            case 5:
                return 5;
            case 6:
                return 1;
        }
        return 0;
    }

    private int getCountDaNangCapDo(int level) {
        switch (level) {
            case 0:
                return 3;
            case 1:
                return 7;
            case 2:
                return 11;
            case 3:
                return 17;
            case 4:
                return 23;
            case 5:
                return 35;
            case 6:
                return 50;
        }
        return 0;
    }

    private int getGoldNangCapDo(int level) {
        switch (level) {
            case 0:
                return 10000;
            case 1:
                return 70000;
            case 2:
                return 300000;
            case 3:
                return 1500000;
            case 4:
                return 7000000;
            case 5:
                return 23000000;
            case 6:
                return 100000000;
        }
        return 0;
    }

    //--------------------------------------------------------------------------check
    private boolean isCoupleItemNangCap(Item item1, Item item2) {
        Item trangBi = null;
        Item daNangCap = null;
        if (item1 != null && item1.isNotNullItem()) {
            if (item1.template.type < 5) {
                trangBi = item1;
            } else if (item1.template.type == 14) {
                daNangCap = item1;
            }
        }
        if (item2 != null && item2.isNotNullItem()) {
            if (item2.template.type < 5) {
                trangBi = item2;
            } else if (item2.template.type == 14) {
                daNangCap = item2;
            }
        }
        if (trangBi != null && daNangCap != null) {
            if (trangBi.template.type == 0 && daNangCap.template.id == 223) {
                return true;
            } else if (trangBi.template.type == 1 && daNangCap.template.id == 222) {
                return true;
            } else if (trangBi.template.type == 2 && daNangCap.template.id == 224) {
                return true;
            } else if (trangBi.template.type == 3 && daNangCap.template.id == 221) {
                return true;
            } else if (trangBi.template.type == 4 && daNangCap.template.id == 220) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean isDaPhaLe(Item item) {
        return item.template.type == 30 || (item.template.id >= 14 && item.template.id <= 20);
    }

    private boolean isTrangBiPhaLeHoa(Item item) {
        if (item != null && item.isNotNullItem()) {
            if (item.template.type < 5 || item.template.type == 32) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private int getParamDaPhaLe(Item daPhaLe) {
        if (daPhaLe.template.type == 30) {
            return daPhaLe.itemOptions.get(0).param;
        }
        switch (daPhaLe.template.id) {
            case 20:
                return 5; // +5%hp
            case 19:
                return 5; // +5%ki
            case 18:
                return 5; // +5%hp/30s
            case 17:
                return 5; // +5%ki/30s
            case 16:
                return 3; // +3%sฤ
            case 15:
                return 2; // +2%giรกp
            case 14:
                return 2; // +2%nรฉ ฤรฒn
            default:
                return -1;
        }
    }

    private int getOptionDaPhaLe(Item daPhaLe) {
        if (daPhaLe.template.type == 30) {
            return daPhaLe.itemOptions.get(0).optionTemplate.id;
        }
        switch (daPhaLe.template.id) {
            case 20:
                return 77;
            case 19:
                return 103;
            case 18:
                return 80;
            case 17:
                return 81;
            case 16:
                return 50;
            case 15:
                return 94;
            case 14:
                return 108;
            default:
                return -1;
        }
    }

    /**
     * Trแบฃ vแป id item c0
     *
     * @param gender
     * @param type
     * @return
     */
    private int getTempIdItemC0(int gender, int type) {
        if (type == 4) {
            return 12;
        }
        switch (gender) {
            case 0:
                switch (type) {
                    case 0:
                        return 0;
                    case 1:
                        return 6;
                    case 2:
                        return 21;
                    case 3:
                        return 27;
                }
                break;
            case 1:
                switch (type) {
                    case 0:
                        return 1;
                    case 1:
                        return 7;
                    case 2:
                        return 22;
                    case 3:
                        return 28;
                }
                break;
            case 2:
                switch (type) {
                    case 0:
                        return 2;
                    case 1:
                        return 8;
                    case 2:
                        return 23;
                    case 3:
                        return 29;
                }
                break;
        }
        return -1;
    }

    //Trแบฃ vแป tรชn ฤแป c0
    private String getNameItemC0(int gender, int type) {
        if (type == 4) {
            return "Rada cแบฅp 1";
        }
        switch (gender) {
            case 0:
                switch (type) {
                    case 0:
                        return "รo vแบฃi 3 lแป";
                    case 1:
                        return "Quแบงn vแบฃi ฤen";
                    case 2:
                        return "Gฤng thun ฤen";
                    case 3:
                        return "Giแบงy nhแปฑa";
                }
                break;
            case 1:
                switch (type) {
                    case 0:
                        return "รo sแปฃi len";
                    case 1:
                        return "Quแบงn sแปฃi len";
                    case 2:
                        return "Gฤng sแปฃi len";
                    case 3:
                        return "Giแบงy sแปฃi len";
                }
                break;
            case 2:
                switch (type) {
                    case 0:
                        return "รo vแบฃi thรด";
                    case 1:
                        return "Quแบงn vแบฃi thรด";
                    case 2:
                        return "Gฤng vแบฃi thรด";
                    case 3:
                        return "Giแบงy vแบฃi thรด";
                }
                break;
        }
        return "";
    }

//--------------------------------------------------------------------------Text tab combine
    private String getTextTopTabCombine(int type) {
        switch (type) {
            case EP_SAO_TRANG_BI:
                return "Ta sแบฝ phรน phรฉp\ncho trang bแป cแปงa ngฦฐฦกi\ntrแป lรชn mแบกnh mแบฝ";
            case PHA_LE_HOA_TRANG_BI:
                return "Ta sแบฝ phรน phรฉp\ncho trang bแป cแปงa ngฦฐฦกi\ntrแป thร?nh trang bแป pha lรช";
            case NHAP_NGOC_RONG:
                return "Ta sแบฝ phรน phรฉp\ncho 7 viรชn Ngแปc Rแปng\nthร?nh 1 viรชn Ngแปc Rแปng cแบฅp cao";
            case NANG_CAP_VAT_PHAM:
                return "Ta sแบฝ phรน phรฉp cho trang bแป cแปงa ngฦฐฦกi trแป lรชn mแบกnh mแบฝ";
            default:
                return "";
        }
    }

    private String getTextInfoTabCombine(int type) {
        switch (type) {
            case EP_SAO_TRANG_BI:
                return "Chแปn trang bแป\n(รo, quแบงn, gฤng, giร?y hoแบทc raฤa) cรณ รด ฤแบทt sao pha lรช\nChแปn loแบกi sao pha lรช\n"
                        + "Sau ฤรณ chแปn 'Nรขng cแบฅp'";
            case PHA_LE_HOA_TRANG_BI:
                return "Chแปn trang bแป\n(รo, quแบงn, gฤng, giร?y hoแบทc raฤa)\nSau ฤรณ chแปn 'Nรขng cแบฅp'";
            case NHAP_NGOC_RONG:
                return "Vร?o hร?nh trang\nChแปn 7 viรชn ngแปc cรนng sao\nSau ฤรณ chแปn 'Lร?m phรฉp'";
            case NANG_CAP_VAT_PHAM:
                return "vร?o hร?nh trang\nChแปn trang bแป\n(รo, quแบงn, gฤng, giร?y hoแบทc raฤa)\nChแปn loแบกi ฤรก ฤแป nรขng cแบฅp\n"
                        + "Sau ฤรณ chแปn 'Nรขng cแบฅp'";
            default:
                return "";
        }
    }

}
