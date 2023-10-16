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
public enum DrawTemplateConditionTypeEnum implements IBaseEnum<Integer> {

    FREE(0, "免費獲得" ),

    SINGLE_RECHARGE(1, "單筆充值"),

    TOTAL_RECHARGE(2, "累積充值"),

    COIN_EXCHANGE(3, "金幣兌換");



    private final Integer value;

    private final String label;

    private static final Map<Integer, DrawTemplateConditionTypeEnum> map = Stream.of(values()).collect(Collectors.toMap(DrawTemplateConditionTypeEnum::getValue, Function.identity()));

    public static DrawTemplateConditionTypeEnum fromIntValue(int value) {
        return map.get(value);
    }

}
