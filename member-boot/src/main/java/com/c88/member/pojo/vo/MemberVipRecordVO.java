package com.c88.member.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MemberVipRecordVO {

    @Schema(title = "id")
    private Long id;

    @Schema(title = "會員-帳號")
    private String username;

    @Schema(title = "會員-id")
    private Long memberId;

    @Schema(title = "類型 0:降級 1:升級 2:手動")
    private Integer type;

    @Schema(title = "原等級")
    private String originLevel;

    @Schema(title = "目標等級")
    private String targetLevel;

    @Schema(title = "目標充值金額")
    private BigDecimal targetRecharge;

    @Schema(title = "目標經驗值")
    private BigDecimal targetExp;

    @Schema(title = "原vip維持條件-充值金額")
    private BigDecimal keepRecharge;

    @Schema(title = "原vip維持條件-累計流水")
    private BigDecimal keepExp;

    @Schema(title = "累積流水")
    private BigDecimal exp;

    @Schema(title = "累積充值")
    private BigDecimal recharge;

    @Schema(title = "升級類型 1:自動, 2手動(SVIP)")
    private Integer levelUpMode;

    @Schema(title = "審核時間", description = "pattern: yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approveTime;

    @Schema(title = "升/降級時間", description = "pattern: yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime levelChangeTime;

    @Schema(title = "資料建立時間", description = "pattern: yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtCreate;

    @Schema(title = "審核者")
    private String approveBy;

    @Schema(title = "狀態 0:待審核 1:已保級 2:已降級 3:已升級")
    private Integer state;
}
