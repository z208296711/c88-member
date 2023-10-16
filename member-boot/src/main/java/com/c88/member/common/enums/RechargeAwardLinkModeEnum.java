package com.c88.member.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 存送優惠關聯模式
 */
@Getter
@AllArgsConstructor
public enum RechargeAwardLinkModeEnum {

    NULL(0, "無"),
    HORIZONTAL(1, "水平"),
    VERTICAL(2, "垂直");

    private final Integer code;

    private final String desc;

    public static RechargeAwardLinkModeEnum getEnum(Integer code) {
        return Arrays.stream(values()).filter(filter -> Objects.equals(filter.getCode(), code)).findFirst().orElseThrow();
    }



}
