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
@Schema(title = "AdminTodayDrawTimesVO")
public class AdminTodayDrawVO {

    @Schema(title = "剩餘抽獎次數")
    private Integer todayFreeDrawRemainCount;

    @Schema(title = "該玩家 該抽獎模型 共可抽獎次數 為無限制:-1 ")
    private Integer drawCount;

}