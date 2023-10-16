package com.c88.member.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Schema(title = "AdminMemberLoginReportVO")
public class AdminMemberLoginReportVO {

    @Schema(title = "日期")
    private LocalDate date;

    @Schema(title = "登錄總人數")
    private Integer distinctLogin;

    @Schema(title = "PC 登錄總人數")
    private Integer distinctPCLogin;

    @Schema(title = "Android 登錄總人數")
    private Integer distinctAndroidLogin;

    @Schema(title = "iOS 登錄總人數")
    private Integer distinctIosLogin;

    @Schema(title = "H5 登錄總人數")
    private Integer distinctH5Login;

    @Schema(title = "登錄總人次")
    private Integer totalLogin;

    @Schema(title = "PC 登錄總人次")
    private Integer totalPCLogin;

    @Schema(title = "Android 登錄總人次")
    private Integer totalAndroidLogin;

    @Schema(title = "iOS 登錄總人次")
    private Integer totalIosLogin;

    @Schema(title = "H5 登錄總人次")
    private Integer totalH5Login;

    public AdminMemberLoginReportVO(LocalDate date) {
        this.date = date;
        this.distinctLogin = 0;
        this.distinctPCLogin = 0;
        this.distinctAndroidLogin = 0;
        this.distinctIosLogin = 0;
        this.distinctH5Login = 0;
        this.totalLogin = 0;
        this.totalPCLogin = 0;
        this.totalAndroidLogin = 0;
        this.totalIosLogin = 0;
        this.totalH5Login = 0;
    }
}