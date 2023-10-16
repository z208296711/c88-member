package com.c88.member.pojo.form;

import com.c88.common.core.base.BasePageQuery;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Schema(title = "找存送優惠領取紀錄表單")
public class FindRechargeAwardRecordForm extends BasePageQuery {

    @Parameter(description = "開始時間")
    @Schema(title = "開始時間")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Parameter(description = "結束時間")
    @Schema(title = "結束時間")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Parameter(description = "存送類型 1平台 2個人")
    @Schema(title = "存送類型", description = "1平台 2個人")
    private Integer type;

    @Parameter(description = "會員帳號")
    @Schema(title = "會員帳號")
    private String username;

    @Parameter(description = "優惠名稱")
    @Schema(title = "優惠名稱")
    private String name;

}
