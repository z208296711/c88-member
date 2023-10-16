package com.c88.member.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(title = "ReceiveFreeBetGiftForm")
public class ReceiveFreeBetGiftForm {

    @Schema(title = "type 1:週 2:月")
    @NotNull
    private Integer type;

}
