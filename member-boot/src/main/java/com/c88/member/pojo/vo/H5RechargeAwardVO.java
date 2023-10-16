package com.c88.member.pojo.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "存送優惠Option")
public class H5RechargeAwardVO {

    @Schema(title = "存送優惠ID")
    private Integer id;

    @Schema(title = "存送優惠名稱")
    private String name;

    @Schema(title = "打碼倍數")
    private BigDecimal betRate;

    @Schema(title = "存送模式", description = "1比例 2固定")
    private Integer mode;

    @Schema(title = "優惠比例")
    private BigDecimal rate;

    @Schema(title = "贈送金額")
    private BigDecimal amount;

    @Schema(title = "最低參與金額")
    private BigDecimal minJoinAmount;

    @Schema(title = "贈送最高上限")
    private BigDecimal maxAwardAmount;

    @JsonIgnore
    private List<Integer> rechargeTypeIds;

}
