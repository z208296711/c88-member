package com.c88.member.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "優惠活動排序")
public class MemberPromotionSortVO {

    @Schema(title = "優惠活動ID")
    private Integer id;

    @Schema(title = "活動標題")
    private String title;

    @Schema(title = "分類ID")
    private Integer categoryId;

    @Schema(title = "分類名稱")
    private String categoryName;

    @Schema(title = "排序")
    private Integer sort;

}
