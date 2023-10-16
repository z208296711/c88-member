package com.c88.member.pojo.form;

import com.c88.common.core.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "GetLevelUpGiftForm")
public class GetLevelUpGiftForm extends BasePageQuery {

    @Schema(title = "username")
    private String username;

    @Schema(title = "會員等級ID")
    private String vipId;

    @Schema(title = "開始時間(格式：yyyy-mm-ss hh:mm:ss)")
    private String beginTime;

    @Schema(title = "結束時間(格式：yyyy-mm-ss hh:mm:ss)")
    private String endTime;

}
