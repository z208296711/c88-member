package com.c88.member.common.enums;

import com.c88.common.core.base.IBaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum FreeBetGiftStateEnum implements IBaseEnum<Integer> {

    UN_RECEIVE(0, " 尚未領取"),
    RECEIVED(1, "已領取"),
    NO_POSITION(2, " 尚不符合資格");

    private final Integer value;

    private final String label;

    private static final Map<Integer, FreeBetGiftStateEnum> map = Stream.of(values()).collect(Collectors.toMap(FreeBetGiftStateEnum::getValue, Function.identity()));

    public static FreeBetGiftStateEnum fromIntValue(int value) {
        return map.get(value);
    }

}
