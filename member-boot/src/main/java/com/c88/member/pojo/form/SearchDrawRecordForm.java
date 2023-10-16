package com.c88.member.pojo.form;

import com.c88.common.core.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "SearchDrawRecordForm")
public class SearchDrawRecordForm extends BasePageQuery {

    @Schema(title = "會員帳號")
    private String username;

    @Schema(title = "0兌換時間/1審核時間")
    private Integer searchType = 0;

    @Schema(title = "開始時間")
    private String startTime;

    @Schema(title = "結束時間")
    private String endTime;

    @Schema(title = "獎項類型  0:未中獎, 1:紅利 2:金幣 3:實體 4:存送優惠")
    private Integer awardType;

    @Schema(title = "狀態 0:待審核 1:已保級 2:已降級 3:已升級")
    private Integer state;
}
