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
@Schema(title = "生日禮金設定")
public class MemberBirthdayGiftSettingVO {

    @Schema(title = "是否需要寄站內信")
    private Integer inbox;

    @Schema(title = "領取效期")
    private Integer days;

    @Schema(title = "打碼倍數")
    private BigDecimal betRate;
}
