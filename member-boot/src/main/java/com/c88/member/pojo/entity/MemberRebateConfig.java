package com.c88.member.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 
 * @TableName member_rebate_config
 */
@TableName(value ="member_rebate_config")
@Data
public class MemberRebateConfig implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    @TableField(value = "vip_id")
    private Integer vipId;

    /**
     * 
     */
    @TableField(value = "category_id")
    private Integer categoryId;

    /**
     * 
     */
    @TableField(value = "gmt_create")
    private LocalDateTime gmtCreate;

    /**
     * 
     */
    @TableField(value = "gmt_modified")
    private LocalDateTime gmtModified;

    /**
     * 
     */
    @TableField(value = "rebate")
    private Double rebate;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}