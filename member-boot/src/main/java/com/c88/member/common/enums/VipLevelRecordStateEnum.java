package com.c88.member.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.c88.common.core.base.IBaseEnum;
import lombok.Getter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public enum VipLevelRecordStateEnum implements IBaseEnum<Integer> {
    //狀態 0:待審核 1:已保級 2:已降級 3:已升級
    PRE_APPROVE(0, "待審核"),

    KEEP_LEVEL(1, "已保級"),

    LEVEL_DOWN(2, "已降級"),

    LEVEL_UP(3, "已升級");


    @Getter
    @EnumValue //  Mybatis-Plus 提供注解表示插入数据库时插入该值
    private Integer value;

    @Getter
    // @JsonValue //  表示对枚举序列化时返回此字段
    private String label;

    VipLevelRecordStateEnum(Integer value, String label) {
        this.value = value;
        this.label = label;
    }

    private static final Map<Integer, VipLevelRecordStateEnum> map = Stream.of(values()).collect(Collectors.toMap(VipLevelRecordStateEnum::getValue, Function.identity()));

    public static VipLevelRecordStateEnum fromIntValue(int value) {
        return map.get(value);
    }

}
