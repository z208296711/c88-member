package com.c88.member.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(title = "存送優惠領取紀錄")
public class MemberRechargeAwardRecordVO {

    @Schema(title = "會員ID")
    private Long memberId;

    @Schema(title = "會員帳號")
    private String username;

    @Schema(title = "優惠名稱")
    private String name;

    @Schema(title = "存送類型", description = "1平台 2個人")
    private Integer type;

    @Schema(title = "存送模式", description = "1比例 2固定")
    private Integer mode;

    @Schema(title = "優惠比例")
    private BigDecimal rate;

    @Schema(title = "打碼倍數")
    private BigDecimal betRate;

    @Schema(title = "充值金額")
    private BigDecimal rechargeAmount;

    @Schema(title = "贈送金額")
    private BigDecimal amount;

    @Schema(title = "使用時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime useTime;

    @Schema(title = "獲得時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtCreate;

}
