package com.c88.member.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "OperationDrawRecordForm")
public class OperationDrawRecordForm {

    @Schema(title = "state")
    private Integer state;

    @Schema(title = "inbox")
    private Integer inbox;

}