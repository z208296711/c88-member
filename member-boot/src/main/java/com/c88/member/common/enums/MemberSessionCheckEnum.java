package com.c88.member.common.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum MemberSessionCheckEnum {
    REG_IP(1, "註冊IP", "reg_ip","relation.Info01"),
    LOGIN_IP(2,"登入ip", "login_ip", "relation.Info02"),
    WITHDRAW_IP(3, "最後提款ip", "withdraw_ip", "relation.Info07"),
    GAME_IP(4, "最後遊戲ip", "game_ip", "relation.Info08"),
    UUID(5, "電腦標籤", "uuid", "relation.Info04"),
    REAL_NAME(6,"真實姓名", "real_name", "relation.Info03"),
    ACCOUNT(7, "相似帳號", "account", "relation.Info09");

    @JsonValue
    Integer value;
    String name;
    String dbName;
    String i18nName;

    public Integer getValue(){
        return value;
    }

    public String getName(){
        return name;
    }

    public String getDbName(){
        return dbName;
    }

    public String getI18nName() {
        return i18nName;
    }

    MemberSessionCheckEnum(int value, String name, String dbName, String i18nName) {
        this.value = value;
        this.name = name;
        this.dbName = dbName;
        this.i18nName = i18nName;
    }

    public static MemberSessionCheckEnum fromIntValue(int value){
        for (MemberSessionCheckEnum s : MemberSessionCheckEnum.values()){
            if(s.value==value)
                return s;
        }
        return null;
    }

}
