package com.c88.member.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title = "存送優惠輪盤選單")
public class MemberRechargeAwardTemplateDrawOptionVO {

    @Schema(title = "ID")
    private Long id;

    @Schema(title = "模型名稱")
    private String name;

    @Schema(title = "打碼倍數")
    private BigDecimal betRate;

    @Schema(title = "存送模式", description = "1比例 2固定")
    private Integer mode;

    @Schema(title = "優惠比例")
    private BigDecimal rate;

    @Schema(title = "贈送金額")
    private BigDecimal amount;

    @Schema(title = "啟用狀態", description = "0停用 1啟用")
    private Integer enable;

    @Schema(title = "最低參與金額")
    private BigDecimal minJoinAmount;

    @Schema(title = "贈送最高上限")
    private BigDecimal maxAwardAmount;
}
