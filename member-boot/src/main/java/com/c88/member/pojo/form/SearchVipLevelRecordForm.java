package com.c88.member.pojo.form;

import com.c88.common.core.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "SearchVipLevelRecordForm")
public class SearchVipLevelRecordForm extends BasePageQuery {

    @Schema(title = "會員帳號")
    private String username;

    @Schema(title = "類型")
    private Integer type;

    @Schema(title = "VIP等級ID")
    private Integer vipId;

    @Schema(title = "狀態 0:待審核 1:已保級 2:已降級 3:已升級")
    private Integer state;
}
