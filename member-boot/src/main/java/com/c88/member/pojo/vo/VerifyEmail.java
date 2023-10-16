package com.c88.member.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class VerifyEmail {

    @Schema(title = "會員帳號", required = true)
    String userName;

    @Schema(title = "電子郵件", required = true)
    String email;

    @Schema(title = "SessionId 發送電子郵件驗證碼後的回傳值", required = true)
    String sessionId;

    @Schema(title = "輸入的驗証碼")
    String code;

}
