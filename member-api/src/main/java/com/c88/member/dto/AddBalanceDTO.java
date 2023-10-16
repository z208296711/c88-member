package com.c88.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "修改餘額")
public class AddBalanceDTO {

    @NotNull(message = "會員ID不得為空")
    @Schema(title = "會員ID")
    private Long memberId;

    @NotNull(message = "餘額不得為空")
    @Schema(title = "餘額(平台分數)")
    private BigDecimal balance;

    @NotNull(message = "打碼倍率不得為空")
    @Schema(title = "打碼倍率")
    private BigDecimal betRate;

    @NotNull(message = "帳變類型不得為空")
    @Schema(title = "帳變類型", description = "參考 BalanceChangeTypeEnum")
    private Integer type;

    @Schema(title = "擴展信息")
    private String note;

    // @Schema(title = "存送優惠相關訊息 有存送優惠就必傳 沒有就傳null")
    // private RechargeAwardDTO rechargeAwardDTO;

}
