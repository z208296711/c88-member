package com.c88.member.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "關聯模式組合")
public class RechargeAwardTemplateLinkModeVO {

    @Schema(title = "關聯模式",description = "0無 1水平 2垂直")
    private Integer linkMode;

    @Schema(title = "模組名稱")
    private String name;

    @Schema(title = "關聯模式排序")
    private Integer linkSort;

}
