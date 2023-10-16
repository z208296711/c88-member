package com.c88.member.dto;

import com.c88.common.core.base.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MemberDTO extends BaseEntity {
    @Schema(title = "會員id")
    private Long id;

    @Schema(title = "帳號")
    private String username;

    @Schema(title = "真實姓名")
    private String realName;

    @Schema(title = "提款密碼")
    private String withdrawPassword;

    @Schema(title = "密碼")
    private String password;

    @Schema(title = "顯示密碼", description = "只顯示頭和末1碼")
    private String displayPassword;

    @Schema(title = "手機號碼")
    private String mobile;

    @Schema(title = "電子郵件")
    private String email;

    // @Schema(title = "餘額")
    // private BigDecimal balance;
    //
    // /**
    //  * 提款限額（為0才可提款）
    //  */
    // @Schema(title = "提款限額")
    // private BigDecimal withdrawLimit;

    @Schema(title = "狀態", description = "1_啟用, 2_凍結")
    private Byte status;

    private int errorPasswordCount;

    @Schema(title = "註冊ip")
    private String registerIp;

    @Schema(title = "最後登入ip")
    private String lastLoginIp;

    @Schema(title = "最後登入時間")
    private LocalDateTime lastLoginTime;

    @Schema(title = "生日")
    private LocalDate birthday;

    @Schema(title = "所屬代理")
    private String agent;

    @Schema(title = "推薦人")
    private String recommender;

    @Schema(title = "註冊域名")
    private String registerDomain;

    @Schema(title = "登錄次數")
    private Integer loginCount;

    @Schema(title = "裝置識別碼")
    private String deviceCode;

    /**
     * 最後修改真實姓名或手機號的時間，用於風控計算
     */
    private LocalDateTime lastInfoModified;

    @Schema(title = "提款控制",description = "0關閉提款 1開啟提款")
    private Integer withdrawControllerState;

}
