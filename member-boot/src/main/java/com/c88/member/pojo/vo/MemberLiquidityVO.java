package com.c88.member.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title = "累計報表")
public class MemberLiquidityVO {

    @Schema(title = "ID")
    private Long id;

    @Schema(title = "會員ID")
    private Long memberId;

    @Schema(title = "會員帳號")
    private String username;

    @Schema(title = "累計充值")
    private BigDecimal rechargeAmount;

    @Schema(title = "累計充值資數")
    private Integer rechargeCount;

    @Schema(title = "累計提取")
    private BigDecimal withdrawAmount;

    @Schema(title = "累計提取次數")
    private Integer withdrawCount;

    @Schema(title = "累計優惠")
    private BigDecimal promotionAmount;

    @Schema(title = "累計優惠次數")
    private Integer promotionCount;

    @Schema(title = "累計紅利")
    private BigDecimal bonusAmount;

    @Schema(title = "累計紅利次數")
    private Integer bonusCount;

    @Schema(title = "總流水")
    private BigDecimal betAmount;

    @Schema(title = "狀態")
    private Integer status;

    @Schema(title = "會員ID")
    private Long uid;

}
