package com.c88.member.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VipCenterVO {

    @Schema(title = "會員-當前充值")
    private BigDecimal recharge = BigDecimal.ZERO;

    @Schema(title = "會員-累計流水")
    private BigDecimal exp = BigDecimal.ZERO;

    @Schema(title = "會員當前VIP-ID")
    private Integer vipId;

    @Schema(title = "會員當前VIP-ID")
    private String vipName;

    @Schema(title = "會員等級-規則條款")
    private String rule;

    @Schema(title = "VIP各級詳細資訊")
    private List<VipDetailVO> vipList;

}
