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
public enum DrawRecordStateEnum implements IBaseEnum<Integer> {

    PRE_APPLY(0, "待申請"),

    PRE_APPROVE(1, "待審核"),

    IN_PROGRESS(2, "處理中"),

    REJECT(3, "已拒絕"),

    SEND(4, "已發送");


    private final Integer value;

    private final String label;

    private static final Map<Integer, DrawRecordStateEnum> map = Stream.of(values()).collect(Collectors.toMap(DrawRecordStateEnum::getValue, Function.identity()));

    public static DrawRecordStateEnum fromIntValue(int value) {
        return map.get(value);
    }

}
