package com.c88.member.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.c88.common.core.base.IBaseEnum;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public enum LevelUpReviewEnum implements IBaseEnum<Integer> {

    DO_NOT_REVIEW(0, "不用審核"),

    NEED_REVIEW(1, "需要審核");// 自動提升



    @Getter
    @EnumValue //  Mybatis-Plus 提供注解表示插入数据库时插入该值
    private Integer value;

    @Getter
    // @JsonValue //  表示对枚举序列化时返回此字段
    private String label;

    LevelUpReviewEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    private static final Map<Integer, LevelUpReviewEnum> map = Stream.of(values()).collect(Collectors.toMap(LevelUpReviewEnum::getValue, Function.identity()));

    public static LevelUpReviewEnum fromIntValue(int value) {
        return map.get(value);
    }

}
