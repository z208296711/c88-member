package com.c88.member.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Schema(title = "AdminMemberDrawRecordVO", description = "輪盤領取紀錄")
public class AdminMemberDrawRecordVO {

    @Schema(title = "id")
    private Long id;

    @Schema(title = "會員Id")
    private Long memberId;

    @Schema(title = "會員帳號")
    private String username;

    @Schema(title = "抽獎類型 0:一般抽獎")
    private Integer drawType;

    @Schema(title = "結果")
    private String awardName;

    @Schema(title = "獎項類型 0未中獎, 1:紅利, 2:金幣, 3:實體, 4:存送優惠")
    private Integer awardType;

    @Schema(title = "獎勵金額")
    private BigDecimal amount;

    @Schema(title = "流水倍數")
    private BigDecimal betRate;

    @Schema(title = "審核時間")
    private LocalDateTime approveTime;

    @Schema(title = "狀態")
    private Integer state;

    @Schema(title = "phone")
    private String phone;

    @Schema(title = "address")
    private String address;

    @Schema(title = "兌換時間")
    private LocalDateTime gmtCreate;
}