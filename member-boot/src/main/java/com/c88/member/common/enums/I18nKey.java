package com.c88.member.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum I18nKey {

    A0213("提款密碼已存在"),
    A0214("舊密碼輸入錯誤"),
    A0215("新密碼與確認密碼不相同"),
    A0216("舊密碼與新密碼相同");

    private final String desc;

}
