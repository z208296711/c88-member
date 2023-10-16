package com.c88.member.dto;

import lombok.Data;

@Data
public class MemberVipConfigDTO {

    /**
     * vip Id
     */
    private Integer id;

    /**
     * vip名稱
     */
    private String name;

    private String dailyBackwaterLimit;


}
