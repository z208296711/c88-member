package com.c88.member.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(title = "UpdateDrawRecordAddressForm")
public class UpdateDrawRecordAddressForm {

    @Schema(title = "recordId")
    @NotNull
    private Long recordId;

    @Schema(title = "address")
    private String address;

    @Schema(title = "recipient")
    private String recipient;

    @Schema(title = "phone")
    private String phone;

}