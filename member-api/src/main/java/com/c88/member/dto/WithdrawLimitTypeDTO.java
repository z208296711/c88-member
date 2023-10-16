package com.c88.member.dto;

import com.c88.common.core.enums.WithdrawLimitTypeEnum;
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
public class WithdrawLimitTypeDTO {

    @Schema(title = "獎勵金額")
    private BigDecimal rewardAmount = BigDecimal.ZERO;

    @Schema(title = "打碼倍率")
    private BigDecimal bateRate = BigDecimal.ZERO;

    /**
     * @see WithdrawLimitTypeEnum
     */
    @Schema(title = "提款限額類型", example = "0")
    private Integer type = 0;

}
