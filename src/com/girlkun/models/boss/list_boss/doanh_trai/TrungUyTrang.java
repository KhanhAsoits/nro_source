package com.girlkun.models.boss.list_boss.doanh_trai;

import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossesData;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

/**
 *
 * @author ❤Girlkun75❤
 * @copyright ❤Trần Lại❤
 */
public class TrungUyTrang extends Boss{

    public TrungUyTrang() throws Exception{
        super(BossID.TRUNG_UY_TRANG, BossesData.TRUNG_UY_TRANG);
    }
    @Override
    public void reward(Player plKill) {
        int[] itemRan = new int[]{17,18,19,20};
        int itemId = itemRan[2];
        if (Util.isTrue(100, 100)) {
            ItemMap it = new ItemMap(this.zone, itemId, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
        Service.getInstance().dropItemMap(this.zone, it);
        }
    }
    
}






















/**
 * Vui lòng không sao chép mã nguồn này dưới mọi hình thức.
 * Hãy tôn trọng tác giả của mã nguồn này.
 * Xin cảm ơn! - Girlkun75
 */