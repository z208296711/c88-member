package com.c88.member.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class MemberDetailVipRecordVO {

    @Schema(title = "升/降級時間", description = "pattern: yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime levelChangeTime;

    @Schema(title = "原等級")
    private String originLevel;

    @Schema(title = "現等級")
    private String targetLevel;

    @Schema(title = "累積流水")
    private BigDecimal exp;

    @Schema(title = "累積充值")
    private BigDecimal recharge;

    @Schema(title = "審核時間", description = "pattern: yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approveTime;

    @Schema(title = "審核者")
    private String approveBy;
}
