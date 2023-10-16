package com.c88.member.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(title = "晉級禮金")
public class MemberLevelUpGiftRecordVO {

    @Schema(title = "會員ID")
    private Long memberId;

    @Schema(title = "會員帳號")
    private String username;

    @Schema(title = "VIP等級ID")
    private String vipId;

    @Schema(title = "VIP等級名稱")
    private String vipName;

    @Schema(title = "獎勵金額")
    private BigDecimal amount;

    @Schema(title = "打碼倍率")
    private Integer betRate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title = "領取時間")
    private LocalDateTime gmtCreate;

}
