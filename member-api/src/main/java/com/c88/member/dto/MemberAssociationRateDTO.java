package com.c88.member.dto;


import lombok.Data;

import java.math.BigDecimal;

/**
 * (member_association_rate)实体类
 *
 * @author Allen
 * @since 2022-06-05 23:45:46
 * @description 由 Mybatisplus Code Generator 创建
 */
@Data
public class MemberAssociationRateDTO {
    private String username;
    /**
     * threshold
     */
    private BigDecimal threshold;
    /**
     * regIp
     */
    private BigDecimal regIp;
    /**
     * loginIp
     */
    private BigDecimal loginIp;
    /**
     * withdrawIp
     */
    private BigDecimal withdrawIp;

    private BigDecimal gameIp;
    /**
     * uuid
     */
    private BigDecimal uuid;
    /**
     * realName
     */
    private BigDecimal realName;
    /**
     * account
     */
    private BigDecimal account;
}