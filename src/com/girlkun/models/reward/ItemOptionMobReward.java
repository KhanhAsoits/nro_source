package com.girlkun.models.reward;

import com.girlkun.models.Template;
import com.girlkun.server.Manager;
import lombok.Data;

/**
 *
 * @author ❤Girlkun75❤
 * @copyright ❤Trần Lại❤
 */
@Data
public class ItemOptionMobReward {

    private Template.ItemOptionTemplate temp;
    private int[] param;
    private int[] ratio;

    public ItemOptionMobReward(int tempId, int[] param, int[] ratio) {
        this.temp = Manager.ITEM_OPTION_TEMPLATES.get(tempId);
        this.param = param;
        if (this.param[0] < 0) {
            this.param[0] = -this.param[0];
        } else if (this.param[0] == 0) {
            this.param[0] = 1;
        }
        if (this.param[1] < 0) {
            this.param[1] = -this.param[1];
        } else if (this.param[1] == 0) {
            this.param[1] = 1;
        }
        if (this.param[0] > this.param[1]) {
            int tempSwap = this.param[0];
            this.param[0] = this.param[1];
            this.param[1] = tempSwap;
        }
        this.ratio = ratio;
    }
    
}






















/**
 * Vui lòng không sao chép mã nguồn này dưới mọi hình thức.
 * Hãy tôn trọng tác giả của mã nguồn này.
 * Xin cảm ơn! - Girlkun75
 */