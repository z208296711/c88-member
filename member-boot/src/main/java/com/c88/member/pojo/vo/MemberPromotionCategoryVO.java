package com.c88.member.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "活動類型")
public class MemberPromotionCategoryVO {

    @Schema(title = "id")
    private Integer id;

    @Schema(title = "名稱")
    private String name;

    @Schema(title = "排序")
    private Integer sort;
}
