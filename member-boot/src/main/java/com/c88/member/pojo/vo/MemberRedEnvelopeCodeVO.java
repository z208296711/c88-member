package com.c88.member.pojo.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "紅包代碼")
public class MemberRedEnvelopeCodeVO {

    @JsonIgnore
    @Schema(title = "優惠模組ID")
    private Long templateId;

    @Schema(title = "會員帳號")
    private String username;

    @Schema(title = "使用狀態", description = "0未使用 1已使用 2已回收")
    private Integer state;

    @Schema(title = "審核狀態", description = "0空 1待審核 2已通過 3已拒絕")
    private Integer reviewState;

}
