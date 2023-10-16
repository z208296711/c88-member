package com.c88.member.pojo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MemberVipInfoVO {

    private Integer memberVipId;
    private Long memberId;
    private String username;
    private Integer currentVipId;
    private Integer previousVipId;
    private LocalDateTime levelUpTime;
    private LocalDateTime levelDownTime;

}
