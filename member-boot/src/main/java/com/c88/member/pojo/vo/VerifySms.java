package com.c88.member.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class VerifySms {

    @Schema(title = "會員帳號", required = true)
    String userName;

    @Schema(title = "手機", required = true)
    String mobile;

    @Schema(title = "SessionId 發送Sms驗證碼後的回傳值", required = true)
    String sessionId;

    @Schema(title = "輸入的驗証碼")
    String code;

}
