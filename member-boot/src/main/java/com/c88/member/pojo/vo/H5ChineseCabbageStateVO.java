package com.c88.member.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "白菜紅包")
public class H5ChineseCabbageStateVO {

    @Schema(title = "白菜紅包模組ID")
    private Long id;

    @Schema(title = "白菜紅包模組名稱")
    private String name;

    @Schema(title = "金額")
    private BigDecimal amount;

    @Schema(title = "流水要求")
    private BigDecimal exp;

    @Schema(title = "是否審核", description = "0不需審核 1需審核")
    private Integer isReview;

    @Schema(title = "狀態", description = "1立即領取 2已領取 3待審核 4審核未通過 5尚不符合資格")
    private Integer state;

    @Schema(title = "當前充值量")
    private BigDecimal currentRechargeAmount;

    @Schema(title = "目標充值要求")
    private BigDecimal targetRechargeAmount;

    @Schema(title = "當前有效投注要求")
    private BigDecimal currentValidBetAmount;

    @Schema(title = "目標有效投注要求")
    private BigDecimal targetValidBetAmount;

}
