package com.c88.member.common.enums;

import com.c88.common.core.base.IBaseEnum;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public enum FreeBetGiftTypeEnum implements IBaseEnum<Integer> {

    WEEKLY(1, " 週"),

    MONTHLY(2, "月");



    @Getter
    private Integer value;

    @Getter
    // @JsonValue //  表示对枚举序列化时返回此字段
    private String label;

    FreeBetGiftTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    private static final Map<Integer, FreeBetGiftTypeEnum> map = Stream.of(values()).collect(Collectors.toMap(FreeBetGiftTypeEnum::getValue, Function.identity()));

    public static FreeBetGiftTypeEnum fromIntValue(int value) {
        return map.get(value);
    }

}
