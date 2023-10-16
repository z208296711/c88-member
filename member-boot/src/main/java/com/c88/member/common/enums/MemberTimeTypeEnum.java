package com.c88.member.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum MemberTimeTypeEnum {

    LOGIN(1, "登入時間"),
    REGISTER(2, "註冊時間"),
    NOT_LIMITED(3, "無限制時間");

    private final Integer code;

    private final String desc;

    public static MemberTimeTypeEnum getEnum(Integer code) {
        return Arrays.stream(values()).filter(filter -> Objects.equals(filter.getCode(), code)).findFirst().orElseThrow();
    }
}
