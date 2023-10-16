package com.c88.member.pojo.form;

import com.c88.common.web.annotation.Mobile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class MemberForm{

    @Schema(title = "會員帳號")
    private String userName;

    @Schema(title = "暱稱")
    private String nickName;

    @Schema(title = "手機")
    @Mobile
    private String mobile;

    @Schema(title = "性別")
    private Integer gender;

    private String avatar;

    @Schema(title = "密碼")
    private String password;

    @Schema(title = "電子郵箱")
    private String email;

    @Schema(title = "SessionId 發送Sms驗證碼後的回傳值，手機登入時需要")
    String sessionId;

    @Schema(title = "驗證碼（手機或電子郵件）")
    private String code;

    @Schema(title = "邀請碼")
    private String promotionCode;

    @Schema(title = "裝置識別碼")
    private String deviceCode;

    @Schema(title = "裝置")
    private String device;

    @Schema(title = "Google recaptcha 驗證值")
    private String token;

    @Schema(title = "登入域名")
    private String loginDomain;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer status;

    @JsonIgnore
    private String displayPassword;

}
