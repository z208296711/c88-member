package com.c88.member.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "存送優惠")
public class H5RechargeAwardCategoryVO {

    @Schema(title = "充值類型ID")
    private Integer rechargeTypeId;

    @Schema(title = "存送優惠Option")
    private List<H5RechargeAwardVO> rechargeAwards;

}
