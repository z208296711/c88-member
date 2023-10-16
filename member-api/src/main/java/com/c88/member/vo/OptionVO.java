package com.c88.member.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Select选择器默认Option属性
 *
 * @author haoxr
 * @date 2022/1/22
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "Select选择器默认Option属性")
@ApiModel("Select选择器默认Option属性")
public class OptionVO<T> {

    public OptionVO(T value, String label) {
        this.value = value;
        this.label = label;
    }

    @Schema(title = "选项的值")
    @ApiModelProperty("选项的值")
    private T value;

    @Schema(title = "选项的标签，若不设置则默认与value相同")
    @ApiModelProperty("选项的标签，若不设置则默认与value相同")
    private String label;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private List<OptionVO<T>> children;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @Schema(title = "是否禁用该选项，默认false")
    @ApiModelProperty("是否禁用该选项，默认false")
    public Boolean disabled;

}