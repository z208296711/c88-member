package com.c88.member.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title = "白菜紅包模組領取條件")
public class MemberRedEnvelopeTemplateConditionVO {

    @Schema(title = "白菜紅包模型ID")
    private Long templateId;

    @Schema(title = "項目階層")
    private Integer level;

    @Schema(title = "充值要求")
    private BigDecimal rechargeAmount;

    @Schema(title = "有效投注")
    private BigDecimal validBetAmount;

    @Schema(title = "紅包金額")
    private BigDecimal amount;
}
