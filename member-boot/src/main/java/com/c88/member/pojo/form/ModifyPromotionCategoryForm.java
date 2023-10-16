package com.c88.member.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(title = "修改優惠活動分類表單")
public class ModifyPromotionCategoryForm {

    @NotNull(message = "優惠活動分類ID不得為空")
    @Schema(title = "優惠活動分類ID")
    private Integer id;

    @NotNull(message = "優惠活動分類名稱不得為空")
    @Schema(title = "優惠活動分類名稱")
    private String name;

}
