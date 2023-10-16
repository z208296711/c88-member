package com.c88.member.pojo.form;

import com.c88.common.core.vo.AssignPlatformGameVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Data
@Schema(title = "新增白菜紅包模組表單")
public class AddRedEnvelopeTemplateForm {

    @NotNull(message = "模型名稱不得為空")
    @Schema(title = "模型名稱")
    private String name;

    @NotNull(message = "白菜活動類型不得為空")
    @Schema(title = "白菜活動類型", description = "1白菜紅包 2紅包代碼")
    private Integer type;

    @NotNull(message = "打碼倍數不得為空")
    @Schema(title = "打碼倍數")
    private BigDecimal betRate;

    @Schema(title = "遊戲平台類型ID", description = "空表示全平台")
    private List<AssignPlatformGameVO> assignPlatformGame;

    @Schema(title = "紅包單日庫存總數")
    private Integer dailyRedEnvelopeTotal;

    @Schema(title = "紅包累積庫存總數")
    private Integer redEnvelopeTotal;

    @Schema(title = "每人每日領取上限")
    private Integer dailyMaxTotal;

    @Schema(title = "每人累計領取上限")
    private Integer maxTotal;

    @Schema(title = "昨日充值要求")
    private BigDecimal yesterdayRechargeAmount;

    @Schema(title = "當日充值要求")
    private BigDecimal dayRechargeAmount;

    @Schema(title = "累積充值要求")
    private BigDecimal totalRechargeAmount;

    @Schema(title = "累積充值要求 開始時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime totalRechargeStartTime;

    @Schema(title = "累積充值要求 結束時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime totalRechargeEndTime;

    @NotNull(message = "驗證手機要求不得為空")
    @Schema(title = "驗證手機要求", description = "0否1是")
    private Integer validatedMobile;

    @NotNull(message = "拒絕關聯帳戶不得為空")
    @Schema(title = "拒絕關聯帳戶", description = "0否1是")
    private Integer validatedLink;

    @NotNull(message = "需綁提款方式不得為空")
    @Schema(title = "需綁提款方式", description = "0否1是")
    private Integer validatedWithdraw;

    @NotNull(message = "不得為空")
    @Schema(title = "可用會員等級")
    private List<Integer> vipIds;

    @Schema(title = "限制會員標籤")
    private List<Integer> tagIds;

    @Schema(title = "限制註冊 開始時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registerStartTime;

    @Schema(title = "限制註冊 結束時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registerEndTime;

    @NotNull(message = "活動開始時間不得為空")
    @Schema(title = "活動開始時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @NotNull(message = "活動結束時間不得為空")
    @Schema(title = "活動結束時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(title = "活動紅包金額")
    private BigDecimal amount;

    @Schema(title = "活動代碼個數")
    private Integer number;

    @Schema(title = "領取條件")
    private List<AddMemberRedEnvelopeTemplateConditionForm> addMemberRedEnvelopeTemplateConditionForms = Collections.emptyList();

}
