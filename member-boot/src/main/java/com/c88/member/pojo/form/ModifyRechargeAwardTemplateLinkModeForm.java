package com.c88.member.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
@Schema(title = "修改存送優惠模組關聯模式表單")
public class ModifyRechargeAwardTemplateLinkModeForm {

    @Schema(title = "關聯模式",description = "0無 1水平 2垂直")
    private Integer mode;

    @Schema(title = "K=模組ID V=排序號")
    private Map<Long, Integer> templateIdToSort;

}
