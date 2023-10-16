package com.c88.member.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Schema(title = "UpdateMemberLevelUpGiftForm")
public class UpdateMemberLevelUpGiftForm {

    @Schema(title = "流水倍率")
    @NotNull
    private BigDecimal betRate;


}
