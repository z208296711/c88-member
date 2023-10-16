package com.c88.member.pojo.form;

import com.c88.common.core.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "SearchTodayDrawRecordForm")
public class SearchTodayDrawRecordForm extends BasePageQuery {

    @Schema(title = "會員帳號")
    private String username;

    @Schema(title = "抽獎模型Id")
    private Integer templateId;

}
