package com.c88.member.pojo.query;


import com.c88.common.core.base.BasePageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@ApiModel
public class UserPageQuery extends BasePageQuery {

    @Schema(title = "关键字(用户名、昵称、手机号)")
    private String keywords;

    @Schema(title = "用户状态")
    private Integer status;

    @Schema(title = "部门ID")
    private Long deptId;

}
