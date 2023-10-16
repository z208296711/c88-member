package com.c88.member.common.enums;

import com.c88.common.core.base.IBaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum BirthdayGiftStateEnum implements IBaseEnum<Integer> {

    UN_RECEIVE(0, " 尚未領取"),
    RECEIVED(1, "已領取"),
    NO_POSITION(2, " 尚不符合資格");

    private final Integer value;

    private final String label;

    public static BirthdayGiftStateEnum getEnum(Integer value) {
        return Arrays.stream(values()).filter(filter -> Objects.equals(filter.getValue(), value)).findFirst().orElseThrow();
    }

}
