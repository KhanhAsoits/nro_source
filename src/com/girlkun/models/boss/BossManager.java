package com.girlkun.models.boss;
import com.girlkun.models.boss.list_boss.Bubbles;
import com.girlkun.models.boss.list_boss.AnTrom;
//import com.girlkun.models.boss.list_boss.Cadic;
import com.girlkun.models.boss.list_boss.BLACK.Black;
import com.girlkun.models.boss.list_boss.NgucTu.CoolerGold;
import com.girlkun.models.boss.list_boss.Doraemon.Doraemon;
import com.girlkun.models.boss.list_boss.FideBack.Kingcold;
import com.girlkun.models.boss.list_boss.Mabu;
import com.girlkun.models.boss.list_boss.NgucTu.Cumber;
import com.girlkun.models.boss.list_boss.cell.Xencon;
import com.girlkun.models.boss.list_boss.ginyu.So1;
import com.girlkun.models.boss.list_boss.ginyu.TieuDoiTruong;
import com.girlkun.models.boss.list_boss.ginyu.So4;
import com.girlkun.models.boss.list_boss.ginyu.So2;
import com.girlkun.models.boss.list_boss.ginyu.So3;
import com.girlkun.models.boss.list_boss.android.*;
import com.girlkun.models.boss.list_boss.cell.SieuBoHung;
import com.girlkun.models.boss.list_boss.cell.XenBoHung;
import com.girlkun.models.boss.list_boss.doanh_trai.TrungUyTrang;
import com.girlkun.models.boss.list_boss.Broly.Broly;
import com.girlkun.models.boss.list_boss.Doraemon.Nobita;
import com.girlkun.models.boss.list_boss.Doraemon.Xeko;
import com.girlkun.models.boss.list_boss.Doraemon.Xuka;
import com.girlkun.models.boss.list_boss.FideBack.FideRobot;
import com.girlkun.models.boss.list_boss.NgucTu.SongokuTaAc;
import com.girlkun.models.boss.list_boss.fide.Fide;
import com.girlkun.models.boss.list_boss.Doraemon.Chaien;
import com.girlkun.models.boss.list_boss.nappa.*;
import com.girlkun.server.ServerManager;
import concac.test;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BossManager implements Runnable {

    public static final int XEN_BO_HUNG = 1;

    private static BossManager I;

    public static BossManager gI() {
        if (BossManager.I == null) {
            BossManager.I = new BossManager();
        }
        return BossManager.I;
    }

    private BossManager() {
        this.bosses = new ArrayList<>();
    }

    private boolean loadedBoss;
    private List<Boss> bosses;

    public void addBoss(Boss boss) {
        this.bosses.add(boss);
    }

    public void loadBoss() {
        if (this.loadedBoss) {
            return;
        }
        try {
//            this.createBoss(BossID.CA_DIC);
            this.createBoss(BossID.BUBBLES);
            this.createBoss(BossID.AN_TROM);
            this.createBoss(BossID.CUMBER);
            this.createBoss(BossID.TRUNG_UY_TRANG);
            this.createBoss(BossID.BLACK);
            this.createBoss(BossID.SONGOKU_TA_AC);
            this.createBoss(BossID.PIC);
            this.createBoss(BossID.POC);
            this.createBoss(BossID.DORAEMON);
            this.createBoss(BossID.KING_KONG);
            this.createBoss(BossID.FIDE_ROBOT);
            this.createBoss(BossID.VUA_COLD);
            this.createBoss(BossID.KUKU);
            this.createBoss(BossID.TEST);
            this.createBoss(BossID.COOLER_GOLD);
            this.createBoss(BossID.SIEU_BO_HUNG);
            this.createBoss(BossID.XEN_BO_HUNG);
            this.createBoss(BossID.TIEU_DOI_TRUONG);
            this.createBoss(BossID.KUKU);
            this.createBoss(BossID.MAP_DAU_DINH);
            this.createBoss(BossID.RAMBO);
            this.createBoss(BossID.FIDE);
            this.createBoss(BossID.DR_KORE);
            this.createBoss(BossID.ANDROID_14);
            this.createBoss(BossID.MABU);
            this.createBoss(BossID.BROLY);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.loadedBoss = true;
        new Thread(BossManager.I, "Update boss").start();
    }

    public Boss createBoss(int bossID) {
        try {
            switch (bossID) {
                case BossID.SO_4:
                    return new So4();
                case BossID.SO_3:
                    return new So3();
                case BossID.SO_2:
                    return new So2();
                case BossID.SO_1:
                    return new So1();
                case BossID.TIEU_DOI_TRUONG:
                    return new TieuDoiTruong();
                case BossID.AN_TROM:
                    return new AnTrom();     
                case BossID.KUKU:
                    return new Kuku();
                case BossID.MAP_DAU_DINH:
                    return new MapDauDinh();
                case BossID.RAMBO:
                    return new Rambo();
                case BossID.BUBBLES:
                    return new Bubbles();    
                case BossID.FIDE:
                    return new Fide();
//                 case BossID.CA_DIC:
//                    return new Cadic();     
                  
                case BossID.DR_KORE:
                    return new DrKore();
                case BossID.ANDROID_19:
                    return new Android19();
                    case BossID.TEST:
                    return new test();
                case BossID.ANDROID_13:
                    return new Android13();
                case BossID.ANDROID_14:
                    return new Android14();
                case BossID.ANDROID_15:
                    return new Android15();
                    
                case BossID.PIC:
                    return new Pic();
                case BossID.POC:
                    return new Poc();
                case BossID.KING_KONG:
                    return new KingKong();
                    
                case BossID.XEN_BO_HUNG:
                    return new XenBoHung();
                    
                case BossID.SIEU_BO_HUNG:
                    return new SieuBoHung();
                    
                case BossID.TRUNG_UY_TRANG:
                    return new TrungUyTrang();

                case BossID.XUKA:
                    return new Xuka();
                case BossID.NOBITA:
                    return new Nobita();
                case BossID.XEKO:
                    return new Xeko();
                case BossID.CHAIEN:
                    return new Chaien();
                case BossID.DORAEMON:
                    return new Doraemon();

                case BossID.VUA_COLD:
                    return new Kingcold();                    
                case BossID.FIDE_ROBOT:
                    return new FideRobot();
                    
                case BossID.BLACK:
                    return new Black();
                    
                case BossID.XEN_CON:
                    return new Xencon();
                    
                case BossID.MABU:
                    return new Mabu();
                    
                    
                case BossID.COOLER_GOLD:
                    return new CoolerGold();
                case BossID.CUMBER:
                    return new Cumber();     
                case BossID.SONGOKU_TA_AC:
                    return new SongokuTaAc();
                    
                case BossID.BROLY:
                    return new Broly();
                    

                default:
                    return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void run() {
        while (ServerManager.isRunning) {
            try {
                long st = System.currentTimeMillis();
                for (Boss boss : this.bosses) {
                    boss.update();
                }
                Thread.sleep(150 - (System.currentTimeMillis() - st));
            } catch (Exception e) {
            }

        }
    }
}

/**
 * Vui lòng không sao chép mã nguồn này dưới mọi hình thức. Hãy tôn trọng tác
 * giả của mã nguồn này. Xin cảm ơn! - Girlkun75
 */
