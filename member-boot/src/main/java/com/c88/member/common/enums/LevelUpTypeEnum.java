package com.c88.member.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.c88.common.core.base.IBaseEnum;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public enum LevelUpTypeEnum implements IBaseEnum<Integer> {

    LEVEL_DOWN(0, "降級"),

    LEVEL_UP(1, "升級"),

    MANUAL(2, "手動");


    @Getter
    @EnumValue //  Mybatis-Plus 提供注解表示插入数据库时插入该值
    private Integer value;

    @Getter
    // @JsonValue //  表示对枚举序列化时返回此字段
    private String label;

    LevelUpTypeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    private static final Map<Integer, LevelUpTypeEnum> map = Stream.of(values()).collect(Collectors.toMap(LevelUpTypeEnum::getValue, Function.identity()));

    public static LevelUpTypeEnum fromIntValue(int value) {
        return map.get(value);
    }

}
