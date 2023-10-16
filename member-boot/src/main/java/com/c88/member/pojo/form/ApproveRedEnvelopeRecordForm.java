package com.c88.member.pojo.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(title = "白菜紅包獎勵通過表單")
public class ApproveRedEnvelopeRecordForm {

    @Schema(title = "白菜紅包領取紀錄ID")
    private List<Long> ids;

    @Schema(title = "發送站內信", description = "0不發送 1發送")
    private Integer sendInbox;

}
