package com.c88.member.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 白菜紅包領取紀錄時間類型
 */
@Getter
@AllArgsConstructor
public enum RedEnvelopeRecordTimeTypeEnum {

    TRANSACTION(1, "申請時間"),
    REVIEW(2, "審核時間");

    private final Integer code;

    private final String desc;

    public static RedEnvelopeRecordTimeTypeEnum getEnum(Integer code) {
        return Arrays.stream(values()).filter(filter -> Objects.equals(filter.getCode(), code)).findFirst().orElseThrow();
    }

}
