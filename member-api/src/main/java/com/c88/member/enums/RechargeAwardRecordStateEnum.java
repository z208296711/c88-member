package com.c88.member.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

/**
 * 存送優惠領取紀錄狀態
 */
@Getter
@AllArgsConstructor
public enum RechargeAwardRecordStateEnum {

    NULL(-1, "無"),
    UNUSED(0, "未使用"),
    USED(1, "已使用"),
    CANCEL(2, "已取消");

    private final Integer code;

    private final String desc;

    public static RechargeAwardRecordStateEnum getEnum(Integer code) {
        return Arrays.stream(values()).filter(filter -> Objects.equals(filter.getCode(), code)).findFirst().orElseThrow();
    }
}
