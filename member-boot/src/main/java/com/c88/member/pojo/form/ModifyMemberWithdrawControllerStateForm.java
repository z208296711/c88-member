package com.c88.member.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(title = "修改會員提款控制表單")
public class ModifyMemberWithdrawControllerStateForm {

    @NotNull(message = "會員ID不得為空")
    @Schema(title = "會員ID")
    private Long memberId;

    @NotNull(message = "提款控制不得為空")
    @Schema(title = "提款控制", description = "0關閉提款 1開啟提款")
    private Integer withdrawControllerState;

}
