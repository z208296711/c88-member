package com.c88.member.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 
 * @TableName member_rebate_config
 */

@Data
public class MemberRebateConfigDTO implements Serializable {
    /**
     * 
     */
    private Long id;

    /**
     * 
     */
    private Integer vipId;

    /**
     * 
     */
    private Integer categoryId;

    /**
     * 
     */
    private LocalDateTime gmtCreate;

    /**
     * 
     */
    private LocalDateTime gmtModified;

    /**
     * 
     */
    private Double rebate;

    private static final long serialVersionUID = 1L;
}
