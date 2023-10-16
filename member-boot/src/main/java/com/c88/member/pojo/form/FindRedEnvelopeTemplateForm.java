package com.c88.member.pojo.form;

import com.c88.common.core.base.BasePageQuery;
import com.c88.member.common.enums.RedEnvelopeTemplateTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "找白菜紅包模組表單")
public class FindRedEnvelopeTemplateForm extends BasePageQuery {

    @Schema(title = "模組名稱")
    private String name;

    /**
     * @see RedEnvelopeTemplateTypeEnum
     */
    @Schema(title = "白菜類型", description = "1白菜紅包 2紅包代碼")
    private Integer type;

    @Schema(title = "啟用狀態", description = "0停用 1啟用")
    private Integer enable;


}
