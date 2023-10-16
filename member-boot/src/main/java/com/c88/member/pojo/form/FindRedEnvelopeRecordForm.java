package com.c88.member.pojo.form;

import com.c88.common.core.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Schema(title = "找白菜紅包紀錄表單")
public class FindRedEnvelopeRecordForm extends BasePageQuery {

    @Schema(title = "開始時間")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(title = "結束時間")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(title = "搜尋時間條件", description = "1申請時間 2審核時間", example = "1")
    private Integer timeType = 1;

    @Schema(title = "帳號")
    private String username;

    @Schema(title = "狀態", description = "1待審核 2已通過 3已拒絕")
    private Integer state;

}
