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
public enum LevelUpNotificationStateEnum implements IBaseEnum<Integer> {

    UN_RECEIVE(0, "尚未領取"),

    READ_VIP_AUTH(1, "查看權益");

    private final Integer value;

    private final String label;

    private static final Map<Integer, LevelUpNotificationStateEnum> map = Stream.of(values()).collect(Collectors.toMap(LevelUpNotificationStateEnum::getValue, Function.identity()));

    public static LevelUpNotificationStateEnum fromIntValue(Integer value) {
        return map.get(value);
    }


}
