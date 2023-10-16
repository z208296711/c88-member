package com.c88.member.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(title = "個人存送優惠")
public class AllMemberPersonalRechargeAwardRecordByTemplateIdVO {

    @Schema(title = "ID")
    private Long id;

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

    @Schema(title = "贈送金額")
    private BigDecimal amount;

    @Schema(title = "來源")
    private String source;

    @Schema(title = "使用時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime useTime;

    @Schema(title = "取消時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime cancelTime;

    @Schema(title = "狀態", description = "-1無 0未使用 1已使用 2已取消")
    private Integer state;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title = "獲得時間")
    private LocalDateTime gmtCreate;

}
