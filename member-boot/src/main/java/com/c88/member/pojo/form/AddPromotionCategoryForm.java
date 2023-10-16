package com.c88.member.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(title = "新增優惠活動分類表單")
public class AddPromotionCategoryForm {

    @NotNull(message = "活動分類名稱不得為空")
    @Schema(title = "活動分類名稱")
    public String name;

}
