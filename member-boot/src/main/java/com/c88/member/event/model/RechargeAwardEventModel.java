package com.c88.member.event.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RechargeAwardEventModel  {

    private Integer memberId;
    private String ip;
    private String username;
    private Integer platformId;
    private Integer platformGameId;
    private String platformCode;

    private LocalDateTime gmtCreate;

}
