package com.c88.member.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(title = "MemberSessionVO")
public class MemberSessionVO {

    @Schema(title = "登錄域名")
    private String loginDomain;

    @Schema(title = "登錄時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loginTime;

    @Schema(title = "登錄IP/地區")
    private String ipArea;
}