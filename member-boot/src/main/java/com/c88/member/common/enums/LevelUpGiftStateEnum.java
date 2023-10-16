package com.c88.member.common.enums;

import com.c88.common.core.base.IBaseEnum;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public enum LevelUpGiftStateEnum implements IBaseEnum<Integer> {

    UN_RECEIVE(0, "有資格, 尚未領取"),

    RECEIVED(1, "已領取"),

    NO_POSITION(2, "無資格");


    @Getter
    private Integer value;

    @Getter
    // @JsonValue //  表示对枚举序列化时返回此字段
    private String label;

    LevelUpGiftStateEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    private static final Map<Integer, LevelUpGiftStateEnum> map = Stream.of(values()).collect(Collectors.toMap(LevelUpGiftStateEnum::getValue, Function.identity()));

    public static LevelUpGiftStateEnum fromIntValue(int value) {
        return map.get(value);
    }

}
