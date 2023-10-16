package com.c88.member.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VipDetailVO {

    @Schema(title = "VIP-Id")
    private Integer id;

    @Schema(title = "VIP-名稱")
    private String name;

    @Schema(title = "等級圖標")
    private String logo;

    @Schema(title = "升級模式 1:自動 2:提升")
    private Integer levelUpMode;

    @Schema(title = "升級條件-經驗值")
    private BigDecimal levelUpExp;

    @Schema(title = "升級條件-充值金額")
    private BigDecimal levelUpRecharge;

    @Schema(title = "保級條件-經驗值")
    private BigDecimal keepExp;

    @Schema(title = "保級條件-充值金額")
    private Integer keepRecharge;

    @Schema(title = "晉級禮金")
    private Integer levelUpGift;

    @Schema(title = "每週免費籌碼")
    private BigDecimal weeklyFreeBet;

    @Schema(title = "每月免費籌碼")
    private BigDecimal monthlyFreeBet;

    @Schema(title = "生日禮金")
    private Integer birthdayGift;

    @Schema(title = "救援金比例")
    private BigDecimal emergencyRate;

    @Schema(title = "金幣兌換折扣")
    private BigDecimal coinExchangeDiscount;

    @Schema(title = "單日提款金額上限")
    private Integer dailyWithdrawAmount;

    @Schema(title = "每日反水領取上限")
    private BigDecimal dailyBackwaterLimit;

    @Schema(title = "升級條件備註-手動升級用")
    private String levelUpNote;

    @Schema(title = "保級條件備註-手動升級用")
    private String keepLevelNote;


}
