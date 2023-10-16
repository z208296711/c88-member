package com.c88.member.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MemberVipRuleVO {

    @Schema(title = "id")
    private Integer id;

    @Schema(title = "內容")
    private String content;

}
