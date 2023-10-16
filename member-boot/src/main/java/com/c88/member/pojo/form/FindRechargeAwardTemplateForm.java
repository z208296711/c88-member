package com.c88.member.pojo.form;

import com.c88.common.core.base.BasePageQuery;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "找存送優惠模組表單")
public class FindRechargeAwardTemplateForm extends BasePageQuery {

    @Parameter(description = "優惠名稱")
    @Schema(title = "優惠名稱")
    private String name;

    @Parameter(description = "啟用狀態 0停用 1啟用")
    @Schema(title = "啟用狀態", description = "0停用 1啟用")
    private Integer enable;

}
