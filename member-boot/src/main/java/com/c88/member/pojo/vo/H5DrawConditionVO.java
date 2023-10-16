package com.c88.member.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class H5DrawConditionVO {


    @Schema(title = "0: 免費獲得, 1:單筆充值, 2:累積充值, 3:金幣兌換")
    private Integer type;

    @Schema(title = "條件")
    private Integer conditionNum;

    @Schema(title = "上限次數")
    private Integer maxNum;

}
