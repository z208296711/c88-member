package com.c88.member.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum ValidEnum {

    UN_VALID(0, " 不驗證"),
    NEED_VALID(1, "需驗證");

    private final Integer value;

    private final String label;

    public static ValidEnum getEnum(Integer value) {
        return Arrays.stream(values()).filter(filter -> Objects.equals(filter.getValue(), value)).findFirst().orElseThrow();
    }

}
