package com.c88.member.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDrawVO{

    private Long id;

    @Schema(title = "模型ID")
    private Long templateId;

    @Schema(title = "抽獎類型")
    private Integer drawType;

    @Schema(title = "獎項ID")
    private Long awardId;

    @Schema(title = "累積中獎次數")
    private Integer prizeCount;

    @Schema(title = "今日中獎次數")
    private Integer prizeToday;

    @Schema(title = "機率")
    private Integer percent;

    @Schema(title = "排序")
    private Integer sort;

    @Schema(title = "是否啟用 0否 1是")
    private Integer enable;

    @Schema(title = "名稱")
    private String itemName;

    @Schema(title = "是否啟用")
    private Integer itemEnable;

    @Schema(title = "獎項類型 0:未中獎 1:紅利, 2:金幣, 3:實體, 4:存送")
    private Integer awardType;

    @Schema(title = "每日限量 -1=無限")
    private Integer dailyNumber;

    @Schema(title = "全部限量 -1=無限")
    private Integer totalNumber;

}
