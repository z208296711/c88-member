package com.c88.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "更新餘額及距離提現表單", description = "直接更新會員提款限額")
public class UpdateBalanceAndWithdrawLimitDTO {

    @Schema(title = "會員ID")
    private Long memberId;

    @Schema(title = "餘額(平台分數)")
    private BigDecimal balance;

    @Schema(title = "提款限額")
    private BigDecimal withdrawLimit;

}
