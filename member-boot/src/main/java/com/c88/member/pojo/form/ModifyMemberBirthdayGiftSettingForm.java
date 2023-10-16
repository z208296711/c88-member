package com.c88.member.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title = "修改生日禮金表單")
public class ModifyMemberBirthdayGiftSettingForm {

    @Schema(title = "站內信件", description = "0停用 1啟用")
    private Integer inbox;

    @Schema(title = "領取效期")
    private Integer days;

    @Schema(title = "打碼倍數")
    private BigDecimal betRate;

}
