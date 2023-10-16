package com.c88.member.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class MemberSessionCheckDTO {
    private String username;
    private Integer checkItem;
    private List<?> connectNames;
    private BigDecimal rate;
    String checkItemValue;
}
