package com.c88.member.pojo.form;

import com.c88.common.core.base.BasePageQuery;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Schema(title = "查找玩家登入記錄")
public class FindMemberLoginRecordForm extends BasePageQuery {

    @Schema(title = "會員帳號")
    private String userName;

    @Schema(title = "開始時間(格式：yyyy-mm-ss hh:mm:ss)")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginTime;

    @Schema(title = "結束時間(格式：yyyy-mm-ss hh:mm:ss)")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}