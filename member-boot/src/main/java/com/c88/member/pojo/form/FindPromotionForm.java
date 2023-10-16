package com.c88.member.pojo.form;

import com.c88.common.core.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "找優惠活動表單")
public class FindPromotionForm extends BasePageQuery {

    @Schema(title = "活動標題")
    private String title;

    @Schema(title = "活動分類ID")
    private Integer categoryId;

}
