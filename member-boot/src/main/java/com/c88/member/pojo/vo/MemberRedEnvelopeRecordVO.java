package com.c88.member.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(title = "找白菜紅包紀錄")
public class MemberRedEnvelopeRecordVO {

    @Schema(title = "ID")
    private Long id;

    @Schema(title = "會員ID")
    private Long memberId;

    @Schema(title = "會員帳號")
    private String username;

    @Schema(title = "活動類型")
    private Integer type;

    @Schema(title = "活動名稱")
    private String name;

    @Schema(title = "白菜金額")
    private BigDecimal amount;

    @Schema(title = "紅包代碼")
    private String code;

    @Schema(title = "模組ID")
    private Long templateId;

    @Schema(title = "階層")
    private Integer level;

    @Schema(title = "輪數")
    private Integer cycle;

    @Schema(title = "狀態", description = "1待審核 2已通過 3已拒絕")
    private Integer state;

    @Schema(title = "審核時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reviewTime;

    @Schema(title = "申請時間")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtCreate;

}
