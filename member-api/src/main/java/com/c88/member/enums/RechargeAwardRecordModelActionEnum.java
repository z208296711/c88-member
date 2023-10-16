package com.c88.member.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 儲存存送優惠行為
 */
@Getter
@AllArgsConstructor
public enum RechargeAwardRecordModelActionEnum {

    ADD(1, "新增"),
    MODIFY(2, "修改");

    private final Integer code;

    private final String desc;

    public static RechargeAwardRecordModelActionEnum getEnum(Integer code) {
        return Arrays.stream(values()).filter(filter -> Objects.equals(filter.getCode(), code)).findFirst().orElseThrow();
    }
}
