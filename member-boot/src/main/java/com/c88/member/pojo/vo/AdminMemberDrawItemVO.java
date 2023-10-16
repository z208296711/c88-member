package com.c88.member.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(title = "AdminMemberDrawTimesVO")
public class AdminMemberDrawItemVO {

    @Schema(title = "獎項名稱")
    private String awardName;

    @Schema(title = "建立時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtCreate;


}