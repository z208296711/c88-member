package com.c88.member.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 白菜紅包領取狀態
 */
@Getter
@AllArgsConstructor
public enum RedEnvelopeRecordStateEnum {

    NULL(0, "無狀態"),
    PENDING_REVIEW(1, "待審核"),
    APPROVE(2, "通過"),
    REJECT(3, "拒絕");

    private final Integer code;

    private final String desc;

    public static RedEnvelopeRecordStateEnum getEnum(Integer code) {
        return Arrays.stream(values()).filter(filter -> Objects.equals(filter.getCode(), code)).findFirst().orElseThrow();
    }


}
