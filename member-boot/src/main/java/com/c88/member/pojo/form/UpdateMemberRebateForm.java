package com.c88.member.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@Schema(title = "修改返點")
public class UpdateMemberRebateForm implements Serializable {
    /**
     * 
     */
    @Schema(title = "返點id")
    @NotNull
    @Valid
    private Long id;

    /**
     * 
     */
    @Schema(title = "vip等級")
    @NotNull
    private Integer vipId;

    /**
     * 
     */
    @Schema(title = "遊戲分類")
    @NotNull
    private Integer categoryId;

    /**
     * 
     */

    private LocalDateTime gmtCreate;

    /**
     * 
     */

    private LocalDateTime gmtModified;

    /**
     * 
     */
    @Schema(title = "返點值")
    @DecimalMin(value = "0.0"   ,message = "不得小於0")
    @DecimalMax(value = "100.0" ,message = "不得大於100")
    private Double rebate;

}
