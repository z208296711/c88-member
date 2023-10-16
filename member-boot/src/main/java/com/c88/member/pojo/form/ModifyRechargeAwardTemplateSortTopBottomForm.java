package com.c88.member.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Data
@Schema(title = "修改置頂至底表單")
public class ModifyRechargeAwardTemplateSortTopBottomForm {

    @Range(min = 0, max = 1, message = "修改排序方式參數錯誤")
    @Schema(title = "修改排序方式", description = "0置頂1置底")
    private Integer sortType;

    @NotNull(message = "ID不得為空")
    @Schema(title = "ID")
    private Integer id;

}
