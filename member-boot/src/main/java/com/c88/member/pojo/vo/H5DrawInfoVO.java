package com.c88.member.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class H5DrawInfoVO {

    @Schema(title = "下次要抽獎的所使用的條件, 0免費 1單筆充值 2累計充值 3金幣兌換 若type =3 為金幣兌換 需 前台顯示詢問用戶是否使用積分進行抽獎。")
    private Integer nextDrawConditionType;

    @Schema(title = "今日抽獎次數")
    private Integer todayDrawCount;

    @Schema(title = "免費抽獎剩餘次數")
    private Integer freeRemainDrawCount;

    @Schema(title = "金幣餘額")
    private Integer coin;

    @Schema(title = "金幣餘額, 若為null 則顯示 '目前暫無使用金幣兌換'")
    private Integer exchangeCoin;

    @Schema(title = "抽獎要求-昨日充值要求")
    private BigDecimal yesterdayRechargeAmount;

    @Schema(title = "抽獎要求-昨日流水要求")
    private BigDecimal yesterdayBetAmount;

    @Schema(title = "抽獎要求-昨日會員充值")
    private BigDecimal yesterdayMemberRechargeAmount;

    @Schema(title = "抽獎要求-昨日會員投注")
    private BigDecimal yesterdayMemberBetAmount;

    @Schema(title = "重置時間 實際使用 +0時區 前台須轉換（'HH:mm')", description = "HH:mm", example = "20:20")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime resetTime;

    @Schema(title = "目前抽獎活動 需要的條件")
    private List<H5DrawConditionVO> conditions;

    @Schema(title = "獎項清單(for 轉盤)")
    private List<H5DrawItemVO> drawItemList;

}
