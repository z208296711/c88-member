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
public enum RedEnvelopeChineseCabbageReceiveStateEnum {

    RECEIVE(1, "立即領取"),
    RECEIVED(2, "已領取"),
    PENDING_REVIEW(3, "待審核"),
    REJECT(4, "審核未通過"),
    NO_POSITION(5, "尚不符合資格"),
    DAILY_RED_ENVELOPE_LIMIT(6, "每日領取上限"),
    RED_ENVELOPE_LIMIT(7, "累計領取上限"),
    MEMBER_DAILY_RED_ENVELOPE_LIMIT(8, "每人每日領取上限"),
    MEMBER_RED_ENVELOPE_LIMIT(9, "每人累計領取上限"),
    YESTERDAY_RECHARGE_AMOUNT_INSUFFICIENT(10, "昨日充值要求"),
    DAY_RECHARGE_AMOUNT_INSUFFICIENT(11, "當日充值要求"),
    TOTAL_RECHARGE_AMOUNT_INSUFFICIENT(12, "累積充值要求");

    private final Integer code;

    private final String desc;

    public static RedEnvelopeChineseCabbageReceiveStateEnum getEnum(Integer code) {
        return Arrays.stream(values()).filter(filter -> Objects.equals(filter.getCode(), code)).findFirst().orElseThrow();
    }

}
