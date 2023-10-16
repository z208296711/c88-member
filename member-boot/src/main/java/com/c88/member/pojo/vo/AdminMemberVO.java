package com.c88.member.pojo.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.c88.common.core.base.BaseEntity;
import com.c88.common.mybatis.handler.StringArrayJsonTypeHandler;
import com.c88.common.web.annotation.Mobile;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(title = "會員")
public class AdminMemberVO extends BaseEntity {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(title = "會員id")
    private Long id;

    @Schema(title = "帳號")
    private String userName;

    @Schema(title = "真實姓名")
    private String realName;

    @Schema(title = "會員等級")
    private String vipLevel;

    @Schema(title = "提款密碼")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String withdrawPassword;

    @Schema(title = "密碼")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Schema(title = "手機號碼")
    @Mobile
    private String mobile;

    @Size(max = 250)
    @Schema(title = "電子郵件")
    private String email;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(title = "餘額")
    private BigDecimal balance;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(title = "狀態", description = "1_啟用, 2_凍結")
    private Byte status;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int errorPasswordCount;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(title = "註冊ip")
    private String registerIp;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(title = "最後登入ip")
    private String lastLoginIp;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(title = "最後登入時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastLoginTime;

    @Schema(title = "生日")
    private LocalDate birthday;

    @Schema(title = "總代理名稱")
    private String masterUsername;

    @Schema(title = "代理上級名稱")
    private String parentUsername;

    @Schema(title = "推薦人")
    private String recommender;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(title = "註冊域名")
    private String registerDomain;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(title = "登錄次數")
    private Integer loginCount;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(title = "裝置識別碼")
    private String deviceCode;

    @Schema(title = "最後備註")
    private String lastRemark;

    @TableField(exist = false)
    @Schema(title = "註冊時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registerTime;

    public LocalDateTime getRegisterTime() {
        return getGmtCreate();
    }

    @TableField(exist = false, typeHandler = StringArrayJsonTypeHandler.class)
    @Schema(title = "會員標籤")
    private String[] tags;

    /**
     * 最後修改真實姓名或手機號的時間，用於風控計算
     */
    private LocalDateTime lastInfoModified;

    @TableField(exist = false)
    @Schema(title = "金幣")
    private Integer coin;

}
