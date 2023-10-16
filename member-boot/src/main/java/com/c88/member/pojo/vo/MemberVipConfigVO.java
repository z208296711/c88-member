package com.c88.member.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title = "會員VIP")
public class MemberVipConfigVO {

    @Schema(title = "編號")
    private Integer id;

    @Schema(title = "等級名稱")
    private String name;

    @Schema(title = "升級模式 1:自動 2:手動")
    private Integer levelUpMode;

    @Schema(title = "升級條件 （充值/經驗值）")
    private String levelUpCondition;

    @Schema(title = "維持條件 （充值/經驗值）")
    private String keepCondition;

    @Schema(title = "晉級禮金")
    private Integer levelUpGift;



    @Schema(title = "生日禮金")
    private Integer birthdayGift;

    @Schema(title = "救援金比例")
    private BigDecimal emergencyRate;

    @Schema(title = "金幣兌換折扣")
    private BigDecimal coinExchangeDiscount;

    @Schema(title = "單日提款金額上限")
    private Integer dailyWithdrawAmount;

    @Schema(title = "每日反水領取上限")
    private Integer dailyBackwaterLimit;

    @Schema(title = "升級條件-經驗值")
    private BigDecimal levelUpExp;

    @Schema(title = "升級條件-充值金額")
    private BigDecimal levelUpRecharge;

    @Schema(title = "維持條件-經驗值")
    private BigDecimal keepExp;

    @Schema(title = "維持條件-充值金額")
    private BigDecimal keepRecharge;

    @Schema(title = "等級圖標")
    private String logo;

    @Schema(title = "降級審核 0:不審 1:要審")
    private Integer levelUpReview;

    @Schema(title = "升級條件備註-手動升級用")
    private String levelUpNote;

    @Schema(title = "維持條件備註-手動升級用")
    private String keepLevelNote;

    @Schema(title = "每週免費籌碼")
    private BigDecimal weeklyFreeBet = BigDecimal.ZERO;

    @Schema(title = "每月免費籌碼")
    private BigDecimal monthlyFreeBet = BigDecimal.ZERO;

    @Schema(title = "排序（手動）")
    private Integer sort;
}
