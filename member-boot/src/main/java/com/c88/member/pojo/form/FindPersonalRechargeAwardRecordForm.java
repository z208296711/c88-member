package com.c88.member.pojo.form;

import com.c88.common.core.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Schema(title = "找個人存送優惠表單")
public class FindPersonalRechargeAwardRecordForm extends BasePageQuery {

    @Schema(title = "開始時間")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(title = "結束時間")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(title = "狀態", description = "-1無 0未使用 1已使用 2已取消")
    private Integer state;

    @Schema(title = "會員帳號")
    private String username;

    @Schema(title = "優惠名稱")
    private String name;
}
