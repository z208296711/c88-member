package com.c88.member.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(title = "修改會員提款密碼表單")
public class ModifyMemberWithdrawPasswordForm {

    @NotNull(message = "會員ID不得為空")
    @Schema(title = "會員ID")
    private Long memberId;

    @NotNull(message = "新提款密碼不得為空")
    @Schema(title = "新提款密碼")
    private String newWithdrawPassword;

    @NotNull(message = "確認新提款密碼不得為空")
    @Schema(title = "確認新提款密碼")
    private String confirmWithdrawPassword;

}
