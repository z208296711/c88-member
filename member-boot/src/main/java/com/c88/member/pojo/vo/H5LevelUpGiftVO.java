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
public class H5LevelUpGiftVO {

    @Schema(title = "vipId")
    private Integer vipId;

    @Schema(title = "vipName名稱")
    private String vipName;

    @Schema(title = "玩家當前 vipName")
    private String currentVipName;

    @Schema(title = "晉級禮金")
    private Integer levelUpGift;

    @Schema(title = "流水要求")
    private String exp;

    @Schema(title = "未領取:0, 已領取:1, 尚不符合資格:2")
    private Integer state;

}
