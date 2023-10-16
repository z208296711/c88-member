package com.c88.member.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(title = "領取白菜紅包表單")
public class ReceiveChineseCabbageForm {

    @NotNull(message = "白菜紅包模組ID不得為空")
    @Schema(title = "白菜紅包模組ID")
    private Long id;

}
