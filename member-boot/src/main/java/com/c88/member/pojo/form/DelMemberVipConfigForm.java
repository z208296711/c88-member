package com.c88.member.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "修改會員VIP")
public class DelMemberVipConfigForm {

    @Schema(title = "原本的VipId")
    private Integer sourceVipId;

    @Schema(title = "預計改成的vipId")
    private Integer targetVipId;

}
