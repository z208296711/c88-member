package com.c88.member.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Schema(title = "會員")
public class MemberVO {

    @Schema(title = "帳號")
    private String userName;

    @Schema(title = "真實姓名")
    private String realName;

    @Schema(title = "手機號碼")
    private String mobile;

    @Schema(title = "電子郵件")
    private String email;

    @Schema(title = "生日")
    private LocalDate birthday;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtCreate;

    @Schema(title = "會員-當前充值")
    private BigDecimal recharge = BigDecimal.ZERO;

    @Schema(title = "會員-累計流水")
    private BigDecimal exp = BigDecimal.ZERO;

    @Schema(title = "vip 相關資訊")
    public VipDetailVO vipDetailVO;
}
