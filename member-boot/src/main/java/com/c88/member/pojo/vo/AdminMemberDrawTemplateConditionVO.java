package com.c88.member.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;


@Data
public class AdminMemberDrawTemplateConditionVO {

    @Schema(title = "id")
    private Integer id;

    @Schema(title = "模型ID")
    private Integer templateId;

    @Schema(title = "階層")
    private Integer level;

    @Schema(title = "項目 0:免費, 1:金幣 2:累積充值 3:單筆充值")
    private Integer type;

    @Schema(title = "條件")
    private BigDecimal conditionNum;

    @Schema(title = "上限次數")
    private Integer maxNum;

}