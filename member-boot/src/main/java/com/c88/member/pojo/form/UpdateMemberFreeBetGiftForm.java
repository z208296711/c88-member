package com.c88.member.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Schema(title = "UpdateMemberLevelUpGiftForm")
public class UpdateMemberFreeBetGiftForm {

    @Schema(title = "領取週期 1:週, 2:月")
    @NotNull
    private Integer type;

    @Schema(title = "是否啟用")
    @NotNull
    private Integer enable;

    @Schema(title = "領取效期")
    @NotNull
    private Integer value;

    @Schema(title = "金額 (上週充值/當月充值)")
    @NotNull
    private Integer amount;

    @Schema(title = "流水倍率")
    @NotNull
    private BigDecimal betRate;


}
