package com.c88.member.pojo.form;

import com.c88.common.core.base.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(title = "白菜紅包模組代碼")
public class FindRedEnvelopeTemplateCodeForm extends BasePageQuery {

    @NotNull(message = "模組ID不得為空")
    @Schema(title = "模組ID")
    private Long id;

    @Schema(title = "紅包代碼")
    private String code;

    @Schema(title = "用戶名")
    private String username;

    @Schema(title = "狀態", description = "0未使用 1已領取 2已回收")
    private Integer state;
}
