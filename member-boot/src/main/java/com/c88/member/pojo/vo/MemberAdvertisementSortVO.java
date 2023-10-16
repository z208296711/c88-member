package com.c88.member.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "廣告排序")
public class MemberAdvertisementSortVO {

    @Schema(title = "廣告ID")
    private Integer id;

    @Schema(title = "廣告名稱")
    private String name;

    @Schema(title = "排序")
    private Integer sort;

}
