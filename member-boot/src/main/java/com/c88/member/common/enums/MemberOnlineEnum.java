package com.c88.member.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum MemberOnlineEnum {

    OFFLINE(0, "離線"),
    ONLINE(1, "上線");

    private final Integer code;

    private final String desc;

    public static MemberOnlineEnum getEnum(Integer code) {
        return Arrays.stream(values()).filter(filter -> Objects.equals(filter.getCode(), code)).findFirst().orElseThrow();
    }
}
