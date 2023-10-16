package com.c88.member.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title = "白菜紅包模型領取條件表單")
public class AddMemberRedEnvelopeTemplateConditionForm {

    @Schema(title = "階層")
    private Integer level;

    @Schema(title = "充值要求")
    private BigDecimal rechargeAmount;

    @Schema(title = "有效投注要求")
    private BigDecimal validBetAmount;

    @Schema(title = "紅包金額")
    private BigDecimal amount;

}
