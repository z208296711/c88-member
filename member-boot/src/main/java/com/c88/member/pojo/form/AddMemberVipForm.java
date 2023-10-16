package com.c88.member.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
@Schema(title = "新增vip會員等級")
public class AddMemberVipForm {

    @Schema(title = "等級名稱")
    @NotBlank
    private String name;

    @Schema(title = "等級圖標")
    @NotBlank
    private String logo;

    @Schema(title = "升級模式 0:未知 1:自動 2:手動")
    @NotNull
    private Integer levelUpMode;

    @Schema(title = "降級審核 0:不審 1:要審")
    private Integer levelUpReview;

    @Schema(title = "升級條件-經驗值")
    @Min(0)
    @Max(9999999)
    private Integer levelUpExp;

    @Schema(title = "升級條件-充值金額")
    private Integer levelUpRecharge;

    @Schema(title = "維持條件-經驗值")
    private Integer keepExp;

    @Schema(title = "維持條件-充值金額")
    private Integer keepRecharge;

    @Schema(title = "晉級禮金")
    private Integer levelUpGift;

    @Schema(title = "生日禮金")
    private Integer birthdayGift;

    @Schema(title = "救援金比例")
    private BigDecimal emergencyRate;

    @Schema(title = "金幣兌換折扣")
    private BigDecimal coinExchangeDiscount;

    @Schema(title = "單日提款金額上限")
    @Min(0)
    @Max(9999999)
    private Integer dailyWithdrawAmount;

    @Schema(title = "每日反水領取上限")
    @Min(0)
    @Max(9999999)
    private Integer dailyBackwaterLimit;

    @Schema(title = "升級條件備註-手動升級用 levelUpMode=2時,必填")
    private String levelUpNote;

    @Schema(title = "維持條件備註-手動升級用 levelUpMode=2時,必填")
    private String keepLevelNote;

    @Schema(title = "手動升級-排序 levelUpMode=1時,必填")
    private Integer sort;

    @Schema(title = "每週免費籌碼")
    private BigDecimal weeklyFreeBet = BigDecimal.ZERO;

    @Schema(title = "每月免費籌碼")
    private BigDecimal monthlyFreeBet = BigDecimal.ZERO;


}
