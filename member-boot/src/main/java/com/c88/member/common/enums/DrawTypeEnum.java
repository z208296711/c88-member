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
public enum DrawTypeEnum implements IBaseEnum<Integer> {

    COMMON_DRAW(0, "一般抽獎");

    private final Integer value;

    private final String label;

    private static final Map<Integer, DrawTypeEnum> map = Stream.of(values()).collect(Collectors.toMap(DrawTypeEnum::getValue, Function.identity()));

    public static DrawTypeEnum fromIntValue(int value) {
        return map.get(value);
    }

}
