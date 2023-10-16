package com.c88.member.pojo.form;

import com.c88.common.core.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "SearchMemberVipLevelRecordForm")
public class SearchMemberVipLevelRecordForm extends BasePageQuery {

    @Schema(title = "開始時間")
    private String startTime;

    @Schema(title = "結束時間")
    private String endTime;

}
