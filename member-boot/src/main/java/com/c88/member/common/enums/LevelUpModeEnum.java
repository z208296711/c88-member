package com.c88.member.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.c88.common.core.base.IBaseEnum;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public enum LevelUpModeEnum implements IBaseEnum<Integer> {

    UNKNOWN(0, "未知"),

    AUTO(1, "vip.vipmod01"),// 自動提升

     MANUAL(2, "vip.vipmod02"); // 手動提升


    @Getter
    @EnumValue //  Mybatis-Plus 提供注解表示插入数据库时插入该值
    private Integer value;

    @Getter
    // @JsonValue //  表示对枚举序列化时返回此字段
    private String label;

    LevelUpModeEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    private static final Map<Integer, LevelUpModeEnum> map = Stream.of(values()).collect(Collectors.toMap(LevelUpModeEnum::getValue, Function.identity()));

    public static LevelUpModeEnum fromIntValue(int value) {
        return map.get(value);
    }

}
