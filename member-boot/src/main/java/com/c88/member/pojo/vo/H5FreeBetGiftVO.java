package com.c88.member.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class H5FreeBetGiftVO {

    @Schema(title = "領取週期", description = "1:週, 2:月")
    private Integer type;

    @Schema(title = "玩家當前VIP名稱")
    private String currentVipName;

    @Schema(title = "流水要求")
    private String exp;

    @Schema(title = "(當月/當週)免費籌碼")
    private BigDecimal amount;

    @Schema(title = "(當月/當週)充值條件")
    private BigDecimal rechargeCondition;

    @Schema(title = "領取狀態", description = "未領取:0, 已領取:1, 尚不符合資格:2")
    private Integer state;

}
