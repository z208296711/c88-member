package com.c88.member.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "會員生日禮金內容")
public class MemberBirthdayGiftVO {

    @Schema(title = "金額")
    private BigDecimal amount;

    @Schema(title = "申請效期")
    private Integer days;

    @Schema(title = "流水要求")
    private Integer exp;

    @Schema(title = "領取狀態", description = "未領取:0, 已領取:1, 尚不符合資格:2")
    private Integer state;

}
