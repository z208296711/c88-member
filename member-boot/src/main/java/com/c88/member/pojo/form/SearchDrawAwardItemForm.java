package com.c88.member.pojo.form;

import com.c88.common.core.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "SearchDrawAwardItemForm")
public class SearchDrawAwardItemForm extends BasePageQuery {

    @Schema(title = "商品名稱")
    private String name;

    @Schema(title = "獎項類型 0:紅利, 1:金幣, 2:實體, 3:存送")
    private Integer awardType;

    @Schema(title = "抽獎類型 0:一般抽獎")
    private Integer drawType;

    @Schema(title = "啟用 0停用 1啟用")
    private Integer enable;

}
