package com.c88.member.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(title = "ReceiveLevelUpGiftForm")
public class ReceiveLevelUpGiftForm  {

    @Schema(title = "vipId")
    @NotNull
    private Integer vipId;

}
