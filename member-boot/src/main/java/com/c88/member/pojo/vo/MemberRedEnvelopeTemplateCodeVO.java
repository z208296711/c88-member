package com.c88.member.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "白菜紅包模組代碼")
public class MemberRedEnvelopeTemplateCodeVO {

    @Schema(title = "紅包代碼")
    private String code;

    @Schema(title = "領取用戶")
    private String username;

    @Schema(title = "狀態", description = "0未使用 1已使用 2已回收")
    private Integer state;

    @Schema(title = "領取時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime receiveTime;

}
