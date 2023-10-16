package com.c88.member.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 存送優惠類型
 */
@Getter
@AllArgsConstructor
public enum RechargeAwardModeEnum {

    RATE(1, "比例"),
    FIX(2, "固定");

    private final Integer code;

    private final String desc;

    public static RechargeAwardModeEnum getEnum(Integer code) {
        return Arrays.stream(values()).filter(filter -> Objects.equals(filter.getCode(), code)).findFirst().orElseThrow();
    }



}
