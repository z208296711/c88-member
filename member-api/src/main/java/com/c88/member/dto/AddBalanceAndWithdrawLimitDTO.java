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
@Schema(title = "更新餘額及距離提現表單", description = "金額會以原本的值增加")
public class AddBalanceAndWithdrawLimitDTO {

    @Schema(title = "會員ID")
    private Long memberId;

    @Schema(title = "餘額(平台分數)")
    private BigDecimal balance;

    @Schema(title = "帳變類型", description = "參考 BalanceChangeTypeEnum")
    private Integer type;

    @Schema(title = "擴展信息")
    private String note;

    @Schema(title = "提款類型相關")
    private WithdrawLimitTypeDTO withdrawLimitType;

    // @Schema(title = "存送優惠")
    // private RechargeAwardDTO rechargeAwardDTO;

}
