package com.c88.member.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(title = "生日禮金")
public class MemberBirthdayGiftRecordVO {

    @Schema(title = "會員ID")
    private Long memberId;

    @Schema(title = "會員帳號")
    private String username;

    @Schema(title = "生日")
    private LocalDate birthday;

    @Schema(title = "VIP等級名稱")
    private String vipName;

    @Schema(title = "獎勵金額")
    private BigDecimal amount;

    @Schema(title = "打碼倍率")
    private BigDecimal betRate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(title = "領取時間")
    private LocalDateTime gmtCreate;

}
