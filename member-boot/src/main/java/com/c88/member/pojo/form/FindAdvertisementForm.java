package com.c88.member.pojo.form;

import com.c88.common.core.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "找廣告表單")
public class FindAdvertisementForm extends BasePageQuery {

    @Schema(title = "廣告名稱")
    private String name;

    @Schema(title = "狀態", description = "0停用1啟用")
    private Integer enable;

}
