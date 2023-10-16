package com.c88.member.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 輪盤模組
 *
 * @TableName member_draw_template
 */
@Data
public class AdminMemberDrawTemplateVO {

    @Schema(title = "id")
    private Integer id;

    @Schema(title = "模型名稱")
    private String name;

    @Schema(title = "抽獎類型ID 0:一般抽獎")
    private Integer drawType;

    @Schema(title = "昨日充值要求")
    private BigDecimal yesterdayRechargeAmount;

    @Schema(title = "昨日流水要求")
    private BigDecimal yesterdayBetAmount;

    @Schema(title = "獎項")
    private Integer awardCount;

    @Schema(title = "免費抽獎時間間隔(統一轉換為-秒)")
    private Integer freeInterval;

    @Schema(title = "每日抽獎次數限制 -1=無限")
    private Integer dailyGiftNumber;

    @Schema(title = "重置時間")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime resetTime;

    @Schema(title = "開始時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(title = "結束時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(title = "啟用 0停用 1啟用")
    private Integer enable;

    @Schema(title = "抽獎條件")
    private List<AdminMemberDrawTemplateConditionVO> conditions;

}