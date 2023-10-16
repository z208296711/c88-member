package com.c88.member.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 活動類型
 */
@Getter
@AllArgsConstructor
public enum PromotionTypeEnum {

    IN(1, "站內活動"),
    OUT(2, "外部活動");

    private final Integer code;

    private final String desc;

    public static PromotionTypeEnum getEnum(Integer code) {
        return Arrays.stream(values()).filter(filter -> Objects.equals(filter.getCode(), code)).findFirst().orElseThrow();
    }
}
