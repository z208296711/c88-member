package com.c88.member.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 白菜紅包代碼狀態
 */
@Getter
@AllArgsConstructor
public enum RedEnvelopeCodeStateEnum {

    UNUSED (0, "未使用"),
    USED(1, "已使用"),
    RECYCLED(2, "已回收");

    private final Integer code;

    private final String desc;

    public static RedEnvelopeCodeStateEnum getEnum(Integer code) {
        return Arrays.stream(values()).filter(filter -> Objects.equals(filter.getCode(), code)).findFirst().orElseThrow();
    }


}
