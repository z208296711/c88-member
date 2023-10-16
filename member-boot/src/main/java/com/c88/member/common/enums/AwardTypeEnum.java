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
public enum AwardTypeEnum implements IBaseEnum<Integer> {

    NON(0, "未中獎"),

    BONUS(1, "紅利"),

    COIN(2, "金幣"),

    ENTITY(3, "實體"),

    RECHARGE_PROMOTION(4, "存送優惠");

    private final Integer value;

    private final String label;

    private static final Map<Integer, AwardTypeEnum> map = Stream.of(values()).collect(Collectors.toMap(AwardTypeEnum::getValue, Function.identity()));

    public static AwardTypeEnum fromIntValue(int value) {
        return map.get(value);
    }

}
