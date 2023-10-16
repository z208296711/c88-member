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
public class H5RouletteVO {

    @Schema(title = "是否需顯示輪盤 true顯示, false disable")
    private Boolean roulette;

    @Schema(title = "當前 免費抽獎間隔時間(秒)")
    private Long freeInterval;

}
