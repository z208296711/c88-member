package com.c88.member.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(title = "存送優惠模組")
public class MemberRechargeAwardTemplateVO {

    @Schema(title = "ID")
    private Long id;

    @Schema(title = "模型名稱")
    private String name;

    @Schema(title = "打碼倍數")
    private BigDecimal betRate;

    @Schema(title = "存送類型", description = "1平台 2個人")
    private Integer type;

    @Schema(title = "存送模式", description = "1比例 2固定")
    private Integer mode;

    @Schema(title = "優惠比例")
    private BigDecimal rate;

    @Schema(title = "贈送金額")
    private BigDecimal amount;

    @Schema(title = "最低參與金額")
    private BigDecimal minJoinAmount;

    @Schema(title = "贈送最高上限")
    private BigDecimal maxAwardAmount;

    @Schema(title = "昨日充值要求")
    private BigDecimal yesterdayRechargeAmount;

    @Schema(title = "上週充值要求")
    private BigDecimal lastWeekRechargeAmount;

    @Schema(title = "累積充值要求")
    private BigDecimal totalRechargeAmount;

    @Schema(title = "累積充值要求 開始時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime totalRechargeStartTime;

    @Schema(title = "累積充值要求 結束時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime totalRechargeEndTime;

    @Schema(title = "每日次數限制")
    private Integer dayNumber;

    @Schema(title = "每週次數限制")
    private Integer weekNumber;

    @Schema(title = "每月次數限制")
    private Integer monthNumber;

    @Schema(title = "累積次數限制")
    private Integer totalNumber;

    @Schema(title = "驗證手機要求", description = "0否1是")
    private Integer validMobile;

    @Schema(title = "拒絕關聯帳戶", description = "0否1是")
    private Integer validLink;

    @Schema(title = "需綁提款方式", description = "0否1是")
    private Integer validWithdraw;

    @Schema(title = "可用會員等級")
    private List<Integer> vipIds;

    @Schema(title = "限制會員標籤")
    private List<Integer> tagIds;

    @Schema(title = "限制支付類型")
    private List<Integer> rechargeTypeIds;

    @Schema(title = "限制註冊 開始時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registerStartTime;

    @Schema(title = "限制註冊 結束時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registerEndTime;

    @Schema(title = "活動開始時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(title = "活動結束時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(title = "關聯模式", description = "0無 1水平 2垂直")
    private Integer linkMode;

    @Schema(title = "關聯模式垂直排序")
    private Integer linkSort;

    @Schema(title = "啟用狀態", description = "0停用 1啟用")
    private Integer enable;

    @Schema(title = "排序")
    private Integer sort;
}
