package com.c88.member.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(title = "修改會員VIP")
public class UpdateMemberVipForm {

    @Schema(title = "會員Id")
    @NotNull
    private Long memberId;

    @Schema(title = "vipId")
    @NotNull
    private Integer vipId;

    @Schema(title = "note")
    private String note;

}
