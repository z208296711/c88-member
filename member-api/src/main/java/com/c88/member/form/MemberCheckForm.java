package com.c88.member.form;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class MemberCheckForm {
    @NotNull(message = "帳號不可空白")
    private String username;

    @NotNull
    private Integer type;
}
