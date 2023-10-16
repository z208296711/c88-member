package com.c88.member.pojo.form;

import com.c88.common.core.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "SearchDrawRecordForm")
public class SearchDrawTemplateForm extends BasePageQuery {

    @Schema(title = "模型名稱")
    private String name;

    @Schema(title = "0 一般抽獎")
    private Integer drawType = 0;

    @Schema(title = "啟用 0停用 1啟用")
    private Integer enable;

}
