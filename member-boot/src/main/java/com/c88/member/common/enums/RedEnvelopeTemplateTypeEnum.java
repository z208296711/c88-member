package com.c88.member.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 白菜紅包類型
 */
@Getter
@AllArgsConstructor
public enum RedEnvelopeTemplateTypeEnum {

    CHINESE_CABBAGE(1, "白菜紅包"),
    RED_PACKETS(2, "紅包代碼");

    private final Integer code;

    private final String desc;

    public static RedEnvelopeTemplateTypeEnum getEnum(Integer code) {
        return Arrays.stream(values()).filter(filter -> Objects.equals(filter.getCode(), code)).findFirst().orElseThrow();
    }

}
