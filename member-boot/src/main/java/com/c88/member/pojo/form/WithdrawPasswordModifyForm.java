package com.c88.member.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Schema(title = "修改提款密碼")
public class WithdrawPasswordModifyForm {

    @Pattern(regexp = "[a-zA-Z0-9]{6,16}", message = "請輸入6~16位英文或數字")
    @NotNull(message = "舊密碼不得為空")
    @Schema(title = "舊密碼")
    private String oldPassword;

    @Pattern(regexp = "[a-zA-Z0-9]{6,16}", message = "請輸入6~16位英文或數字")
    @NotNull(message = "新密碼不得為空")
    @Schema(title = "新密碼")
    private String password;

    @Pattern(regexp = "[a-zA-Z0-9]{6,16}", message = "請輸入6~16位英文或數字")
    @NotNull(message = "確認密碼不得為空")
    @Schema(title = "確認密碼")
    private String confirmPassword;

}
